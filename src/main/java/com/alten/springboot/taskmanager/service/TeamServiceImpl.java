package com.alten.springboot.taskmanager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.springboot.taskmanager.dao.TeamRepository;
import com.alten.springboot.taskmanager.dto.TeamDto;
import com.alten.springboot.taskmanager.entity.Team;

@Service
public class TeamServiceImpl implements TeamService {

	@Autowired
	private TeamRepository teamDao;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	@Transactional
	public List<TeamDto> findAll() {
		List<Team> teams = teamDao.findAll();

		List<TeamDto> teamsDto = new ArrayList<TeamDto>();
		for (Team team : teams) {
			teamsDto.add(modelMapper.map(team, TeamDto.class));
		}
		return teamsDto;
	}

	@Override
	@Transactional
	public TeamDto findById(int teamId) {
		Optional<Team> result = teamDao.findById(teamId);

		TeamDto teamDto = null;
		if (result.isPresent()) {

			teamDto = modelMapper.map(result.get(), TeamDto.class);
		}

		return teamDto;
	}

	@Override
	@Transactional
	public void save(TeamDto teamDto) {
		Team team = modelMapper.map(teamDto, Team.class);
		teamDao.save(team);

	}

	@Override
	@Transactional
	public boolean update(TeamDto teamDto) {
		Optional<Team> result = teamDao.findById(teamDto.getId());

		if (result.isPresent()) {
			Team oldTeam = result.get();
			Team newTeam = modelMapper.map(teamDto, Team.class);

			// update only if you have the last version
			int oldVersion = oldTeam.getVersion();
			if (oldVersion == newTeam.getVersion()) {
				oldTeam.setVersion(oldVersion + 1);
				oldTeam.setName(newTeam.getName());

				oldTeam.getEmployees().clear();
				oldTeam.getEmployees().addAll(newTeam.getEmployees());

				teamDao.save(oldTeam);
				return true;
			}

			else {

				throw new RuntimeException("You are trying to update an older version of this team, db:" + oldVersion
						+ ", your object: " + newTeam.getVersion());

			}
		} else {
			throw new NullPointerException("Error, team not found in the db");
		}
	}

	@Override
	@Transactional
	public void delete(int teamId) {
		Optional<Team> result = teamDao.findById(teamId);
		if (result.isPresent()) {
			teamDao.deleteById(teamId);
		} else {
			throw new NullPointerException("Error, team not found in the db");
		}

	}

	@Override
	public String populateDB() {
		
		return "ok";

	}

	public static Date between(Date startInclusive, Date endExclusive) {
		long startMillis = startInclusive.getTime();
		long endMillis = endExclusive.getTime();
		long randomMillisSinceEpoch = ThreadLocalRandom.current().nextLong(startMillis, endMillis);

		return new Date(randomMillisSinceEpoch);
	}
}
