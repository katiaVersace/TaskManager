package com.alten.springboot.taskmanager.service;



import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.alten.springboot.taskmanager.dto.EmployeeDto;

public interface EmployeeService extends UserDetailsService 
{

    EmployeeDto findByUserName(String userName);
    
    List<EmployeeDto> findAll();

    EmployeeDto findById(int employeeId);
    
    void save(EmployeeDto employee);
    
    boolean update(EmployeeDto employee);
    
    void delete(int employeeId);
    
  
   
    
   

	
 
}
