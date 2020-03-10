package com.alten.springboot.taskmanager.dataservice;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.alten.springboot.taskmanager.model.Employee;

public interface IEmployeeDataService extends UserDetailsService {

    public Employee findByUserName(String userName);

    public List<Employee> findAll();

    public Employee findById(int employeeId);

    public Employee save(Employee employee);

    public List<Employee> saveAll(List<Employee> employees);

    public Employee update(Employee employee);

    public void delete(int employeeId);

}
