package mbd.s3ackup.daemon.api.rest.dto;

public class LoginTypeResponse {

	private boolean loginCredentialsSavedLocally;
	private String accessKey;
	private String secretKey;

	public boolean isLoginCredentialsSavedLocally() {
		return loginCredentialsSavedLocally;
	}

	public void setLoginCredentialsSavedLocally(boolean loginCredentialsSavedLocally) {
		this.loginCredentialsSavedLocally = loginCredentialsSavedLocally;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	@Override
	public String toString() {
		return "LoginStatusResponse [loginCredentialsSavedLocally=" + loginCredentialsSavedLocally + ", accessKey="
				+ accessKey + ", secretKey=" + secretKey + "]";
	}
}
