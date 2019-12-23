package com.alten.springboot.taskmanager.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "All details about the Role. ")
public class RoleDto implements Serializable{

	@ApiModelProperty(notes = "The database generated Role ID")
	private int id;
	
	@ApiModelProperty(notes = "The Role name")
	private String name;

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

	/*@Override
	public String toString() {
		return "RoleDto [id=" + id + ", name=" + name + "]";
	}*/
}
