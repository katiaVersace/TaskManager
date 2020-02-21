package com.alten.springboot.taskmanager.controller

import com.alten.springboot.taskmanager.dataservice.IEmployeeDataService
import com.alten.springboot.taskmanager.dto.EmployeeDto
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.FormParam

@Component
class LoginController(@Autowired private val employeeService: IEmployeeDataService,
                      @Autowired private val authManager: AuthenticationManager,
                      @Autowired private val modelMapper: ModelMapper) : ILoginController {

    override fun login(@FormParam("username") username: String?, @FormParam("password") password: String?, request: HttpServletRequest?): EmployeeDto? {
        val authReq = UsernamePasswordAuthenticationToken(username, password)
        return try {
            val auth = authManager!!.authenticate(authReq)
            val sc = SecurityContextHolder.getContext()
            sc.authentication = auth
            val session = request!!.getSession(true)
            val theUser = modelMapper!!.map(employeeService!!.findByUserName(username), EmployeeDto::class.java)
            session.setAttribute("user", theUser)
            session.setAttribute("SPRING_SECURITY_CONTEXT", sc)
            theUser
        } catch (ex: Exception) { //ex.printStackTrace();
            null
        }
    }

    override fun logout(request: HttpServletRequest?): String? {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth != null) {
            SecurityContextLogoutHandler().logout(request, null, auth)
        }
        return "logout"
    }
}