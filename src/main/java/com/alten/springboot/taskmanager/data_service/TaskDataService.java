package com.alten.springboot.taskmanager.data_service;

import java.util.List;

import com.alten.springboot.taskmanager.entity.Task;

public interface TaskDataService {
	
	public List<Task> findAll();
	
	public Task findById(int taskId);
	
	public Task save(Task task);
	
	public Task update(Task task);
	
	public void delete(int taskId);

	public  List<Task> findByEmployeeId(int employeeId);
	
	public void deleteAll();

}
