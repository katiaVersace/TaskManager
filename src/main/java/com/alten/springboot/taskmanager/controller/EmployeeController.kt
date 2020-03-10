package com.alten.springboot.taskmanager.controller

import com.alten.springboot.taskmanager.businessservice.IEmployeeBusinessService
import com.alten.springboot.taskmanager.config.PrincipalUser
import com.alten.springboot.taskmanager.dto.AvailabilityByEmployeeInputDto
import com.alten.springboot.taskmanager.dto.EmployeeDto
import com.alten.springboot.taskmanager.dto.RoleDto
import com.alten.springboot.taskmanager.dto.TaskDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestBody
import javax.servlet.http.HttpServletRequest

@Component
open class EmployeeController(@Autowired private val employeeService: IEmployeeBusinessService) : IEmployeeController {

    override fun getEmployees(): List<EmployeeDto?>? {
        return employeeService!!.findAll()
    }

    override fun getEmployee(employeeId: Int): EmployeeDto? {
        return employeeService!!.findById(employeeId)
    }

    override fun addEmployee(admin: Int, theEmployee: EmployeeDto?): EmployeeDto? {
        theEmployee!!.id = 0 // cioè inserisco, perche provo ad aggiornare ma l'id 0 non esiste
        val employeeRole = RoleDto()
        employeeRole.id = 1
        theEmployee.roles.add(employeeRole)
        if (admin == 1) {
            val adminRole = RoleDto()
            adminRole.id = 2
            theEmployee.roles.add(adminRole)
        }
        return employeeService!!.save(theEmployee)
    }

    override fun updateEmployee(@RequestBody theEmployee: EmployeeDto?): EmployeeDto? {
        employeeService!!.update(theEmployee)
        return theEmployee
    }

    override fun deleteEmployee(employeeId: String?, request: HttpServletRequest?): String? {
        employeeService!!.delete(employeeId!!.toInt())
        val userId = (SecurityContextHolder.getContext().authentication.principal as PrincipalUser).id
        return if (employeeId.toInt() == userId) {
            SecurityContextHolder.clearContext()
            if (request!!.session != null) {
                request.session.invalidate()
            }
            "redirect:/auth/logout"
        } else "Deleted employee with id: $employeeId"
    }

    override fun getAvailableEmployeesByTeamAndTask(teamId: Int, theTask: TaskDto?): List<EmployeeDto?>? {
        return employeeService!!.getAvailableEmployeesByTeamAndTask(teamId, theTask)
    }

    override fun getAvailabilityByEmployee(input: AvailabilityByEmployeeInputDto?): String? {
        return employeeService!!.getAvailabilityByEmployee(input!!.employee_id, input.start, input.end)
    }
}