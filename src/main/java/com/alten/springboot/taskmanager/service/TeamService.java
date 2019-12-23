package com.alten.springboot.taskmanager.service;

import java.util.Date;
import java.util.List;

import com.alten.springboot.taskmanager.dto.TeamDto;

public interface TeamService {
	public List<TeamDto> findAll();
	
	TeamDto findById(int teamId);
    
    void save(TeamDto team);
    
    boolean update(TeamDto team);
    
    void delete(int teamId);
    
    String populateDB();
}
