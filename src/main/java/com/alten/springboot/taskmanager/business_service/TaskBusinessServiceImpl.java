package com.alten.springboot.taskmanager.business_service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.springboot.taskmanager.data_service.EmployeeDataService;
import com.alten.springboot.taskmanager.data_service.TaskDataService;
import com.alten.springboot.taskmanager.dto.TaskDto;
import com.alten.springboot.taskmanager.entity.Employee;
import com.alten.springboot.taskmanager.entity.Task;

@Service
public class TaskBusinessServiceImpl implements TaskBusinessService {

	@Autowired
	private TaskDataService taskDataService;

	@Autowired
	private EmployeeDataService employeeDataService;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public TaskDto findById(int taskId) {
		Task task = taskDataService.findById(taskId);

		TaskDto taskDto = null;
		if (task != null) {
			taskDto = modelMapper.map(task, TaskDto.class);
		}
		return taskDto;

	}

	@Override
	public void save(TaskDto taskDto) {
		Task task = modelMapper.map(taskDto, Task.class);
		Employee employee = employeeDataService.findById(taskDto.getEmployeeId());
		task.setEmployee(employee);
		List<Task> tasks = employee.getTasks();
		tasks.add(task);
		taskDataService.save(task);

		if (tasks.size() >= 5) {

			employee.setTopEmployee(true);
			employeeDataService.update(employee);
		}

	}

	@Override
	public boolean update(TaskDto taskDto) {
		
		Task task = modelMapper.map(taskDto, Task.class);
		return taskDataService.update(task);
	}

	@Override
	public void delete(int taskId) {
		Task task = taskDataService.findById(taskId);
		Employee employee = employeeDataService.findById(task.getEmployee().getId());
		List<Task> tasks = employee.getTasks();

		Task taskToDelete = null;
		for (Task t : tasks) {
			if (t.getId() == taskId) {
				taskToDelete = t;
			}
		}

		if (taskToDelete != null) {
			tasks.remove(taskToDelete);
			taskToDelete.setEmployee(null);

			if (employee.getTasks().size() < 5) {
				employee.setTopEmployee(false);

			}
			employeeDataService.update(employee);
			// taskDataService.delete(taskId); //remove orphan
		}
	}

	@Override
	public List<TaskDto> findAll() {

		List<Task> tasks = taskDataService.findAll();

		List<TaskDto> tasksDto = new ArrayList<TaskDto>();
		for (Task task : tasks) {
			tasksDto.add(modelMapper.map(task, TaskDto.class));
		}
		return tasksDto;

	}

	@Override
	public List<TaskDto> findByEmployeeId(int employeeId) {
		List<Task> tasks = taskDataService.findByEmployeeId(employeeId);
		List<TaskDto> tasksDto = new ArrayList<TaskDto>();
		for (Task task : tasks) {
			tasksDto.add(modelMapper.map(task, TaskDto.class));
		}
		return tasksDto;
	}

}
