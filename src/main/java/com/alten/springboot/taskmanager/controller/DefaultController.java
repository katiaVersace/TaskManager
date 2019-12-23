package com.alten.springboot.taskmanager.controller;


import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Task Management System", description = "Default operations in Task Management System")
@RestController
public class DefaultController {

	@ApiOperation(value = "Home", response = String.class)
	@GetMapping("/")
	public String getHome() {
		
		return "Home";
		
	}
	
	@ApiOperation(value = "CRSF token", response = CsrfToken.class)
	@GetMapping("/csrf")
	public CsrfToken csrf(CsrfToken token) {
	    return token;
	
		
	}
	
	
}
