package com.alten.springboot.taskmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alten.springboot.taskmanager.businessservice.ITeamBusinessService;
import com.alten.springboot.taskmanager.dto.RandomPopulationInputDto;
import com.alten.springboot.taskmanager.dto.TaskDto;
import com.alten.springboot.taskmanager.dto.TeamDto;

@Component
public class TeamController implements ITeamController {

	@Autowired
	private ITeamBusinessService teamService;

	@Override
	public List<TeamDto> getTeams() {
		return teamService.findAll();
	}

	@Override
	public TeamDto getTeam(int teamId) {
		return teamService.findById(teamId);

	}

	@Override
	public TeamDto addTeam(TeamDto theTeam) {
		theTeam.setId(0);
		teamService.save(theTeam);
		return theTeam;
	}

	@Override
	public TeamDto updateTeam(TeamDto theTeam) {

		teamService.update(theTeam);
		return theTeam;
	}

	@Override
	public String deleteTeam(int teamId) {
		teamService.delete(teamId);
		return "Deleted team with id: " + teamId;
	}

	@Override
	public String randomPopulation(RandomPopulationInputDto input) {

		return teamService.randomPopulation(input.getStart(), input.getEnd(), input.getTeams_size(),
				input.getEmployees_size(), input.getTasks_size(), input.getTask_max_duration());

	}

	@Override
	public TaskDto assignTaskToTeam(int teamId, TaskDto task) {

		return teamService.tryAssignTaskToTeam(task.getExpectedStartTime(), task.getExpectedEndTime(), teamId, task);
	}

}
