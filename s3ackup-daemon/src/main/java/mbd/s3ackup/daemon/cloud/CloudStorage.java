package mbd.s3ackup.daemon.cloud;

import java.nio.file.Path;
import java.util.List;

import mbd.s3ackup.daemon.cloud.CloudFile.StorageType;

public interface CloudStorage {

	List<CloudRoot> listRootDirectories();

	List<CloudPath> listFiles(CloudDirectory prefix);

	List<CloudPath> listFilesRecursive(CloudDirectory prefix);

	List<CloudDirectory> listDirectoriesRecursive(CloudDirectory prefix);

	CloudDirectoryAttributes getDirectoryAttributes(CloudDirectory prefix, boolean forceCalculatable)
			throws NotCalculatableException;

	void uploadFile(Path localPath, CloudPath cloudFile);

	void downloadFile(Path localPath, CloudFile cloudPath);

	void deleteFile(CloudPath cloudFile);

	void changeStorageClass(CloudFile cloudfile, StorageType storageType);
}
