package mbd.s3ackup.daemon.cloud.executor;

import mbd.s3ackup.daemon.cloud.CloudPath;

public class CloudEventTaskKey {

	public enum CloudAction {
		DOWNLOAD, UPLOAD, DELETE
	}

	private CloudAction cloudAction;
	private String localPath;
	private CloudPath cloudPath;

	public CloudEventTaskKey(String localPath, CloudAction cloudAction, CloudPath cloudPath) {
		super();
		this.localPath = localPath;
		this.cloudAction = cloudAction;
		this.cloudPath = cloudPath;
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

	public CloudPath getCloudPath() {
		return cloudPath;
	}

	public void setCloudPath(CloudPath cloudPath) {
		this.cloudPath = cloudPath;
	}

	@Override
	public String toString() {
		return "CloudEventTaskKey [cloudAction=" + cloudAction + ", localPath=" + localPath + ", cloudPath=" + cloudPath
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cloudAction == null) ? 0 : cloudAction.hashCode());
		result = prime * result + ((cloudPath == null) ? 0 : cloudPath.hashCode());
		result = prime * result + ((localPath == null) ? 0 : localPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CloudEventTaskKey other = (CloudEventTaskKey) obj;
		if (cloudAction != other.cloudAction)
			return false;
		if (cloudPath == null) {
			if (other.cloudPath != null)
				return false;
		}
		else if (!cloudPath.equals(other.cloudPath))
			return false;
		if (localPath == null) {
			if (other.localPath != null)
				return false;
		}
		else if (!localPath.equals(other.localPath))
			return false;
		return true;
	}

}