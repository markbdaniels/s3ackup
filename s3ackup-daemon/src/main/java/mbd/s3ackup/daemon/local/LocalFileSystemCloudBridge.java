package mbd.s3ackup.daemon.local;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.machinezoo.noexception.Exceptions;

import mbd.s3ackup.daemon.cloud.CloudDirectory;
import mbd.s3ackup.daemon.cloud.CloudFile;
import mbd.s3ackup.daemon.cloud.CloudPath;
import mbd.s3ackup.daemon.cloud.CloudRoot;
import mbd.s3ackup.daemon.cloud.executor.CloudEventTask;
import mbd.s3ackup.daemon.cloud.executor.CloudEventTaskKey.CloudAction;
import mbd.s3ackup.daemon.cloud.executor.CloudTaskExecutor;
import mbd.s3ackup.daemon.local.settings.AppPreferences;

@Service
public class LocalFileSystemCloudBridge {

	private static final Logger log = LoggerFactory.getLogger(LocalFileSystemCloudBridge.class);

	@Autowired
	private AppPreferences appPreferences;

	@Autowired
	private CloudTaskExecutor cloudTaskExecutor;

	public void mkRootDirs(CloudRoot cloudRoot) throws IOException, InterruptedException {
		Path full = calculateLocalRoot(cloudRoot);
		Files.createDirectories(full);
		setupFileWatcher(cloudRoot);
	}

	public void mkChildDirs(CloudRoot cloudRoot, List<CloudDirectory> dirs) {
		Path full = calculateLocalRoot(cloudRoot);
		dirs.forEach(Exceptions.sneak().consumer(item -> {
			Path absoluteDir = full.resolve(item.getPath());
			Files.createDirectories(absoluteDir);
		}));

	}

	public void rmRootDirs(CloudRoot cloudRoot) throws IOException {
		destroyFileWatcher(cloudRoot);
	}

	public Path calculateLocalRoot(CloudRoot cloudRoot) {
		Path localDir = appPreferences.getLocalDir();
		Path full = localDir.resolve(Paths.get(cloudRoot.getName()));
		return full;
	}

	/**
	 * File watcher
	 */
	private Map<CloudRoot, FileWatchService> fileWatchServiceMap = new HashMap<>();

	private void setupFileWatcher(CloudRoot cloudRoot) {
		Consumer<Path> saveFunction = path -> {
			processEvent(CloudAction.UPLOAD, path);
		};

		// destroy old one after creating new one
		FileWatchService oldFileWatchService = fileWatchServiceMap.remove(cloudRoot);

		Path rootDir = calculateLocalRoot(cloudRoot);
		try {
			FileWatchService fileWatchService = new FileWatchService(rootDir, saveFunction);
			fileWatchServiceMap.put(cloudRoot, fileWatchService);
		}
		catch (IOException e) {
			log.error("unabled to start file watcher service on dir[" + rootDir + "]", e);
		}

		destroyFileWatcher(oldFileWatchService);
	}

	private void destroyFileWatcher(CloudRoot cloudRoot) {
		FileWatchService fileWatchService = fileWatchServiceMap.remove(cloudRoot);
		destroyFileWatcher(fileWatchService);
	}

	private void destroyFileWatcher(FileWatchService fileWatchService) {
		if (fileWatchService != null) {
			fileWatchService.destroy();
		}
	}

	private void processEvent(CloudAction cloudAction, Path localPath) {
		Path localDir = appPreferences.getLocalDir();
		if (!localPath.startsWith(localDir)) {
			log.warn("expected path[{}] to start with [{}]", localPath, localDir);
			throw new IllegalArgumentException("expected path [" + localPath + "] to start with [" + localDir + "]");
		}
		Path fullcloudPath = localDir.relativize(localPath);
		String root = PathUtils.extractRootFromPath(fullcloudPath.toString());
		String prefix = PathUtils.removeRootFromPath(fullcloudPath.toString());
		CloudRoot cloudRoot = new CloudRoot(root);

		if (cloudAction == CloudAction.UPLOAD && Files.isDirectory(localPath)) {
			// new directory - need to recreate the file watcher.
			fileWatcherSetupQueue.add(cloudRoot);
			return;
		}

		CloudEventTask cloudEventTask = new CloudEventTask(localPath.toString(), cloudAction,
				new CloudFile(cloudRoot, prefix));
		cloudTaskExecutor.submitTask(cloudEventTask);
	}

	private Set<CloudRoot> fileWatcherSetupQueue = Collections.synchronizedSet(new LinkedHashSet<>());

	@Scheduled(fixedDelay = 1000)
	private void processFileWatcherQueue() {
		if (fileWatcherSetupQueue.size() > 0) {
			Iterator<CloudRoot> iterator = fileWatcherSetupQueue.iterator();
			while (iterator.hasNext()) {
				CloudRoot cloudRoot = iterator.next();
				log.info("resetting file watcher on dir[{}]", cloudRoot);
				setupFileWatcher(cloudRoot);
				iterator.remove();
			}
		}
	}

	public String getHashOfLocalFile(CloudPath cloudPath) {
		Path localPath = Paths.get(calculateLocalRoot(cloudPath.getRoot()).toString(), cloudPath.getPath());
		return PathUtils.calculateMd5hash(localPath);
	}
}
