package com.alten.springboot.taskmanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alten.springboot.taskmanager.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
