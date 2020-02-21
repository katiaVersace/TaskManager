package com.alten.springboot.taskmanager.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Api(value = "Task Management System", description = "Default operations in Task Management System")
@RestController
class DefaultController {

    @GetMapping("/")
    @ApiOperation(value = "Home", response = String::class)
    fun getHome(): String ="Home"

    @ApiOperation(value = "CRSF token", response = CsrfToken::class)
    @GetMapping("/csrf")
    fun csrf(token: CsrfToken): CsrfToken = token

}