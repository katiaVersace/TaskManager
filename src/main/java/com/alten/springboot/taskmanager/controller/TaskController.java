package com.alten.springboot.taskmanager.controller;

import java.util.List;

import javax.ws.rs.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.alten.springboot.taskmanager.businessservice.ITaskBusinessService;
import com.alten.springboot.taskmanager.dto.TaskDto;

@Component
public class TaskController implements ITaskController {

	@Autowired
	private ITaskBusinessService taskService;

	@Override
	public List<TaskDto> getTasks() {
		return taskService.findAll();
	}

	@Override
	public TaskDto getTask( String taskId) {

		TaskDto theTask = taskService.findById(Integer.parseInt(taskId));

		return theTask;
	}

	@Override
	public TaskDto addTask(TaskDto theTask) {
		theTask.setId(0); // cioè inserisco, perche provo ad aggiornare ma l'id 0 non esiste
		theTask = taskService.save(theTask);
		return theTask;
	}

	@Override
	public TaskDto updateTaskAdmin(@RequestBody TaskDto theTask) {

		taskService.update(theTask);
		return theTask;
	}

	@Override
	public TaskDto updateTask(@RequestBody TaskDto theTask) {
		TaskDto oldTask = taskService.findById(theTask.getId());

		if (theTask.getRealStartTime() != null) {
			oldTask.setRealStartTime(theTask.getRealStartTime());
		}
		if (theTask.getRealEndTime() != null) {
			oldTask.setRealEndTime(theTask.getRealEndTime());
		}


		taskService.update(oldTask);

		return oldTask;
	}

	@Override
	public String deleteTask(String taskId) {

		taskService.delete(Integer.parseInt(taskId));

		return "Deleted task with id: " + taskId;

	}

	@Override
	public List<TaskDto> getTasksByEmployeeId( String employeeId) {

		List<TaskDto> tasks = taskService.findByEmployeeId(Integer.parseInt(employeeId));
		return tasks;
	}

}
