package com.alten.springboot.taskmanager.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.alten.springboot.taskmanager.dto.EmployeeDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Login Management System", description = "Operations pertaining to Login")
@Path("/auth")
public interface ILoginController {
	
	@ApiOperation(value = "Login", response = EmployeeDto.class)
	@POST
	@Path("/login")
	public EmployeeDto login( @FormParam("username") String username, @FormParam("password")  String password);

	@ApiOperation(value = "Logout", response = String.class)
	@GET
	@Path("/logout")
	public String logout() ;

}
