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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import com.alten.springboot.taskmanager.dto.TeamDto;

@Api(value = "Team Management System", description = "Operations pertaining to teams")
@Path("/teams")
public interface ITeamController {
	
	@ApiOperation(value = "Get Teams", response = String.class)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<TeamDto> getTeams();
	
	@ApiOperation(value = "Get an Teams by Id", response = TeamDto.class)
	@GET
	@Path("/{teamId}")
	@Produces(MediaType.APPLICATION_JSON)
	public TeamDto getTeam(
			@ApiParam(value = "Team Id from which Team object will retrieve", required = true) @PathParam("teamId") int teamId);

	@ApiOperation(value = "Add a team, allowed only to ADMIN employees", response = TeamDto.class)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@POST
	//@Path("/teams")
	@Consumes(MediaType.APPLICATION_JSON) 
	@Produces(MediaType.APPLICATION_JSON)
	public TeamDto addTeam(
			@ApiParam(value = "Team object store in database table", required = true) @RequestBody TeamDto theTeam);

	@ApiOperation(value = "Update a team, allowed only to the Admin", response = TeamDto.class)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PUT
	//@Path("/teams")
	@Consumes(MediaType.APPLICATION_JSON) 
	@Produces(MediaType.APPLICATION_JSON)
	public TeamDto updateTeam(
			@ApiParam(value = "Updated Team object to store in database table", required = true) @RequestBody TeamDto theTeam);

	@ApiOperation(value = "Delete a team, allowed only to ADMIN employees", response = String.class)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DELETE
	@Path("/{teamId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteTeam(
			@ApiParam(value = "Team Id from which team object will delete from database table", required = true) @PathParam("teamId") int teamId);

//	@ApiOperation(value = "Populate db", response = String.class)
//	@GET
//	@Path("/populate")
//	@Produces(MediaType.APPLICATION_JSON)
//	public void populateDatabase();


}
