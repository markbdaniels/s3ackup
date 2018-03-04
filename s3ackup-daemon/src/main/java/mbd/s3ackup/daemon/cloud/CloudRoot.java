package mbd.s3ackup.daemon.cloud;

import java.util.Date;

public final class CloudRoot {

	private final String name;
	private Date dtCreated;

	public CloudRoot(String name) {
		super();
		this.name = name;
	}

	public CloudRoot(String name, Date dtCreated) {
		super();
		this.name = name;
		this.dtCreated = dtCreated;
	}

	public String getName() {
		return name;
	}

	public Date getDtCreated() {
		return dtCreated;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		CloudRoot other = (CloudRoot) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CloudRoot [name=" + name + "]";
	}
}
