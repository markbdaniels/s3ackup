package mbd.s3ackup.daemon.api.rest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import mbd.s3ackup.daemon.api.rest.dto.LoginRequest;
import mbd.s3ackup.daemon.api.rest.dto.LoginResponse;
import mbd.s3ackup.daemon.api.rest.dto.LoginTypeResponse;
import mbd.s3ackup.daemon.cloud.CachedCloudStorage;
import mbd.s3ackup.daemon.cloud.s3.S3Client;
import mbd.s3ackup.daemon.cloud.s3.S3Client.NotAuthenticatedException;
import mbd.s3ackup.daemon.local.settings.AppPreferences;
import mbd.s3ackup.daemon.util.Encryptor.EncryptorException;

@Service
public class LoginService {

	private static final Logger log = LoggerFactory.getLogger(LoginService.class);

	@Autowired
	private S3Client s3Client;

	@Autowired
	private AppPreferences appPreferences;

	@Autowired
	@Qualifier("cachedCloudStorage")
	private CachedCloudStorage cloudStorage;

	public LoginResponse login(LoginRequest request) throws EncryptorException {
		cloudStorage.clearCache();
		LoginResponse response = new LoginResponse();
		try {

			String accessKey = request.getAccessKey();
			String secretKey = request.getSecretKey();
			String pin = request.getPin();

			if (StringUtils.isEmpty(accessKey) && StringUtils.isEmpty(secretKey) && StringUtils.isNotEmpty(pin)) {
				accessKey = appPreferences.getAwsAccessKey();
				secretKey = appPreferences.getAwsSecretKeyDecrypted(pin);
			}

			s3Client.login(accessKey, secretKey);
			if (request.isRememberMe()) {
				appPreferences.saveAwsKeys(accessKey, secretKey, pin);
			}
			response.setSuccess(true);
			return response;
		}
		catch (NotAuthenticatedException e) {
			log.error("", e);
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			return response;
		}
	}

	public void logout() {
		cloudStorage.clearCache();
		s3Client.logout();
	}

	public LoginTypeResponse loginType() {
		LoginTypeResponse out = new LoginTypeResponse();
		String awsAccessKey = appPreferences.getAwsAccessKey();
		String awsSecretKey = appPreferences.getAwsSecretKey();
		if (StringUtils.isEmpty(awsAccessKey) || StringUtils.isEmpty(awsSecretKey)) {
			out.setLoginCredentialsSavedLocally(false);
			return out;
		}
		else {
			out.setLoginCredentialsSavedLocally(true);
			out.setAccessKey(awsAccessKey);
			out.setSecretKey("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			return out;
		}
	}

	public LoginTypeResponse clear() {
		appPreferences.clearAwsKeys();
		return loginType();
	}

}
