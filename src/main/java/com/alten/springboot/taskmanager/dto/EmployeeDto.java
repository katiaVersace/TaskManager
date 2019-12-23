package com.alten.springboot.taskmanager.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "All details about the Employee. ")
public class EmployeeDto implements Serializable{
	
	@ApiModelProperty(notes = "The database generated employee ID")
	private int id;

	@ApiModelProperty(notes = "The employee username")
	private String userName;

	@ApiModelProperty(notes = "The employee password")
	private String password;

	@ApiModelProperty(notes = "The employee first name")
	private String firstName;

	@ApiModelProperty(notes = "The employee last name")
	private String lastName;

	@ApiModelProperty(notes = "The employee email")
	private String email;
	
	@ApiModelProperty(notes = "The employee Top Employee property")
	private boolean topEmployee;

	@ApiModelProperty(notes = "The employee's roles")
	private Collection<RoleDto> roles;
	
	@ApiModelProperty(notes = "The employee version stored in the database")
	private int version;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isTopEmployee() {
		return topEmployee;
	}

	public void setTopEmployee(boolean topEmployee) {
		this.topEmployee = topEmployee;
	}

	public Collection<RoleDto> getRoles() {
		if(roles == null)
			roles = new ArrayList<RoleDto>();
		return roles;
	}

	public void setRoles(Collection<RoleDto> roles) {
		this.roles = roles;
	}


	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	

}
