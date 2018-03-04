package mbd.s3ackup.daemon.api.rest.dto;

import java.util.Date;
import java.util.List;

import mbd.s3ackup.daemon.cloud.CloudFile;

public class ListFileResponse {

	private List<FileObject> fileList;

	public List<FileObject> getFileList() {
		return fileList;
	}

	public void setFileList(List<FileObject> fileList) {
		this.fileList = fileList;
	}

	public static class FileObject {

		public enum Type {
			Directory, File
		};

		public enum SyncStatus {
			CLOUD_ONLY, LOCAL_ONLY, HASH_EQUAL, HASH_NOT_EQUAL
		};

		private String name;
		private String path;
		private Type type;
		private Date dtLastModified;
		private Long size;
		private CloudFile.StorageType storageType;
		private SyncStatus syncStatus;

		public FileObject(Type type, String path, String name) {
			super();
			this.path = path;
			this.type = type;
			this.name = name;
		}

		public FileObject(Type type, String path, String name, Date dtLastModified, Long size,
				CloudFile.StorageType storageType) {
			this(type, path, name);
			this.dtLastModified = dtLastModified;
			this.size = size;
			this.storageType = storageType;
		}

		public Type getType() {
			return type;
		}

		public String getName() {
			return name;
		}

		public String getPath() {
			return path;
		}

		public Date getDtLastModified() {
			return dtLastModified;
		}

		public Long getSize() {
			return size;
		}

		public CloudFile.StorageType getStorageType() {
			return storageType;
		}

		public SyncStatus getSyncStatus() {
			return syncStatus;
		}

		public void setDtLastModified(Date dtLastModified) {
			this.dtLastModified = dtLastModified;
		}

		public void setSyncStatus(SyncStatus syncStatus) {
			this.syncStatus = syncStatus;
		}

	}

	public static class File extends FileObject {
		public File(String path, String name, Date dtLastModified, Long size, CloudFile.StorageType storageType) {
			super(Type.File, path, name, dtLastModified, size, storageType);
		}
	}

	public static class Directory extends FileObject {
		public Directory(String path, String name) {
			super(Type.Directory, path, name);
		}
	}

}
