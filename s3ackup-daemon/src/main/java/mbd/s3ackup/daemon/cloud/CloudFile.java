package mbd.s3ackup.daemon.cloud;

import java.util.Date;

public final class CloudFile extends CloudPath {

	private String hash;
	private Date lastModified;
	private StorageType storageType;
	private Long size;

	public CloudFile(CloudRoot root, String path) {
		super(root, path);
	}

	public enum StorageType {
		STANDARD, STANDARD_IA, REDUCED_REDUNDANCY, GLACIER
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public StorageType getStorageType() {
		return storageType;
	}

	public void setStorageType(StorageType storageType) {
		this.storageType = storageType;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "CloudFile [hash=" + hash + ", lastModified=" + lastModified + ", storageType=" + storageType + ", size="
				+ size + "]";
	}

}