package mbd.s3ackup.daemon.api.rest.dto;

public class LoginRequest {

	private String accessKey;
	private String secretKey;
	private boolean rememberMe;
	private String pin;

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	@Override
	public String toString() {
		return "LoginRequest [accessKey=" + accessKey + ", secretKey=" + secretKey + ", rememberMe=" + rememberMe
				+ ", pin=" + pin + "]";
	}
}
