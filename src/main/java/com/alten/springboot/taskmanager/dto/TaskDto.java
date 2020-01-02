package com.alten.springboot.taskmanager.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "All details about the Task. ")
public class TaskDto implements Serializable {

	@ApiModelProperty(notes = "The database generated task ID")
	private int id;

	@ApiModelProperty(notes = "The task description")
	private String description;

	@ApiModelProperty(notes = "The employee associated with the task")
	private int employeeId;

	@ApiModelProperty(notes = "The task Expected Start Time")
	private String expectedStartTime;

	@ApiModelProperty(notes = "The task Real Start Time")
	private String realStartTime;

	@ApiModelProperty(notes = "The task Expected End Time")
	private String expectedEndTime;

	@ApiModelProperty(notes = "The task Real End Time")
	private String realEndTime;

	@ApiModelProperty(notes = "The task version stored in the database")
	private int version;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getExpectedStartTime() {
		return expectedStartTime;
	}

	public void setExpectedStartTime(String expectedStartTime) {
		this.expectedStartTime = expectedStartTime;
	}

	public String getRealStartTime() {
		return realStartTime;
	}

	public void setRealStartTime(String realStartTime) {
		this.realStartTime = realStartTime;
	}

	public String getExpectedEndTime() {
		return expectedEndTime;
	}

	public void setExpectedEndTime(String expectedEndTime) {
		this.expectedEndTime = expectedEndTime;
	}

	public String getRealEndTime() {
		return realEndTime;
	}

	public void setRealEndTime(String realEndTime) {
		this.realEndTime = realEndTime;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
