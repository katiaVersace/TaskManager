package com.alten.springboot.taskmanager.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Input for random Population service ")
public class AssignTaskToTeamInputDto implements Serializable{
	
	@ApiModelProperty(notes = "The first day for considered period")
	private String start;

	@ApiModelProperty(notes = "The last day for considered period")
	private String end;

	@ApiModelProperty(notes = "The team_id")
	private int team_id;
	
	@ApiModelProperty(notes = "The task to assign")
	private TaskDto task;
	
	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public TaskDto getTask() {
		return task;
	}

	public void setTask(TaskDto theTask) {
		this.task = theTask;
	}

	public int getTeam_id() {
		return team_id;
	}

	public void setTeam_id(int team_id) {
		this.team_id = team_id;
	}

	
}
