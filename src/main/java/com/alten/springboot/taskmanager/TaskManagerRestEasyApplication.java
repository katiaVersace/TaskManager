package com.alten.springboot.taskmanager;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.alten.springboot.taskmanager.controller.EmployeeController;

import io.swagger.jaxrs.config.BeanConfig;

@ApplicationPath("/resteasy/")
public class TaskManagerRestEasyApplication extends Application {
	
	public TaskManagerRestEasyApplication() {
		 BeanConfig beanConfig = new BeanConfig();
	        beanConfig.setVersion("1.0.2");
	        beanConfig.setSchemes(new String[] { "http" });
	        beanConfig.setHost("localhost:8080");
	        beanConfig.setBasePath("/resteasy");
	        beanConfig.setResourcePackage("io.swagger.resources,io.swagger.jaxrs.listing,"+
	                EmployeeController.class.getPackage().getName());
	        
	        beanConfig.setDescription("Task Manager API Documentation");
	        beanConfig.setScan(true);
	        
	}

}
