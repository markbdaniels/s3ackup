package mbd.s3ackup.daemon.local;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathUtils {

	private static final Logger log = LoggerFactory.getLogger(PathUtils.class);

	public static String extractRootFromPath(String path) {
		Path p = Paths.get(path).getName(0);
		return FilenameUtils.separatorsToUnix(p.toString());
	}

	public static String removeRootFromPath(String path) {
		Path p = Paths.get(path);
		if (p.getNameCount() <= 1) {
			return "";
		}
		else {
			p = p.subpath(1, p.getNameCount());
		}
		return FilenameUtils.separatorsToUnix(p.toString());
	}

	public static String calculateMd5hash(Path localPath) {
		if (!Files.exists(localPath)) {
			// file does not exist
			return null;
		}

		try {
			InputStream is = Files.newInputStream(localPath);
			String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
			is.close();
			return md5;
		}
		catch (IOException e) {
			log.error("unable to calc md5", e);
			return null;
		}
	}
}
