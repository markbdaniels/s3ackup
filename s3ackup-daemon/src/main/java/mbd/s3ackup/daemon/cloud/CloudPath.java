package mbd.s3ackup.daemon.cloud;

import java.nio.file.Path;
import java.util.Comparator;

import org.apache.commons.io.FilenameUtils;

public abstract class CloudPath implements Comparable<CloudPath> {

	private CloudRoot root;
	private String path;

	public CloudPath(CloudRoot root, String path) {
		super();
		this.root = root;
		this.path = path == null ? "" : path;
	}

	public CloudPath(CloudRoot root, Path path) {
		this(root, path == null ? null : FilenameUtils.separatorsToUnix(path.toString()));
	}

	public CloudRoot getRoot() {
		return root;
	}

	public String getPath() {
		return path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((root == null) ? 0 : root.hashCode());
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
		CloudPath other = (CloudPath) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		}
		else if (!path.equals(other.path))
			return false;
		if (root == null) {
			if (other.root != null)
				return false;
		}
		else if (!root.equals(other.root))
			return false;
		return true;
	}

	@Override
	public int compareTo(CloudPath o) {
		return this.getPath().compareToIgnoreCase(o.getPath());
	}

	public boolean isDirectory() {
		return this instanceof CloudDirectory;
	}

	public boolean isFile() {
		return this instanceof CloudFile;
	}

	public static Comparator<? super CloudPath> sortByTypeThenNameComparator() {
		return (CloudPath o1, CloudPath o2) -> {
			if (o1.isDirectory() && o2.isFile()) {
				return -11;
			}
			else if (o1.isFile() && o2.isDirectory()) {
				return 1;
			}
			else {
				return o1.compareTo(o2);
			}
		};
	}

	@Override
	public String toString() {
		return "CloudPath [root=" + root + ", path=" + path + "]";
	}
}
