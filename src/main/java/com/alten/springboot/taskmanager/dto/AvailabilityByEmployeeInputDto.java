package com.alten.springboot.taskmanager.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Input for random Population service ")
public class AvailabilityByEmployeeInputDto implements Serializable{
	
	@ApiModelProperty(notes = "The first day for considered period")
	private String start;

	@ApiModelProperty(notes = "The last day for considered period")
	private String end;

	@ApiModelProperty(notes = "The employee_id")
	private int employee_id;
	
	
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

	public int getEmployee_id() {
		return employee_id;
	}

	public void setEmployee_id(int employee_id) {
		this.employee_id = employee_id;
	}

	

	
}
