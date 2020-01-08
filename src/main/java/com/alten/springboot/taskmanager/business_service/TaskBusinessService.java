package com.alten.springboot.taskmanager.business_service;

import java.util.List;

import com.alten.springboot.taskmanager.dto.TaskDto;

public interface TaskBusinessService {
	
	public List<TaskDto> findAll();
	
	public TaskDto findById(int taskId);
	
	public TaskDto save(TaskDto task);
	
	public boolean update(TaskDto task);
	
	public void delete(int taskId);

	public  List<TaskDto> findByEmployeeId(int employeeId);
	
	

}
