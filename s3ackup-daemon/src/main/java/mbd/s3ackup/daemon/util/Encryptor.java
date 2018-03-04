package mbd.s3ackup.daemon.util;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;

public class Encryptor {

	public static final String charset = "UTF8";
	private Cipher encrypter;
	private Cipher decrypter;

	public Encryptor(char[] passphrase, byte[] salt, int iterationCount) throws EncryptorException {
		try {
			KeySpec keySpec = new PBEKeySpec(passphrase, salt, iterationCount);
			SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
			this.encrypter = Cipher.getInstance(key.getAlgorithm());
			this.decrypter = Cipher.getInstance(key.getAlgorithm());
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
			this.encrypter.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			this.decrypter.init(Cipher.DECRYPT_MODE, key, paramSpec);
		}
		catch (Exception e) {
			throw new EncryptorException(e);
		}
	}

	public String decrypt(String param) throws EncryptorException {
		try {
			synchronized (decrypter) {
				return new String(this.decrypter.doFinal(Base64.decodeBase64(param.getBytes())), charset);
			}
		}
		catch (Exception e) {
			throw new EncryptorException(e);
		}
	}

	public String encrypt(String param) throws EncryptorException {
		try {
			synchronized (encrypter) {
				return new String(Base64.encodeBase64(encrypter.doFinal(param.getBytes())), charset);
			}
		}
		catch (Exception e) {
			throw new EncryptorException(e);
		}
	}

	public static class EncryptorException extends Exception {
		private static final long serialVersionUID = 1L;

		public EncryptorException(Throwable cause) {
			super(cause);
		}
	}
}