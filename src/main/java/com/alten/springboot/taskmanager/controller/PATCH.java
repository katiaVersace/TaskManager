package com.alten.springboot.taskmanager.controller;

import java.lang.annotation.Target;

import javax.ws.rs.HttpMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.ElementType;

/*
 * To enable PATCH for RestEasy we need to define a annotation annotated with @HttpMethod
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("PATCH")
public @interface PATCH {
}