package com.alten.springboot.taskmanager.business_service;

import java.time.LocalDate;
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
	public TaskDto save(TaskDto taskDto) {
		Task task = modelMapper.map(taskDto, Task.class);
		
		if(!checkDate(task)) return null;

		Employee employee = employeeDataService.findById(taskDto.getEmployeeId());
		taskDto = null;
		if (employeeAvailable(employee, task.getExpectedStartTime(), task.getExpectedEndTime())) {
			task.setEmployee(employee);
			List<Task> tasks = employee.getTasks();
			tasks.add(task);

			taskDto = modelMapper.map(taskDataService.save(task), TaskDto.class);

			if (tasks.size() >= 5) {

				employee.setTopEmployee(true);
				employeeDataService.update(employee);
			}
		}

		return taskDto;
	}

	@Override
	public boolean update(TaskDto taskDto) {

		Task task = modelMapper.map(taskDto, Task.class);
		// check sulle date
		if(!checkDate(task)) return false;
		
		if (taskDataService.update(task) != null)
			return true;
		else
			return false;
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

	public boolean betweenTwoDate(LocalDate toCheck, LocalDate start, LocalDate end) {
		boolean result = (toCheck.isAfter(start) && toCheck.isBefore(end)) || toCheck.equals(start)
				|| toCheck.equals(end);
		return result;
	}

	public boolean employeeAvailable(Employee e, LocalDate startTask, LocalDate endTask) {
		for (Task t : e.getTasks()) {
			if (betweenTwoDate(startTask, t.getExpectedStartTime(), t.getExpectedEndTime())
					|| betweenTwoDate(endTask, t.getExpectedStartTime(), t.getExpectedEndTime())
					|| betweenTwoDate(t.getExpectedStartTime(), startTask, endTask)
					|| betweenTwoDate(t.getExpectedEndTime(), startTask, endTask)) {
				return false;
			}

		}

		return true;
	}
	
	public boolean checkDate(Task theTask) {
		// check sulle date
		LocalDate today = LocalDate.now();
		if (!((theTask.getExpectedStartTime().isAfter(today) || theTask.getExpectedStartTime().equals(today))
				&& (theTask.getExpectedStartTime().isBefore(theTask.getExpectedEndTime())
						|| theTask.getExpectedStartTime().equals(theTask.getExpectedEndTime()))))
			return false;

		if (theTask.getRealStartTime() != null || theTask.getRealEndTime() != null) {
			if (!((theTask.getRealStartTime().isAfter(today)
					|| theTask.getRealStartTime().equals(today))
					&& (theTask.getRealStartTime().isBefore(theTask.getRealEndTime())
							|| theTask.getRealStartTime().equals(theTask.getRealEndTime()))))
				return false;
		}
		return  true;
	}
}
