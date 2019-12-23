package com.alten.springboot.taskmanager.data_service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.springboot.taskmanager.dao.TaskRepository;
import com.alten.springboot.taskmanager.entity.Task;

@Service
public class TaskDataServiceImpl implements TaskDataService {

	@Autowired
	private TaskRepository taskDao;

	
	@Override
	@Transactional
	public Task findById(int taskId) {
		Optional<Task> result = taskDao.findById(taskId);
		return result.get();

	}

	@Override
	@Transactional
	public void save(Task task) {
		
		taskDao.save(task);

	}

	@Override
	@Transactional
	public boolean update(Task newTask) {

		Optional<Task> result = taskDao.findById(newTask.getId());
		if (result.isPresent()) {
			Task oldTask = result.get();
		
			// update only if I have the last version
			int oldVersion = oldTask.getVersion();
			if (oldVersion == newTask.getVersion()) {
				oldTask.setVersion(oldVersion + 1);
				oldTask.setDescription(newTask.getDescription());
				oldTask.setExpectedStartTime(newTask.getExpectedStartTime());
				oldTask.setRealStartTime(newTask.getRealStartTime());
				oldTask.setExpectedEndTime(newTask.getExpectedEndTime());
				oldTask.setRealEndTime(newTask.getRealEndTime());

				taskDao.save(oldTask);
				return true;
			}

			else {
				throw new RuntimeException("You are trying to update an older version of this task, db:" + oldVersion
						+ ", your object: " + newTask.getVersion());
			}
		} else {
			throw new NullPointerException("Error, employee not found in the db");
		}

	}

	@Override
	@Transactional
	public void delete(int taskId) {
		System.out.println("Delete task "+taskId);
		Optional<Task> result = taskDao.findById(taskId);

		if (result.isPresent()) {
			System.out.println("Delete task 1");
			taskDao.deleteById(taskId);
		} else
			throw new NullPointerException("Task not found");

	}

	@Override
	@Transactional
	public List<Task> findAll() {
		List<Task> tasks = taskDao.findAll();

		
		return tasks;
	}

	@Override
	@Transactional
	public List<Task> findByEmployeeId(int employeeId) {
		List<Task> tasks = taskDao.findByEmployeeId(employeeId);
		return tasks;
	}

}
