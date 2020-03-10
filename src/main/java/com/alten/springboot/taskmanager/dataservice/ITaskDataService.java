package com.alten.springboot.taskmanager.dataservice;

import java.util.List;

import com.alten.springboot.taskmanager.model.Task;

public interface ITaskDataService {

    public List<Task> findAll();

    public Task findById(int taskId);

    public Task save(Task task);

    public List<Task> saveAll(List<Task> tasks);

    public Task update(Task task);

    public void delete(int taskId);

    public List<Task> findByEmployeeId(int employeeId);

    public void deleteAll();


}
