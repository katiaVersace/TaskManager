package com.alten.springboot.taskmanager.controller;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;

import com.alten.springboot.taskmanager.dto.AvailabilityByEmployeeInputDto;
import com.alten.springboot.taskmanager.dto.EmployeeDto;
import com.alten.springboot.taskmanager.dto.TaskDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value = "Employee Management System", description = "Operations pertaining to employee in Employee Management System")

@Path("/employees")
public interface IEmployeeController {

	@ApiOperation(value = "View a list of available employees", response = List.class)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<EmployeeDto> getEmployees();

	@ApiOperation(value = "Get an employee by Id", response = EmployeeDto.class)
	@GET
	@Path("/{employeeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public EmployeeDto getEmployee(
			@ApiParam(value = "Employee Id from which employee object will retrieve", required = true) @PathParam("employeeId") int employeeId);

	@ApiOperation(value = "Add an employee, allowed only to ADMIN employees", response = EmployeeDto.class)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@POST
	@Path("/{admin}")
	@Consumes(MediaType.APPLICATION_JSON) 
	@Produces(MediaType.APPLICATION_JSON)
	public EmployeeDto addEmployee(
			@ApiParam(value = "Variable to distinguish if it's saving admin or not", required = true) @PathParam("admin") int admin,
			@ApiParam(value = "Employee object store in database table", required = true) @RequestBody EmployeeDto theEmployee);

		
	@ApiOperation(value = "Update an employee, allowed only to the Admin or the interested Employee", response = EmployeeDto.class)
	@PreAuthorize("@securityService.isOwner(principal.id,#theEmployee.getId()) or hasRole('ROLE_ADMIN')")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON) 
	@Produces(MediaType.APPLICATION_JSON)
	public EmployeeDto updateEmployee(
			@ApiParam(value = "Updated Employee object to store in database table", required = true) @RequestBody EmployeeDto theEmployee);

	@ApiOperation(value = "Delete an employee, allowed only to ADMIN employees", response = String.class)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DELETE
	@Path("/{employeeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteEmployee(
			@ApiParam(value = "Employee Id from which employee object will delete from database table", required = true) @PathParam("employeeId") String employeeId);

	@ApiOperation(value = "View a list of available employees in a Team for a Task", response = List.class)
	@GET
	@Path("/employeesByTeamAndTask/{teamId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<EmployeeDto> getAvailableEmployeesByTeamAndTask(@ApiParam(value = "Team id object store in database table", required = true) @PathParam("teamId") int teamId,
			@ApiParam(value = "Task object store in database table", required = true) TaskDto theTask);
	
	
	@ApiOperation(value = "View availability and tasks for an Employee", response = List.class)
	@GET
	@Path("/availability")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAvailabilityByEmployee(@ApiParam(value = "input", required = true)  AvailabilityByEmployeeInputDto input);


}
