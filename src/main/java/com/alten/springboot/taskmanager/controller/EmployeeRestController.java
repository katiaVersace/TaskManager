package com.alten.springboot.taskmanager.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.alten.springboot.taskmanager.business_service.EmployeeBusinessService;
import com.alten.springboot.taskmanager.dto.EmployeeDto;


@Component
public class EmployeeRestController implements IEmployeeRestController {

	@Autowired
	private EmployeeBusinessService employeeService;

	
	
	private @Context  HttpServletRequest request;

//	@Override
//	public String getHello() {
//
//		return "Home";
//	}

	@Override
	public List<EmployeeDto> getEmployees() {

		return employeeService.findAll();
	}

	@Override
	public EmployeeDto getEmployee(@PathParam("employeeId") int employeeId) {
		EmployeeDto theEmployee = employeeService.findById(employeeId);

		return theEmployee;
	}

	@Override
	public EmployeeDto addEmployee(@RequestBody EmployeeDto theEmployee) {
		theEmployee.setId(0); // cioè inserisco, perche provo ad aggiornare ma l'id 0 non esiste
		employeeService.save(theEmployee);
		return theEmployee;
	}

	@Override
	public EmployeeDto updateEmployee(@RequestBody EmployeeDto theEmployee) {
		employeeService.update(theEmployee);

		return theEmployee;
	}

	@Override
	public String deleteEmployee(@PathParam("employeeId") String employeeId) {

		employeeService.delete(Integer.parseInt(employeeId));

		HttpSession session = request.getSession();
		int userId = ((EmployeeDto) session.getAttribute("user")).getId();
		if (Integer.parseInt(employeeId) == userId) {
			SecurityContextHolder.clearContext();
			if (session != null) {
				session.invalidate();
			}
			return "redirect:/logout";
		} else
			return "Deleted employee with id: " + employeeId;

	}

	



}
