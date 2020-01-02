package com.alten.springboot.taskmanager.business_service;

import java.util.List;

import com.alten.springboot.taskmanager.dto.TaskDto;
import com.alten.springboot.taskmanager.dto.TeamDto;

public interface TeamBusinessService {
	public List<TeamDto> findAll();
	
	TeamDto findById(int teamId);
    
    void save(TeamDto team);
    
    boolean update(TeamDto team);
    
    void delete(int teamId);
    
    String populateDB();

	String randomPopulation(String start, String end, int teams_size, int employees_size, int tasks_size,
			int task_max_duration);

	boolean tryAssignTaskToTeam(String start, String end, int team_id, TaskDto theTask);
}
