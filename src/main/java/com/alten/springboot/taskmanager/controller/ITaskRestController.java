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

import com.alten.springboot.taskmanager.dto.TaskDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value = "Task Management System", description = "Operations pertaining to task in Task Management System")

@Path("/tasks")
public interface ITaskRestController {
	
	
	@ApiOperation(value = "View a list of available tasks", response = List.class)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<TaskDto> getTasks();
	
	@ApiOperation(value = "Get a task by Id", response = TaskDto.class)
	@GET
	@Path("/{taskId}")
	@Produces(MediaType.APPLICATION_JSON)
	public TaskDto getTask(
			@ApiParam(value = "Task id from which Task object will retrieve", required = true) @PathParam("taskId") String taskId) ;
	
	@ApiOperation(value = "Add a task, allowed only to ADMIN employees", response = TaskDto.class)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@POST
	@Consumes(MediaType.APPLICATION_JSON) 
	@Produces(MediaType.APPLICATION_JSON)
	public TaskDto addTask(
			@ApiParam(value = "Task object store in database table", required = true) TaskDto theTask);
	
	@ApiOperation(value = "Put a task, allowed only to ADMIN employees", response = TaskDto.class)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON) 
	@Produces(MediaType.APPLICATION_JSON)
	public TaskDto updateTaskAdmin(
			@ApiParam(value = "Updated Task object to store in database table", required = true) @RequestBody TaskDto theTask);
	
	@ApiOperation(value = "Patch a task, allowed only to ADMIN employees and to the owner of  the task", response = TaskDto.class)
	@PreAuthorize("@securityService.isOwner(principal.id,#theTask.getEmployeeId()) or hasRole('ROLE_ADMIN')")
	@PATCH
	@Consumes(MediaType.APPLICATION_JSON) 
	@Produces(MediaType.APPLICATION_JSON)
	public TaskDto updateTask(
			@ApiParam(value = "Updated Task object to store in database table", required = true) @RequestBody TaskDto theTask);
	
	@ApiOperation(value = "Delete a task, allowed only to ADMIN employees", response = String.class)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DELETE
	@Path("/{taskId}") 
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteTask(
			@ApiParam(value = "Task Id from which task object will delete from database table", required = true) @PathParam("taskId") String taskId);
	
	@ApiOperation(value = "View a list of available tasks for a predefined employee", response = List.class)
	@GET
	@Path("/tasksByEmployee/{employeeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TaskDto> getTasksByEmployeeId(
			@ApiParam(value = "Employee id for which will retrieve the tasks", required = true) @PathParam("employeeId") String employeeId) ;

	
	


}
