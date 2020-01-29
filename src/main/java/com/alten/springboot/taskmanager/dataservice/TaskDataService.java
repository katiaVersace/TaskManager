package com.alten.springboot.taskmanager.dataservice;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.springboot.taskmanager.dao.TaskRepository;
import com.alten.springboot.taskmanager.model.Task;

@Service
public class TaskDataService implements ITaskDataService {

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
	public Task save(Task task) {

		return taskDao.save(task);

	}

	@Override
	@Transactional
	public Task update(Task newTask) {


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


				return taskDao.save(oldTask);

			}

			else {
				throw new RuntimeException("You are trying to update an older version of this task, db:" + oldVersion
						+ ", your object: " + newTask.getVersion());
			}
		} else {
			throw new NullPointerException("Error, task not found in the db");
		}

	}

	@Override
	@Transactional
	public void delete(int taskId) {
		Optional<Task> result = taskDao.findById(taskId);

		if (result.isPresent()) {
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

	@Override
	@Transactional
	public void deleteAll() {
		taskDao.deleteAll();
	}

}
