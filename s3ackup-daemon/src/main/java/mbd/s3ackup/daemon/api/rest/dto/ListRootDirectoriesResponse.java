package mbd.s3ackup.daemon.api.rest.dto;

import java.util.Date;
import java.util.List;

public class ListRootDirectoriesResponse {

	private List<RootDirectory> rootDirectoryList;

	public List<RootDirectory> getRootDirectoryList() {
		return rootDirectoryList;
	}

	public void setRootDirectoryList(List<RootDirectory> rootDirectoryList) {
		this.rootDirectoryList = rootDirectoryList;
	}

	@Override
	public String toString() {
		return "ListRootDirectoriesResponse [rootDirectoryList=" + rootDirectoryList + "]";
	}

	public static class RootDirectory {
		private String name;
		private Boolean enabled;
		private Date dtCreated;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Boolean getEnabled() {
			return enabled;
		}

		public void setEnabled(Boolean enabled) {
			this.enabled = enabled;
		}

		public Date getDtCreated() {
			return dtCreated;
		}

		public void setDtCreated(Date dtCreated) {
			this.dtCreated = dtCreated;
		}

		@Override
		public String toString() {
			return "RootDirectory [name=" + name + ", enabled=" + enabled + ", dtCreated=" + dtCreated + "]";
		}
	}
}
