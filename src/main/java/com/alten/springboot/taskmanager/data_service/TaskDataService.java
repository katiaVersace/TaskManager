package com.alten.springboot.taskmanager.data_service;

import java.util.List;

import com.alten.springboot.taskmanager.entity.Task;

public interface TaskDataService {
	
	public List<Task> findAll();
	
	public Task findById(int taskId);
	
	public void save(Task task);
	
	public boolean update(Task task);
	
	public void delete(int taskId);

	public  List<Task> findByEmployeeId(int employeeId);
	
	

}
