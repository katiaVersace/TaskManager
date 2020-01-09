package com.alten.springboot.taskmanager.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Input for random Population service ")
public class RandomPopulationInputDto implements Serializable {

	@ApiModelProperty(notes = "The first day for considered period")
	private String start;

	@ApiModelProperty(notes = "The last day for considered period")
	private String end;

	@ApiModelProperty(notes = "The size of the teams you want to generate")
	private int teams_size;

	@ApiModelProperty(notes = "The size of the employees you want to generate")
	private int employees_size;

	@ApiModelProperty(notes = "The size of the tasks you want to generate")
	private int tasks_size;

	@ApiModelProperty(notes = "The max duration of the tasks you want to generate")
	private int task_max_duration;

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

	public int getTeams_size() {
		return teams_size;
	}

	public void setTeams_size(int teams_size) {
		this.teams_size = teams_size;
	}

	public int getEmployees_size() {
		return employees_size;
	}

	public void setEmployees_size(int employees_size) {
		this.employees_size = employees_size;
	}

	public int getTasks_size() {
		return tasks_size;
	}

	public void setTasks_size(int tasks_size) {
		this.tasks_size = tasks_size;
	}

	public int getTask_max_duration() {
		return task_max_duration;
	}

	public void setTask_max_duration(int task_max_duration) {
		this.task_max_duration = task_max_duration;
	}

}
