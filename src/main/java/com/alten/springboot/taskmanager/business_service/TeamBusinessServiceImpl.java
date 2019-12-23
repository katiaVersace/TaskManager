package com.alten.springboot.taskmanager.business_service;



import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.springboot.taskmanager.data_service.TeamDataService;
import com.alten.springboot.taskmanager.dto.TeamDto;
import com.alten.springboot.taskmanager.entity.Team;

@Service
public class TeamBusinessServiceImpl implements TeamBusinessService {

	@Autowired
	private TeamDataService teamDataService;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public List<TeamDto> findAll() {
		List<Team> teams = teamDataService.findAll();

		List<TeamDto> teamsDto = new ArrayList<TeamDto>();
		for (Team team : teams) {
			teamsDto.add(modelMapper.map(team, TeamDto.class));
		}
		return teamsDto;
	}

	@Override
	public TeamDto findById(int teamId) {
		Team team = teamDataService.findById(teamId);

		TeamDto teamDto = null;
		if (team != null) {

			teamDto = modelMapper.map(team, TeamDto.class);
		}

		return teamDto;
	}

	@Override
	public void save(TeamDto teamDto) {
		Team team = modelMapper.map(teamDto, Team.class);
		teamDataService.save(team);

	}

	@Override
	public boolean update(TeamDto teamDto) {
		Team newTeam = modelMapper.map(teamDto, Team.class);
		return teamDataService.update(newTeam);
	}

	@Override
	public void delete(int teamId) {
		 teamDataService.delete(teamId);

	}

	@Override
	public String populateDB() {
		
		return "ok";

	}


}
