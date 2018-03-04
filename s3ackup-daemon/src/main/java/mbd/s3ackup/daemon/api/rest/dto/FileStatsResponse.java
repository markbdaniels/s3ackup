package mbd.s3ackup.daemon.api.rest.dto;

import java.util.Date;
import java.util.Map;

import mbd.s3ackup.daemon.cloud.CloudFile.StorageType;

public class FileStatsResponse {

	private boolean isCalculatable;
	private String unCalculatableMessage;
	private String path;
	private long size;
	private long count;
	private Date dtLastModified;

	private Map<StorageType, Long> countByStorageType;
	private Map<StorageType, Long> sizeByStorageType;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public Date getDtLastModified() {
		return dtLastModified;
	}

	public void setDtLastModified(Date dtLastModified) {
		this.dtLastModified = dtLastModified;
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

	public boolean isCalculatable() {
		return isCalculatable;
	}

	public void setCalculatable(boolean isCalculatable) {
		this.isCalculatable = isCalculatable;
	}

	public String getUnCalculatableMessage() {
		return unCalculatableMessage;
	}

	public void setUnCalculatableMessage(String unCalculatableMessage) {
		this.unCalculatableMessage = unCalculatableMessage;
	}

	@Override
	public String toString() {
		return "FileStatsResponse [path=" + path + ", size=" + size + ", count=" + count + ", dtLastModified="
				+ dtLastModified + ", countByStorageType=" + countByStorageType + ", sizeByStorageType="
				+ sizeByStorageType + "]";
	}

}
