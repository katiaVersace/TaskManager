package com.alten.springboot.taskmanager.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alten.springboot.taskmanager.model.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    public List<Task> findByEmployeeId(int employeeId);

}
