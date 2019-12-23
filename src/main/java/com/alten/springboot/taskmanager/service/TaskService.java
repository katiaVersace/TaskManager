package com.alten.springboot.taskmanager.service;

import java.util.List;

import com.alten.springboot.taskmanager.dto.TaskDto;

public interface TaskService {
	
	public List<TaskDto> findAll();
	
	public TaskDto findById(int taskId);
	
	public void save(TaskDto task);
	
	public boolean update(TaskDto task);
	
	public void delete(int taskId);

	public  List<TaskDto> findByEmployeeId(int employeeId);
	
	

}
