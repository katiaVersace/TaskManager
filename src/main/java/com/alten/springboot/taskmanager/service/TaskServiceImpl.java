package com.alten.springboot.taskmanager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.springboot.taskmanager.dao.EmployeeRepository;
import com.alten.springboot.taskmanager.dao.TaskRepository;
import com.alten.springboot.taskmanager.dto.TaskDto;
import com.alten.springboot.taskmanager.entity.Employee;
import com.alten.springboot.taskmanager.entity.Task;

@Service
public class TaskServiceImpl implements TaskService {

	@Autowired
	private TaskRepository taskDao;

	@Autowired
	private EmployeeRepository employeeDao;
	
	@Autowired
	private ModelMapper modelMapper;

	@Override
	@Transactional
	public TaskDto findById(int taskId) {
		Optional<Task> result = taskDao.findById(taskId);

		TaskDto taskDtoLight = null;
		if (result.isPresent()) {
			taskDtoLight = modelMapper.map(result.get(), TaskDto.class);
		}
		return taskDtoLight;

	}

	@Override
	@Transactional
	public void save(TaskDto taskDtoLight) {
		Task task = modelMapper.map(taskDtoLight, Task.class);
		Employee employee = employeeDao.findById(taskDtoLight.getEmployeeId()).get();
		
		
		List<Task> tasks = employee.getTasks();
		tasks.add(task);

		if (tasks.size() >= 5) {

			employee.setTopEmployee(true);
		}
		
	
		task.setEmployee(employee);
		taskDao.save(task);
		employeeDao.save(employee);

	}

	@Override
	@Transactional
	public boolean update(TaskDto TaskDtoLight) {

		Optional<Task> result = taskDao.findById(TaskDtoLight.getId());
		if (result.isPresent()) {
			Task oldTask = result.get();
			Task newTask = modelMapper.map(TaskDtoLight, Task.class);
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
				 throw new RuntimeException("You are trying to update an older version of this task, db:" + oldVersion +", your object: " + newTask.getVersion());
			}
		} else {
			throw new NullPointerException("Error, employee not found in the db");
		}

	}

	@Override
	@Transactional
	public void delete(int taskId) {
		Optional<Task> result = taskDao.findById(taskId);
		
		if(result.isPresent()) {
		Task task = result.get();
		Employee employee = employeeDao.findById(task.getEmployee().getId()).get();
		List<Task> tasks = employee.getTasks();
		tasks.remove(task);
		
		if (tasks.size() < 5) {
			
			employee.setTopEmployee(false);
		}
		employeeDao.save(employee);
		taskDao.deleteById(taskId);
		}
		else throw new NullPointerException("Task not found");

	}

	@Override
	public List<TaskDto> findAll() {
		List<Task> tasks = taskDao.findAll();
		
		List<TaskDto> tasksDto = new ArrayList<TaskDto>();
		for (Task task : tasks) {
			tasksDto.add(modelMapper.map(task, TaskDto.class));
		}
		return tasksDto;
	}

	@Override
	public List<TaskDto> findByEmployeeId(int employeeId) {
		List<Task> tasks = taskDao.findByEmployeeId(employeeId);
		List<TaskDto> tasksDto = new ArrayList<TaskDto>();
		for (Task task : tasks) {
			tasksDto.add(modelMapper.map(task, TaskDto.class));
		}
		return tasksDto;
	}

}
