package mbd.s3ackup.daemon.cloud;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import mbd.s3ackup.daemon.cloud.CloudFile.StorageType;
import mbd.s3ackup.daemon.util.DateUtil;

public class CloudDirectoryAttributes {

	private long count = 0L;
	private long size = 0L;
	private Date lastModified;
	private StorageType storageType;

	private Map<StorageType, Long> countByStorageType = new TreeMap<>();
	private Map<StorageType, Long> sizeByStorageType = new TreeMap<>();

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
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

	public Map<StorageType, Long> getCountByStorageType() {
		return countByStorageType;
	}

	public void setCountByStorageType(Map<StorageType, Long> countByStorageType) {
		this.countByStorageType = countByStorageType;
	}

	public Map<StorageType, Long> getSizeByStorageType() {
		return sizeByStorageType;
	}

	public void setSizeByStorageType(Map<StorageType, Long> sizeByStorageType) {
		this.sizeByStorageType = sizeByStorageType;
	}

	@Override
	public String toString() {
		return "CloudDirectoryAttributes [count=" + count + ", size=" + size + ", lastModified=" + lastModified
				+ ", storageType=" + storageType + ", countByStorageType=" + countByStorageType + ", sizeByStorageType="
				+ sizeByStorageType + "]";
	}

	public void incrementStats(StorageType storageType, long size, Date lastModified) {
		this.count++;
		this.size += size;
		this.lastModified = DateUtil.returnNewestDate(this.lastModified, lastModified);

		if (this.storageType == null) {
			this.setStorageType(storageType);
		}
		else if (this.storageType != null && !this.storageType.equals(storageType)) {
			// there are files with mixed storage types - null it out as it
			// is not applicable
			this.storageType = null;
		}

		Long countByType = countByStorageType.get(storageType);
		countByStorageType.put(storageType, countByType == null ? 1 : countByType + 1);

		Long sizeByType = sizeByStorageType.get(storageType);
		sizeByStorageType.put(storageType, sizeByType == null ? size : sizeByType + size);
	}
}
