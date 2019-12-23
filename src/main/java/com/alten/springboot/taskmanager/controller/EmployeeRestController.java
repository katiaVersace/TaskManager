package com.alten.springboot.taskmanager.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.alten.springboot.taskmanager.dto.EmployeeDto;
import com.alten.springboot.taskmanager.service.EmployeeService;


@Component
public class EmployeeRestController implements IEmployeeRestController {

	@Autowired
	private EmployeeService employeeService;

	
	
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
