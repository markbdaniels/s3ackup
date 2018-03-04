package mbd.s3ackup.daemon.api.rest.dto;

import mbd.s3ackup.daemon.cloud.executor.CloudEventTask.Progress;
import mbd.s3ackup.daemon.cloud.executor.CloudEventTaskKey.CloudAction;

public class TaskResponse {

	private int id;
	private Progress progress;
	private int progressPct;
	private CloudAction cloudAction;
	private String localPath;
	private String cloudPath;

	public TaskResponse() {
	}

	public TaskResponse(int id, CloudAction cloudAction, Progress progress, String localPath, String cloudPath) {
		super();
		this.id = id;
		this.cloudAction = cloudAction;
		this.localPath = localPath;
		this.cloudPath = cloudPath;
		this.setProgress(progress);
	}

	public CloudAction getCloudAction() {
		return cloudAction;
	}

	public void setCloudAction(CloudAction cloudAction) {
		this.cloudAction = cloudAction;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getCloudPath() {
		return cloudPath;
	}

	public void setCloudPath(String cloudPath) {
		this.cloudPath = cloudPath;
	}

	public Progress getProgress() {
		return progress;
	}

	public void setProgress(Progress progress) {
		this.progress = progress;
		switch (progress) {
		case pending:
			this.setProgressPct(0);
			break;
		case processing:
			this.setProgressPct(50);
			break;
		case complete:
			this.setProgressPct(100);
			break;
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProgressPct() {
		return progressPct;
	}

	public void setProgressPct(int progressPct) {
		this.progressPct = progressPct;
	}

	@Override
	public String toString() {
		return "TaskResponse [id=" + id + ", progress=" + progress + ", progressPct=" + progressPct + ", cloudAction="
				+ cloudAction + ", localPath=" + localPath + ", cloudPath=" + cloudPath + "]";
	}
}