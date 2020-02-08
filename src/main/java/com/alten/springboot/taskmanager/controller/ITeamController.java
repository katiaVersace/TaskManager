package com.alten.springboot.taskmanager.controller;

import java.util.List;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.alten.springboot.taskmanager.dto.RandomPopulationInputDto;
import com.alten.springboot.taskmanager.dto.TaskDto;
import com.alten.springboot.taskmanager.dto.TeamDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value = "Team Management System", description = "Operations pertaining to teams")
@RestController
@RequestMapping("/teams")
public interface ITeamController {

	@ApiOperation(value = "Get Teams", response = String.class)
	@GetMapping(produces = "application/json")
	public List<TeamDto> getTeams();

	@ApiOperation(value = "Get an Teams by Id", response = TeamDto.class)
	@GetMapping(value ="/{teamId}",produces = "application/json")
	public TeamDto getTeam(
			@ApiParam(value = "Team Id from which Team object will retrieve", required = true) @PathVariable("teamId") int teamId);

	@ApiOperation(value = "Add a team, allowed only to ADMIN employees", response = TeamDto.class)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(consumes = "application/json", produces = "application/json")
	public TeamDto addTeam(
			@ApiParam(value = "Team object store in database table", required = true) @RequestBody TeamDto theTeam);

	@ApiOperation(value = "Update a team, allowed only to the Admin", response = TeamDto.class)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping(consumes = "application/json", produces = "application/json")
	public TeamDto updateTeam(
			@ApiParam(value = "Updated Team object to store in database table", required = true) @RequestBody TeamDto theTeam);

	@ApiOperation(value = "Delete a team, allowed only to ADMIN employees", response = String.class)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(value = "/{teamId}", produces = "application/json")
	public String deleteTeam(
			@ApiParam(value = "Team Id from which team object will delete from database table", required = true) @PathVariable("teamId") int teamId);

	@ApiOperation(value = "Random Populate db, allowed only to ADMIN employees", response = TaskDto.class)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(value = "/randomPopulation", consumes = "application/json", produces = "application/json")
	public String randomPopulation(
			@ApiParam(value = "Input variables", required = true) @RequestBody  RandomPopulationInputDto input);

	@ApiOperation(value = "Assign Task to team with possibility to rearrange, allowed only to ADMIN employees", response = TaskDto.class)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(value = "/assignTaskToTeam/{teamId}", consumes = "application/json", produces = "application/json")
	public TaskDto assignTaskToTeam(@ApiParam(value = "Team Id", required = true) @PathVariable("teamId") int teamId,
			@ApiParam(value = "Task to assign", required = true) @RequestBody TaskDto task);

}
