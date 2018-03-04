package mbd.s3ackup.daemon.api.rest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.machinezoo.noexception.Exceptions;

import mbd.s3ackup.daemon.api.rest.dto.FileStatsResponse;
import mbd.s3ackup.daemon.api.rest.dto.ListFileResponse;
import mbd.s3ackup.daemon.api.rest.dto.ListFileResponse.FileObject;
import mbd.s3ackup.daemon.api.rest.dto.ListFileResponse.FileObject.SyncStatus;
import mbd.s3ackup.daemon.api.rest.dto.ListRootDirectoriesResponse;
import mbd.s3ackup.daemon.api.rest.dto.ListRootDirectoriesResponse.RootDirectory;
import mbd.s3ackup.daemon.api.rest.dto.ListTasksResponse;
import mbd.s3ackup.daemon.api.rest.dto.TaskResponse;
import mbd.s3ackup.daemon.cloud.CachedCloudStorage;
import mbd.s3ackup.daemon.cloud.CloudDirectory;
import mbd.s3ackup.daemon.cloud.CloudDirectoryAttributes;
import mbd.s3ackup.daemon.cloud.CloudFile;
import mbd.s3ackup.daemon.cloud.CloudPath;
import mbd.s3ackup.daemon.cloud.CloudRoot;
import mbd.s3ackup.daemon.cloud.NotCalculatableException;
import mbd.s3ackup.daemon.cloud.executor.CloudEventTask;
import mbd.s3ackup.daemon.cloud.executor.CloudEventTaskKey.CloudAction;
import mbd.s3ackup.daemon.cloud.executor.CloudTaskExecutor;
import mbd.s3ackup.daemon.local.LocalFileSystemCloudBridge;
import mbd.s3ackup.daemon.local.PathUtils;
import mbd.s3ackup.daemon.local.settings.AppPreferences;

@Service
public class RestApiService {

	@Autowired
	private LocalFileSystemCloudBridge localFileSystemCloudBridge;

	@Autowired
	private AppPreferences appPreferences;

	@Autowired
	@Qualifier("cachedCloudStorage")
	private CachedCloudStorage cloudStorage;

	@Autowired
	private CloudTaskExecutor cloudTaskExecutor;

	public ListRootDirectoriesResponse getRootDirectories() {
		List<CloudRoot> listRootDirectories = cloudStorage.listRootDirectories();
		listRootDirectories.forEach(Exceptions.sneak().consumer(cloudRoot -> {
			if (appPreferences.isRootEnabledOnLocal(cloudRoot)) {
				localFileSystemCloudBridge.mkRootDirs(cloudRoot);
			}
			else {
				// remove directories
				localFileSystemCloudBridge.rmRootDirs(cloudRoot);
			}
		}));
		List<RootDirectory> collect = listRootDirectories.stream()
				.map(this::mapToRootDirectory).collect(Collectors.toList());
		ListRootDirectoriesResponse response = new ListRootDirectoriesResponse();
		response.setRootDirectoryList(collect);
		return response;
	}

	private RootDirectory mapToRootDirectory(CloudRoot cloudRoot) {
		RootDirectory out = new RootDirectory();
		out.setName(cloudRoot.getName());
		out.setDtCreated(cloudRoot.getDtCreated());
		out.setEnabled(appPreferences.isRootEnabledOnLocal(cloudRoot));
		return out;
	}

	ListRootDirectoriesResponse enableRootDirectory(Set<String> rootDirectorySet) {
		if (rootDirectorySet != null && rootDirectorySet.size() > 0) {
			rootDirectorySet.forEach(item -> {
				appPreferences.enabledRootOnLocal(new CloudRoot(item), true);
			});
		}
		return getRootDirectories();
	}

	ListRootDirectoriesResponse disableRootDirectory(Set<String> rootDirectorySet) {
		if (rootDirectorySet != null && rootDirectorySet.size() > 0) {
			rootDirectorySet.forEach(item -> {
				appPreferences.enabledRootOnLocal(new CloudRoot(item), false);
			});
		}
		return getRootDirectories();
	}

	ListFileResponse getFileList(String path, boolean refresh) {
		refresh = refresh && cloudStorage.clearCache();

		String root = PathUtils.extractRootFromPath(path);
		String prefix = PathUtils.removeRootFromPath(path);
		List<FileObject> collect = cloudStorage
				.listFiles(new CloudDirectory(new CloudRoot(root), prefix)).stream()
				.map(this::mapToFileObject).collect(Collectors.toList());
		ListFileResponse out = new ListFileResponse();
		out.setFileList(collect);
		return out;
	}

	private FileObject mapToFileObject(CloudPath cloudPath) {
		Path fullPath = Paths.get(cloudPath.getRoot().getName(), cloudPath.getPath());
		Path fileName = Paths.get(cloudPath.getPath()).getFileName();
		if (cloudPath.isDirectory()) {
			ListFileResponse.Directory out = new ListFileResponse.Directory(
					FilenameUtils.separatorsToUnix(fullPath.toString()),
					fileName.toString());
			return out;
		}
		else {
			CloudFile file = (CloudFile) cloudPath;
			ListFileResponse.File out = new ListFileResponse.File(
					FilenameUtils.separatorsToUnix(fullPath.toString()),
					fileName.toString(), file.getLastModified(), file.getSize(),
					file.getStorageType());

			// check if file exists on local
			String hash = localFileSystemCloudBridge.getHashOfLocalFile(cloudPath);
			if (hash == null) {
				out.setSyncStatus(SyncStatus.CLOUD_ONLY);
			}
			else {
				if (hash.equals(file.getHash())) {
					out.setSyncStatus(SyncStatus.HASH_EQUAL);
				}
				else {
					out.setSyncStatus(SyncStatus.HASH_NOT_EQUAL);
				}
			}
			return out;
		}
	}

	FileStatsResponse getDirectoryStats(String dir, boolean forceCalculatable) {
		String root = PathUtils.extractRootFromPath(dir);
		String prefix = PathUtils.removeRootFromPath(dir);
		CloudDirectoryAttributes directoryAttributes;
		FileStatsResponse out = new FileStatsResponse();
		out.setPath(dir);
		try {
			directoryAttributes = cloudStorage.getDirectoryAttributes(
					new CloudDirectory(new CloudRoot(root), prefix), forceCalculatable);
			out.setCalculatable(true);
			out.setCount(directoryAttributes.getCount());
			out.setSize(directoryAttributes.getSize());
			out.setDtLastModified(directoryAttributes.getLastModified());
			out.setCountByStorageType(directoryAttributes.getCountByStorageType());
			out.setSizeByStorageType(directoryAttributes.getSizeByStorageType());
		}
		catch (NotCalculatableException e) {
			out.setCalculatable(false);
			out.setUnCalculatableMessage(e.getMessage());
		}
		return out;
	}

	List<FileStatsResponse> getDirectoriesStats(Set<String> pathsSet,
			boolean forceCalculatable, boolean refresh) {
		refresh = refresh && cloudStorage.clearCache();
		return pathsSet.stream().map(prefix -> {
			return getDirectoryStats(prefix, forceCalculatable);
		}).collect(Collectors.toList());
	}

	int downloadPaths(Set<String> pathsSet) {
		List<CloudEventTask> collect = pathsSet.stream().flatMap(item -> {
			CloudRoot root = new CloudRoot(PathUtils.extractRootFromPath(item));
			String path = PathUtils.removeRootFromPath(item);
			CloudDirectory prefix = new CloudDirectory(root, path);
			List<CloudPath> listFilesRecursive = cloudStorage.listFilesRecursive(prefix);
			return listFilesRecursive.stream();
		}).map(item -> {
			String localPath = Paths.get(localFileSystemCloudBridge
					.calculateLocalRoot(item.getRoot()).toString(), item.getPath())
					.toString();
			CloudAction cloudAction = CloudAction.DOWNLOAD;
			CloudFile cloudFile = new CloudFile(item.getRoot(), item.getPath());
			return new CloudEventTask(localPath, cloudAction, cloudFile);
		}).collect(Collectors.toList());

		collect.forEach(item -> cloudTaskExecutor.submitTask(item));
		return collect.size();
	}

	int deletePaths(Set<String> pathsSet) {
		List<CloudEventTask> collect = pathsSet.stream().flatMap(item -> {
			CloudRoot root = new CloudRoot(PathUtils.extractRootFromPath(item));
			String path = PathUtils.removeRootFromPath(item);
			CloudDirectory prefix = new CloudDirectory(root, path);
			List<CloudPath> listFilesRecursive = cloudStorage.listFilesRecursive(prefix);
			return listFilesRecursive.stream();
		}).map(item -> {
			CloudAction cloudAction = CloudAction.DELETE;
			CloudFile cloudFile = new CloudFile(item.getRoot(), item.getPath());
			return new CloudEventTask(null, cloudAction, cloudFile);
		}).collect(Collectors.toList());
		collect.forEach(item -> cloudTaskExecutor.submitTask(item));
		return collect.size();
	}

	public ListTasksResponse ListTasks() {
		Set<CloudEventTask> set = cloudTaskExecutor.getProcessingTaskIdSet();

		List<TaskResponse> collect = set.stream().map(item -> {
			return new TaskResponse(item.getId(), item.getCloudAction(),
					item.getProgress(), item.getLocalPath(),
					String.format("%s/%s", item.getCloudFile().getRoot().getName(),
							item.getCloudFile().getPath()));
		}).collect(Collectors.toList());

		ListTasksResponse out = new ListTasksResponse(collect);
		return out;
	}

}
