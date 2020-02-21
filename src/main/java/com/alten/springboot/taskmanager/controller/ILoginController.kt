package com.alten.springboot.taskmanager.controller

import com.alten.springboot.taskmanager.dto.EmployeeDto
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.FormParam

@Api(value = "Login Management System", description = "Operations pertaining to Login")
@RestController
@RequestMapping("/auth")
interface ILoginController {
    @ApiOperation(value = "Login", response = EmployeeDto::class)
    @PostMapping(value = ["/login"])
    fun login(@FormParam("username") username: String?, @FormParam("password") password: String?, request: HttpServletRequest?): EmployeeDto?

    @ApiOperation(value = "Logout", response = String::class)
    @GetMapping("/logout")
    fun logout(request: HttpServletRequest?): String?
}