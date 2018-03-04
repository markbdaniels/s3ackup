package mbd.s3ackup.daemon.cloud;

import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import mbd.s3ackup.daemon.cloud.CloudFile.StorageType;

@Service
public class CachedCloudStorage implements CloudStorage {

	@Autowired
	private CloudStorage cloudStorage;

	@Override
	@Cacheable(value = "default", sync = false)
	public List<CloudRoot> listRootDirectories() {
		return cloudStorage.listRootDirectories();
	}

	@Override
	@Cacheable(value = "default", sync = false)
	public List<CloudPath> listFiles(CloudDirectory prefix) {
		return cloudStorage.listFiles(prefix);
	}

	@Override
	public List<CloudPath> listFilesRecursive(CloudDirectory prefix) {
		return cloudStorage.listFilesRecursive(prefix);
	}

	@Override
	@Cacheable(value = "default", sync = false)
	public List<CloudDirectory> listDirectoriesRecursive(CloudDirectory prefix) {
		return cloudStorage.listDirectoriesRecursive(prefix);
	}

	@Override
	@Cacheable(value = "default", sync = false)
	public CloudDirectoryAttributes getDirectoryAttributes(CloudDirectory prefix, boolean forceCalculatable)
			throws NotCalculatableException {
		return cloudStorage.getDirectoryAttributes(prefix, forceCalculatable);
	}

	@Override
	public void uploadFile(Path localPath, CloudPath cloudFile) {
		cloudStorage.uploadFile(localPath, cloudFile);
	}

	@Override
	public void downloadFile(Path localPath, CloudFile cloudPath) {
		cloudStorage.downloadFile(localPath, cloudPath);
	}

	@Override
	public void deleteFile(CloudPath cloudFile) {
		cloudStorage.deleteFile(cloudFile);
	}

	@CacheEvict(value = "default", allEntries = true)
	public boolean clearCache() {
		return true;
	}

	@Override
	public void changeStorageClass(CloudFile cloudfile, StorageType storageType) {
		cloudStorage.changeStorageClass(cloudfile, storageType);
	}

}
