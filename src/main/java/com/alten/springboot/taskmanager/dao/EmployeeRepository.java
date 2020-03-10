package com.alten.springboot.taskmanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alten.springboot.taskmanager.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    public Employee findByUserName(String userName);

}











