package com.alten.springboot.taskmanager.controller;

import java.lang.annotation.Target;

import javax.ws.rs.HttpMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.ElementType;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("PATCH")
public @interface PATCH {
}