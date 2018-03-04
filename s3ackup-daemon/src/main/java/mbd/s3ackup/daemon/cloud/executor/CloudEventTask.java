package mbd.s3ackup.daemon.cloud.executor;

import mbd.s3ackup.daemon.cloud.CloudFile;

public class CloudEventTask extends CloudEventTaskKey {

	public enum Progress {
		pending, processing, complete
	}

	private Integer id;
	private CloudFile cloudFile;
	private Progress progress;

	public CloudEventTask(String localPath, CloudAction cloudAction, CloudFile cloudFile) {
		super(localPath, cloudAction, cloudFile);
		this.cloudFile = cloudFile;
		this.progress = Progress.pending;
	}

	public CloudFile getCloudFile() {
		return cloudFile;
	}

	public void setCloudFile(CloudFile cloudFile) {
		this.cloudFile = cloudFile;
	}

	public Progress getProgress() {
		return progress;
	}

	public void setProgress(Progress progress) {
		this.progress = progress;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "CloudEventTask [id=" + id + ", cloudFile=" + cloudFile + ", progress=" + progress + "]";
	}

}