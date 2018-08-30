package mbd.s3ackup.daemon.cloud.executor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import mbd.s3ackup.daemon.cloud.CachedCloudStorage;
import mbd.s3ackup.daemon.cloud.executor.CloudEventTask.Progress;
import mbd.s3ackup.daemon.cloud.executor.CloudEventTaskKey.CloudAction;

@Service
public class CloudTaskExecutor {

	private static final Logger log = LoggerFactory.getLogger(CloudTaskExecutor.class);

	// Using hash set in case of duplicate events which sometimes happens
	private Set<CloudEventTask> cloudEventTaskQueue = Collections.synchronizedSet(new LinkedHashSet<>());

	@Autowired
	@Qualifier("cloudActionExecutor")
	private Executor executor;

	@Autowired
	private CachedCloudStorage cloudStorage;

	private final AtomicInteger counter = new AtomicInteger(0);
	private final Map<Integer, CloudEventTask> processingTasks = new ConcurrentHashMap<>();
	private final Cache<Integer, CloudEventTask> completedTasks = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();

	public Set<CloudEventTask> getProcessingTaskIdSet() {
		Set<CloudEventTask> out = new HashSet<>();
		out.addAll(processingTasks.values());
		out.addAll(completedTasks.asMap().values());
		return out;
	}

	public void submitTask(CloudEventTask cloudEventTask) {
		if (isEventValid(cloudEventTask)) {
			if (cloudEventTaskQueue.add(cloudEventTask)) {
				int id = counter.incrementAndGet();
				cloudEventTask.setId(id);
				processingTasks.put(id, cloudEventTask);
			}
		}
		else {
			log.debug("ignoring task [{}]", cloudEventTask);
		}
	}

	@Scheduled(fixedDelay = 500)
	private void processQueue() {
		if (cloudEventTaskQueue.size() > 0) {
			Iterator<CloudEventTask> iterator = cloudEventTaskQueue.iterator();
			while (iterator.hasNext()) {
				try {
					CloudEventTask task = iterator.next();
					executor.execute(() -> {
						try {
							task.setProgress(Progress.processing);
							switch (task.getCloudAction()) {
							case UPLOAD:
								processUpload(task);
								break;
							case DOWNLOAD:
								try {
									notifyObserverOfDownload(task);
									processDownload(task);
								}
								finally {
									notifyObserverOfDownloadComplete(task);
								}
								break;
							case DELETE:
								processDelete(task);
								break;
							default:
								throw new IllegalArgumentException();
							}
						}
						catch (Exception e) {
							log.error("error executing s3 upload task", e);
						}
						finally {
							task.setProgress(Progress.complete);
							processingTasks.remove(task.getId());
							completedTasks.put(task.getId(), task);
						}
					});
					iterator.remove();
				}
				catch (RejectedExecutionException e) {
					// executor queue full - try again later
					return;
				}
			}
		}
	}

	private void processUpload(CloudEventTask task) {
		Path localPath = Paths.get(task.getLocalPath());
		if (Files.exists(localPath)) {
			cloudStorage.uploadFile(localPath, task.getCloudFile());
			cloudStorage.clearCache();
		}
		else {
			log.error("file does not exists[{}]", localPath);
		}
	}

	private void processDownload(CloudEventTask task) {
		Path localPath = Paths.get(task.getLocalPath());
		cloudStorage.downloadFile(localPath, task.getCloudFile());
		cloudStorage.clearCache();
	}

	private void processDelete(CloudEventTask task) {
		cloudStorage.deleteFile(task.getCloudFile());
		cloudStorage.clearCache();
	}

	/**
	 * The File watcher will submit CREATE events when we DOWNLOAD files. We need to
	 * ignore these events. Keep in mind that the events may be submitted sometime after
	 * they occurred (500 ms)
	 */
	private Map<String, CloudEventTask> uploadProcessingMap = new ConcurrentHashMap<>();
	private final Cache<String, CloudEventTask> uploadProcessingMapWithExpire = Caffeine.newBuilder().expireAfterWrite(2, TimeUnit.SECONDS).maximumSize(500).build();

	private void notifyObserverOfDownload(CloudEventTask task) {
		uploadProcessingMap.put(task.getLocalPath(), task);
	}

	private void notifyObserverOfDownloadComplete(CloudEventTask task) {
		uploadProcessingMap.remove(task.getLocalPath());
		uploadProcessingMapWithExpire.put(task.getLocalPath(), task);

	}

	private boolean isEventValid(CloudEventTask task) {
		if (CloudAction.UPLOAD.equals(task.getCloudAction())) {
			return !(uploadProcessingMap.containsKey(task.getLocalPath()) || uploadProcessingMapWithExpire.getIfPresent(task.getLocalPath()) != null);
		}
		return true;
	}
}
