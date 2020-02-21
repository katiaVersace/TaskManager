package com.alten.springboot.taskmanager.controller

import com.alten.springboot.taskmanager.dto.RandomPopulationInputDto
import com.alten.springboot.taskmanager.dto.TaskDto
import com.alten.springboot.taskmanager.dto.TeamDto
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@Api(value = "Team Management System", description = "Operations pertaining to teams")
@RestController
@RequestMapping("/teams")
interface ITeamController {
    @GetMapping(produces = ["application/json"])
    @ApiOperation(value = "Get Teams", response = String::class)
    fun getTeams(): List<TeamDto?>?

    @ApiOperation(value = "Get an Teams by Id", response = TeamDto::class)
    @GetMapping(value = ["/{teamId}"], produces = ["application/json"])
    fun getTeam(
            @ApiParam(value = "Team Id from which Team object will retrieve", required = true) @PathVariable("teamId") teamId: Int): TeamDto?

    @ApiOperation(value = "Add a team, allowed only to ADMIN employees", response = TeamDto::class)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    fun addTeam(
            @ApiParam(value = "Team object store in database table", required = true) @RequestBody theTeam: TeamDto?): TeamDto?

    @ApiOperation(value = "Update a team, allowed only to the Admin", response = TeamDto::class)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(consumes = ["application/json"], produces = ["application/json"])
    fun updateTeam(
            @ApiParam(value = "Updated Team object to store in database table", required = true) @RequestBody theTeam: TeamDto?): TeamDto?

    @ApiOperation(value = "Delete a team, allowed only to ADMIN employees", response = String::class)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = ["/{teamId}"], produces = ["application/json"])
    fun deleteTeam(
            @ApiParam(value = "Team Id from which team object will delete from database table", required = true) @PathVariable("teamId") teamId: Int): String?

    @ApiOperation(value = "Random Populate db, allowed only to ADMIN employees", response = TaskDto::class)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = ["/randomPopulation"], consumes = ["application/json"], produces = ["application/json"])
    fun randomPopulation(
            @ApiParam(value = "Input variables", required = true) @RequestBody input: RandomPopulationInputDto?): String?

    @ApiOperation(value = "Assign Task to team with possibility to rearrange, allowed only to ADMIN employees", response = TaskDto::class)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = ["/assignTaskToTeam/{teamId}"], consumes = ["application/json"], produces = ["application/json"])
    fun assignTaskToTeam(@ApiParam(value = "Team Id", required = true) @PathVariable("teamId") teamId: Int,
                         @ApiParam(value = "Task to assign", required = true) @RequestBody task: TaskDto?): TaskDto?
}