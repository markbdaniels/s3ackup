package mbd.s3ackup.daemon.z_boot;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import mbd.s3ackup.daemon.api.rest.RestApiService;
import mbd.s3ackup.daemon.cloud.s3.S3Client.NotAuthenticatedException;
import mbd.s3ackup.daemon.local.settings.AppPreferences;
import mbd.s3ackup.daemon.util.DesktopOpen;

@Component
public class PostStartupCommandRunner implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(PostStartupCommandRunner.class);

	@Autowired
	private RestApiService restApiService;

	@Autowired
	private AppPreferences appPreferences;

	@Value(value = "${server.port:56000}")
	private int port;

	/**
	 * Some tasks to run after the app has started
	 */
	@Override
	public void run(String... args) throws Exception {

		// starting watching the dirs if authed
		try {
			restApiService.getRootDirectories();
		}
		catch (NotAuthenticatedException e) {
		}

		String url = String.format("http://localhost:%s", port);
		DesktopOpen.openBrower(new URI(url));
		log.info("You can now view s3ackup in the browser: {}", url);

		DesktopOpen.openFile(appPreferences.getLocalDir());
	}
}
