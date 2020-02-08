package com.alten.springboot.taskmanager.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.alten.springboot.taskmanager.config.PrincipalUser;
import com.alten.springboot.taskmanager.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.alten.springboot.taskmanager.businessservice.IEmployeeBusinessService;
import com.alten.springboot.taskmanager.dto.AvailabilityByEmployeeInputDto;
import com.alten.springboot.taskmanager.dto.EmployeeDto;
import com.alten.springboot.taskmanager.dto.RoleDto;
import com.alten.springboot.taskmanager.dto.TaskDto;
import org.springframework.web.bind.annotation.RequestParam;

@Component

public class EmployeeController implements IEmployeeController {

	@Autowired
	private IEmployeeBusinessService employeeService;


	@Override
	public List<EmployeeDto> getEmployees() {

		return employeeService.findAll();
	}

	@Override
	public EmployeeDto getEmployee( int employeeId) {
		EmployeeDto theEmployee = employeeService.findById(employeeId);

		return theEmployee;
	}

	@Override
	public EmployeeDto addEmployee(int admin, EmployeeDto theEmployee) {

		theEmployee.setId(0); // cioè inserisco, perche provo ad aggiornare ma l'id 0 non esiste
		
		RoleDto employeeRole = new RoleDto();
		employeeRole.setId(1);
		theEmployee.getRoles().add(employeeRole);
		
		if(admin == 1) {
			RoleDto adminRole = new RoleDto();
			adminRole.setId(2);
			theEmployee.getRoles().add(adminRole);
		}
		return employeeService.save(theEmployee);
	}
	
	

	@Override
	public EmployeeDto updateEmployee(@RequestBody EmployeeDto theEmployee) {
		employeeService.update(theEmployee);

		return theEmployee;
	}

	@Override
	public String deleteEmployee( String employeeId, HttpServletRequest request) {


		employeeService.delete(Integer.parseInt(employeeId));
		int userId= ((PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

		if (Integer.parseInt(employeeId) == userId) {

			SecurityContextHolder.clearContext();
			if (request.getSession() != null) {
				request.getSession().invalidate();
			}
			return "redirect:/auth/logout";
		} else
			return "Deleted employee with id: " + employeeId;

	}

	@Override
	public List<EmployeeDto> getAvailableEmployeesByTeamAndTask(int teamId, TaskDto theTask) {
		return employeeService.getAvailableEmployeesByTeamAndTask(teamId, theTask);

	}

	@Override
	public String getAvailabilityByEmployee(AvailabilityByEmployeeInputDto input) {
		return employeeService.getAvailabilityByEmployee(input.getEmployee_id(), input.getStart(), input.getEnd());
	}

}
