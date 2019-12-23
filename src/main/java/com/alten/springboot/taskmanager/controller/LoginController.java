package com.alten.springboot.taskmanager.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import com.alten.springboot.taskmanager.data_service.EmployeeDataService;
import com.alten.springboot.taskmanager.dto.EmployeeDto;

@Component
public class LoginController implements ILoginController {
	
	@Autowired
	private EmployeeDataService employeeService;
	
	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private ModelMapper modelMapper;
	
	private @Context  HttpServletRequest request;
	
	@Override
	public EmployeeDto login( @FormParam("username") String username, @FormParam("password")  String password) {
		
		UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username, password);
		try {
			Authentication auth = authManager.authenticate(authReq);

			SecurityContext sc = SecurityContextHolder.getContext();
			sc.setAuthentication(auth);
			HttpSession session = request.getSession(true);

			EmployeeDto theUser = modelMapper.map(employeeService.findByUserName(username), EmployeeDto.class);

			session.setAttribute("user", theUser);
			session.setAttribute("SPRING_SECURITY_CONTEXT", sc);

			return theUser;
		} catch (Exception ex) {
			return null;
		}

	}

	@Override
	public String logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){    
            new SecurityContextLogoutHandler().logout(request, null, auth);
        }
    
     return "logout";
    }

}
