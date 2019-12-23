package com.alten.springboot.taskmanager.data_service;

import java.util.List;

import com.alten.springboot.taskmanager.entity.Team;

public interface TeamDataService {
	public List<Team> findAll();
	
	Team findById(int teamId);
    
    void save(Team team);
    
    boolean update(Team team);
    
    void delete(int teamId);
    
    String populateDB();
}
