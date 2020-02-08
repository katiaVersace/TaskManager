package com.alten.springboot.taskmanager.controller;

import com.alten.springboot.taskmanager.dto.TaskDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "Task Management System", description = "Operations pertaining to task in Task Management System")
@RestController
@RequestMapping("/tasks")
public interface ITaskController {

    @ApiOperation(value = "View a list of available tasks", response = List.class)
    @GetMapping(produces = "application/json")
    public List<TaskDto> getTasks();

    @ApiOperation(value = "Get a task by Id", response = TaskDto.class)
    @GetMapping(value = "/{taskId}", produces = "application/json")
    public TaskDto getTask(
            @ApiParam(value = "Task id from which Task object will retrieve", required = true) @PathVariable("taskId") String taskId);

    @ApiOperation(value = "Add a task, allowed only to ADMIN employees", response = TaskDto.class)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public TaskDto addTask(@ApiParam(value = "Task object store in database table", required = true) @RequestBody TaskDto theTask);

    @ApiOperation(value = "Put a task, allowed only to ADMIN employees", response = TaskDto.class)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(consumes = "application/json", produces = "application/json")
    public TaskDto updateTaskAdmin(
            @ApiParam(value = "Updated Task object to store in database table", required = true) @RequestBody TaskDto theTask);

    @ApiOperation(value = "Patch a task, allowed only to ADMIN employees and to the owner of  the task", response = TaskDto.class)
    @PreAuthorize("@securityDataService.isOwner(principal.id,#theTask.getEmployeeId()) or hasRole('ROLE_ADMIN')")
    @PatchMapping(consumes = "application/json", produces = "application/json")
    public TaskDto updateTask(
            @ApiParam(value = "Updated Task object to store in database table", required = true) @RequestBody TaskDto theTask);

    @ApiOperation(value = "Delete a task, allowed only to ADMIN employees", response = String.class)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{taskId}", produces = "application/json")
    public String deleteTask(
            @ApiParam(value = "Task Id from which task object will delete from database table", required = true) @PathVariable("taskId") String taskId);

    @ApiOperation(value = "View a list of available tasks for a predefined employee", response = List.class)
    @GetMapping(value = "/tasksByEmployee/{employeeId}", produces = "application/json")
    public List<TaskDto> getTasksByEmployeeId(
            @ApiParam(value = "Employee id for which will retrieve the tasks", required = true) @PathVariable("employeeId") String employeeId);

}
