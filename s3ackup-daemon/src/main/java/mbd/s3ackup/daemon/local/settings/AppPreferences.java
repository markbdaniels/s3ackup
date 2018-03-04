package mbd.s3ackup.daemon.local.settings;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import org.springframework.stereotype.Component;

import mbd.s3ackup.daemon.cloud.CloudRoot;
import mbd.s3ackup.daemon.util.Encryptor;
import mbd.s3ackup.daemon.util.Encryptor.EncryptorException;

@Component
public class AppPreferences {

	private static final String AWS_SECRET_KEY = "aws.secret.key";
	private static final String AWS_ACCESS_KEY = "aws.access.key";
	private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

	public boolean isRootEnabledOnLocal(CloudRoot cloudRoot) {
		String key = getCloudRootEnabledKey(cloudRoot);
		return prefs.getBoolean(key, false);
	}

	private String getCloudRootEnabledKey(CloudRoot cloudRoot) {
		String key = "cloudRoot.enabled.on.local." + cloudRoot.getName();
		return key;
	}

	public void enabledRootOnLocal(CloudRoot cloudRoot, boolean enabled) {
		String key = getCloudRootEnabledKey(cloudRoot);
		prefs.putBoolean(key, enabled);
	}

	public Path getLocalDir() {
		String userHome = System.getProperty("user.home");
		Path path = Paths.get(userHome, "s3ackup");
		Path out = Paths.get(prefs.get("local.dir", path.toAbsolutePath().toString()));
		try {
			// create it if is does not exist
			Files.createDirectories(out);
		}
		catch (IOException e) {
		}
		return out;
	}

	public void saveAwsKeys(String accessKey, String secretKey, String pin)
			throws EncryptorException {
		Encryptor encryptor = buildEncryptor(pin);
		prefs.put(AWS_ACCESS_KEY, accessKey);
		prefs.put(AWS_SECRET_KEY, encryptor.encrypt(secretKey));
	}

	public void clearAwsKeys() {
		prefs.put(AWS_ACCESS_KEY, "");
		prefs.put(AWS_SECRET_KEY, "");
	}

	public String getAwsAccessKey() {
		return prefs.get(AWS_ACCESS_KEY, null);
	}

	public String getAwsSecretKey() {
		return prefs.get(AWS_SECRET_KEY, null);
	}

	public String getAwsSecretKeyDecrypted(String pin) throws EncryptorException {
		String encryptedSecretKey = getAwsSecretKey();
		Encryptor encryptor = buildEncryptor(pin);
		return encryptor.decrypt(encryptedSecretKey);
	}

	private Encryptor buildEncryptor(String pin) throws EncryptorException {
		return new Encryptor(pin.toCharArray(),
				Charset.forName("UTF-8")
						.encode(CharBuffer.wrap(
								new char[] { 'B', 'D', 'j', 'J', 'h', 'X', 'N', 'd' }))
						.array(),
				20);
	}

}
