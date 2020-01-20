package com.alten.springboot.taskmanager.businessservice;

import java.util.List;

import com.alten.springboot.taskmanager.dto.EmployeeDto;
import com.alten.springboot.taskmanager.dto.TaskDto;

public interface IEmployeeBusinessService 
{

    public EmployeeDto findByUserName(String userName);
    
    public List<EmployeeDto> findAll();

    public EmployeeDto findById(int employeeId);
    
    public void save(EmployeeDto employee);
    
    public boolean update(EmployeeDto employee);
    
    public void delete(int employeeId);

    public List<EmployeeDto> getAvailableEmployeesByTeamAndTask(int teamId, TaskDto theTask);

    public String getAvailabilityByEmployee(int employeeId, String start, String end);
    
  
   
    
   

	
 
}
