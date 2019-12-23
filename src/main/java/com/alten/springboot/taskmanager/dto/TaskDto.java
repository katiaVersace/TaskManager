package com.alten.springboot.taskmanager.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "All details about the Task. ")
public class TaskDto implements Serializable{
	
	@ApiModelProperty(notes = "The database generated task ID")
	private int id;

	@ApiModelProperty(notes = "The task description")
	private String description;

	@ApiModelProperty(notes = "The employee associated with the task")
	private int employeeId;
	
	@ApiModelProperty(notes = "The task Expected Start Time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd",timezone="CET")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate expectedStartTime;
	
	@ApiModelProperty(notes = "The task Real Start Time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd",timezone="CET")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate realStartTime;
	
	@ApiModelProperty(notes = "The task Expected End Time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd",timezone="CET")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate expectedEndTime;
	
	@ApiModelProperty(notes = "The task Real End Time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd",timezone="CET")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate realEndTime;
	
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

	public LocalDate getExpectedStartTime() {
		return expectedStartTime;
	}

	public void setExpectedStartTime(LocalDate expectedStartTime) {
		this.expectedStartTime = expectedStartTime;
	}

	public LocalDate getRealStartTime() {
		return realStartTime;
	}

	public void setRealStartTime(LocalDate realStartTime) {
		this.realStartTime = realStartTime;
	}

	public LocalDate getExpectedEndTime() {
		return expectedEndTime;
	}

	public void setExpectedEndTime(LocalDate expectedEndTime) {
		this.expectedEndTime = expectedEndTime;
	}

	public LocalDate getRealEndTime() {
		return realEndTime;
	}

	public void setRealEndTime(LocalDate realEndTime) {
		this.realEndTime = realEndTime;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	


}
