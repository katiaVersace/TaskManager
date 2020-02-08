package com.alten.springboot.taskmanager.controller;

import com.alten.springboot.taskmanager.dto.AvailabilityByEmployeeInputDto;
import com.alten.springboot.taskmanager.dto.EmployeeDto;
import com.alten.springboot.taskmanager.dto.TaskDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "Employee Management System", description = "Operations pertaining to employee in Employee Management System")

@RestController
@RequestMapping("/employees")
public interface IEmployeeController {

    @ApiOperation(value = "View a list of available employees", response = List.class)
    @GetMapping(produces = "application/json")
    public List<EmployeeDto> getEmployees();

    @ApiOperation(value = "Get an employee by Id", response = EmployeeDto.class)
    @GetMapping(value = "/{employeeId}", produces = "application/json")
    public EmployeeDto getEmployee(
            @ApiParam(value = "Employee Id from which employee object will retrieve", required = true) @PathVariable("employeeId") int employeeId);

    @ApiOperation(value = "Add an employee, allowed only to ADMIN employees", response = EmployeeDto.class)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/{admin}", consumes = "application/json", produces = "application/json")
    public EmployeeDto addEmployee(
            @ApiParam(value = "Variable to distinguish if it's saving admin or not", required = true) @PathVariable("admin") int admin,
            @ApiParam(value = "Employee object store in database table", required = true) @RequestBody EmployeeDto theEmployee);


    @ApiOperation(value = "Update an employee, allowed only to the Admin or the interested Employee", response = EmployeeDto.class)
    @PreAuthorize("@securityDataService.isOwner(principal.id,#theEmployee.getId()) or hasRole('ROLE_ADMIN')")
    @PutMapping(consumes = "application/json", produces = "application/json")
    public EmployeeDto updateEmployee(
            @ApiParam(value = "Updated Employee object to store in database table", required = true) @RequestBody EmployeeDto theEmployee);

    @ApiOperation(value = "Delete an employee, allowed only to ADMIN employees", response = String.class)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{employeeId}")
    public String deleteEmployee(
            @ApiParam(value = "Employee Id from which employee object will delete from database table", required = true) @PathVariable("employeeId") String employeeId, HttpServletRequest request);

    @ApiOperation(value = "View a list of available employees in a Team for a Task", response = List.class)
    @GetMapping(value = "/employeesByTeamAndTask/{teamId}", consumes = "application/json", produces = "application/json")
    public List<EmployeeDto> getAvailableEmployeesByTeamAndTask(@ApiParam(value = "Team id object store in database table", required = true) @PathVariable("teamId") int teamId,
                                                                @ApiParam(value = "Task object store in database table", required = true) @RequestBody  TaskDto theTask);


    @ApiOperation(value = "View availability and tasks for an Employee", response = List.class)
    @GetMapping(value = "/availability",  consumes = "application/json", produces = "application/json")
    public String getAvailabilityByEmployee(@ApiParam(value = "input", required = true) @RequestBody  AvailabilityByEmployeeInputDto input);


}
