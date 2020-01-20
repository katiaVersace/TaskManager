package com.alten.springboot.taskmanager.dataservice;

import java.util.List;

import com.alten.springboot.taskmanager.model.Team;

public interface ITeamDataService {
	
	public List<Team> findAll();
	
	public Team findById(int teamId);
    
	public Team save(Team team);
    
	public Team update(Team team);
    
	public void delete(int teamId);
    	
	public void deleteAll();
}
