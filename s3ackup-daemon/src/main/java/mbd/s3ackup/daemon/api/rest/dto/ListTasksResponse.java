package mbd.s3ackup.daemon.api.rest.dto;

import java.util.List;

public class ListTasksResponse {

	private List<TaskResponse> taskResponseList;

	public ListTasksResponse() {
	}

	public ListTasksResponse(List<TaskResponse> taskResponseList) {
		super();
		this.taskResponseList = taskResponseList;
	}

	public List<TaskResponse> getTaskResponseList() {
		return taskResponseList;
	}

	public void setTaskResponseList(List<TaskResponse> taskResponseList) {
		this.taskResponseList = taskResponseList;
	}

	@Override
	public String toString() {
		return "ListTasksResponse [taskResponseList=" + taskResponseList + "]";
	}

}
