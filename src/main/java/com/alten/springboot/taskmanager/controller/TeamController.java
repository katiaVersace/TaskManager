package com.alten.springboot.taskmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alten.springboot.taskmanager.business_service.TeamBusinessService;
import com.alten.springboot.taskmanager.dto.TeamDto;

@Component
public class TeamController implements ITeamController{
	
	@Autowired
	private TeamBusinessService teamService;

	@Override
	public List<TeamDto> getTeams() {
		return teamService.findAll();
	}

	@Override
	public TeamDto getTeam(int teamId) {
		TeamDto team = teamService.findById(teamId);
		
		return team;
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
		return "Deleted team with id: "+teamId;
	}

//	@Override
//	public void populateDatabase() {
//		teamService.populateDB();
//	}

}
