package mbd.s3ackup.daemon.cloud.s3;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;

@Component
public class S3Client {

	private static final Logger log = LoggerFactory.getLogger(S3Client.class);

	private AmazonS3Wrapper amazonS3Wrapper;

	public S3Client(@Value("${aws.key.access:}") String accessKey,
			@Value("${aws.key.secret:}") String secretKey) {
		if (StringUtils.isNotEmpty(accessKey) && StringUtils.isNotEmpty(secretKey)) {
			login(accessKey, secretKey);
		}
	}

	public void login(String accessKey, String secretKey) {
		this.amazonS3Wrapper = new AmazonS3Wrapper(accessKey, secretKey);
	}

	public void logout() {
		this.amazonS3Wrapper = null;
	}

	public AmazonS3 getS3Client() {
		if (amazonS3Wrapper == null) {
			throw new NotAuthenticatedException();
		}
		return amazonS3Wrapper.getS3client();
	}

	public static class NotAuthenticatedException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public NotAuthenticatedException() {
			super();
		}

		public NotAuthenticatedException(String message) {
			super(message);
		}
	}

	private static class AmazonS3Wrapper {
		private AmazonS3 s3client;

		public AmazonS3Wrapper(String accessKey, String secretKey) {
			super();
			log.info("creating S3 client with key[{}], secret[{}]", accessKey,
					StringUtils.leftPad(StringUtils.right(secretKey, 4), 40, "x"));
			BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);

			this.s3client = AmazonS3ClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
					.withRegion("us-east-1").withForceGlobalBucketAccessEnabled(true)
					.build();

			// test connection
			try {
				this.s3client.listBuckets();
			}
			catch (AmazonS3Exception e) {
				log.error("unable to authenticate", e);
				throw new NotAuthenticatedException(e.getMessage());
			}
		}

		public AmazonS3 getS3client() {
			return s3client;
		}
	}

}
