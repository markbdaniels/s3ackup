package mbd.s3ackup.daemon.cloud;

import java.nio.file.Path;

public final class CloudDirectory extends CloudPath {

	public CloudDirectory(CloudRoot root, String path) {
		super(root, path);
	}

	public CloudDirectory(CloudRoot root, Path path) {
		super(root, path);
	}

	@Override
	public String toString() {
		return "CloudDirectory [toString()=" + super.toString() + "]";
	}
}