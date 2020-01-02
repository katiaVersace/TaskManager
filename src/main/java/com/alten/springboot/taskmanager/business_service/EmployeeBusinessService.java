package com.alten.springboot.taskmanager.business_service;



import java.util.List;

import com.alten.springboot.taskmanager.dto.EmployeeDto;
import com.alten.springboot.taskmanager.dto.TaskDto;
import com.alten.springboot.taskmanager.dto.TeamDto;

public interface EmployeeBusinessService 
{

    EmployeeDto findByUserName(String userName);
    
    List<EmployeeDto> findAll();

    EmployeeDto findById(int employeeId);
    
    void save(EmployeeDto employee);
    
    boolean update(EmployeeDto employee);
    
    void delete(int employeeId);

	List<EmployeeDto> getAvailableEmployeesByTeamAndTask(int teamId, TaskDto theTask);

	String getAvailabilityByEmployee(int employeeId, String start, String end);
    
  
   
    
   

	
 
}
