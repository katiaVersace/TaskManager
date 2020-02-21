package com.alten.springboot.taskmanager.controller

import com.alten.springboot.taskmanager.dto.AvailabilityByEmployeeInputDto
import com.alten.springboot.taskmanager.dto.EmployeeDto
import com.alten.springboot.taskmanager.dto.TaskDto
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Api(value = "Employee Management System", description = "Operations pertaining to employee in Employee Management System")
@RestController
@RequestMapping("/employees")
interface IEmployeeController {

    @ApiOperation(value = "View a list of available employees", response = MutableList::class)
    @GetMapping(produces = ["application/json"])
    fun getEmployees(): List<EmployeeDto?>?

    @ApiOperation(value = "Get an employee by Id", response = EmployeeDto::class)
    @GetMapping(value = ["/{employeeId}"], produces = ["application/json"])
    fun getEmployee(
            @ApiParam(value = "Employee Id from which employee object will retrieve", required = true) @PathVariable("employeeId") employeeId: Int): EmployeeDto?

    @ApiOperation(value = "Add an employee, allowed only to ADMIN employees", response = EmployeeDto::class)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = ["/{admin}"], consumes = ["application/json"], produces = ["application/json"])
    fun addEmployee(
            @ApiParam(value = "Variable to distinguish if it's saving admin or not", required = true) @PathVariable("admin") admin: Int,
            @ApiParam(value = "Employee object store in database table", required = true) @RequestBody theEmployee: EmployeeDto?): EmployeeDto?

    @ApiOperation(value = "Update an employee, allowed only to the Admin or the interested Employee", response = EmployeeDto::class)
    @PreAuthorize("@securityDataService.isOwner(principal.id,#theEmployee.getId()) or hasRole('ROLE_ADMIN')")
    @PutMapping(consumes = ["application/json"], produces = ["application/json"])
    fun updateEmployee(
            @ApiParam(value = "Updated Employee object to store in database table", required = true) @RequestBody theEmployee: EmployeeDto?): EmployeeDto?

    @ApiOperation(value = "Delete an employee, allowed only to ADMIN employees", response = String::class)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = ["/{employeeId}"])
    fun deleteEmployee(
            @ApiParam(value = "Employee Id from which employee object will delete from database table", required = true) @PathVariable("employeeId") employeeId: String?, request: HttpServletRequest?): String?

    @ApiOperation(value = "View a list of available employees in a Team for a Task", response = MutableList::class)
    @GetMapping(value = ["/employeesByTeamAndTask/{teamId}"], consumes = ["application/json"], produces = ["application/json"])
    fun getAvailableEmployeesByTeamAndTask(@ApiParam(value = "Team id object store in database table", required = true) @PathVariable("teamId") teamId: Int,
                                           @ApiParam(value = "Task object store in database table", required = true) @RequestBody theTask: TaskDto?): List<EmployeeDto?>?

    @ApiOperation(value = "View availability and tasks for an Employee", response = MutableList::class)
    @GetMapping(value = ["/availability"], consumes = ["application/json"], produces = ["application/json"])
    fun getAvailabilityByEmployee(@ApiParam(value = "input", required = true) @RequestBody input: AvailabilityByEmployeeInputDto?): String?
}