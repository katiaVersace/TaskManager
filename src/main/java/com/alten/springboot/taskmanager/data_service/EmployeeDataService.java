package com.alten.springboot.taskmanager.data_service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.alten.springboot.taskmanager.entity.Employee;

public interface EmployeeDataService extends UserDetailsService {

	public Employee findByUserName(String userName);

	public List<Employee> findAll();

	public Employee findById(int employeeId);

	public Employee save(Employee employee);

	public Employee update(Employee employee);

	public void delete(int employeeId);

}
