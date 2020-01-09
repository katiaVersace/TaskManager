package com.alten.springboot.taskmanager.data_service;

import java.util.List;

import com.alten.springboot.taskmanager.entity.Team;

public interface TeamDataService {
	public List<Team> findAll();
	
	public Team findById(int teamId);
    
	public Team save(Team team);
    
	public Team update(Team team);
    
	public void delete(int teamId);
    	
	public void deleteAll();
}
