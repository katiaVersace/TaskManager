package com.alten.springboot.taskmanager.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "All details about the Team. ")
public class TeamDto implements Serializable{
	
	@ApiModelProperty(notes = "The Team id")
	private int id;
	
	@ApiModelProperty(notes = "The Team name")
	private String name;
	
	@ApiModelProperty(notes = "The employees of the team")
	private List<EmployeeDto> employees;
	
	@ApiModelProperty(notes = "The team version stored in the database")
	private int version;

	public TeamDto() {
		super();
	}

	public TeamDto(String name) {
		super();
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EmployeeDto> getEmployees() {
		if(employees==null)
			employees = new ArrayList<EmployeeDto>();
		return employees;
	}

	public void setEmployees(List<EmployeeDto> employees) {
		this.employees = employees;
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}


	
	
	

}
