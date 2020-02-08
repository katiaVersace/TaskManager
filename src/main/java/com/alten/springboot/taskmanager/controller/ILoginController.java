package com.alten.springboot.taskmanager.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.alten.springboot.taskmanager.dto.EmployeeDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Login Management System", description = "Operations pertaining to Login")
@RestController
@RequestMapping("/auth")
public interface ILoginController {
	
	@ApiOperation(value = "Login", response = EmployeeDto.class)
	@PostMapping(value = "/login")
	public EmployeeDto login( @FormParam("username") String username, @FormParam("password")  String password, HttpServletRequest request);

	@ApiOperation(value = "Logout", response = String.class)
	@GetMapping("/logout")
	public String logout(HttpServletRequest request) ;

}
