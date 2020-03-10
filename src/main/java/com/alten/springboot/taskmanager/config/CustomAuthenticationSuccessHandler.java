package com.alten.springboot.taskmanager.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.alten.springboot.taskmanager.dataservice.IEmployeeDataService;
import com.alten.springboot.taskmanager.dto.EmployeeDto;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private IEmployeeDataService employeeService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {


        String userName = authentication.getName();


        EmployeeDto theUser = modelMapper.map(employeeService.findByUserName(userName), EmployeeDto.class);

        // now place in the session
        HttpSession session = request.getSession();
        session.setAttribute("user", theUser);

        // forward to home page

        response.sendRedirect(request.getContextPath() + "/");
    }

}
