package com.alten.springboot.taskmanager.data_service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alten.springboot.taskmanager.config.PrincipalUser;
import com.alten.springboot.taskmanager.dao.EmployeeRepository;
import com.alten.springboot.taskmanager.dao.RoleRepository;
import com.alten.springboot.taskmanager.entity.Employee;
import com.alten.springboot.taskmanager.entity.Role;

@Service
public class EmployeeDataServiceImpl implements EmployeeDataService {

	@Autowired
	private EmployeeRepository employeeDao;

	@Autowired
	private RoleRepository roleDao;

	@Override
	@Transactional
	public Employee findByUserName(String userName) {

		Employee employee = employeeDao.findByUserName(userName);
		return employee;

	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		Employee employee = employeeDao.findByUserName(userName);
		if (employee == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new PrincipalUser(employee.getUserName(), employee.getPassword(), true, true, true, true,
				mapRolesToAuthorities(employee.getRoles()), employee.getId());

	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public List<Employee> findAll() {
		List<Employee> employees = employeeDao.findAll();

		return employees;
	}

	@Override
	@Transactional
	public Employee findById(int employeeId) {
		Optional<Employee> result = employeeDao.findById(employeeId);

		return result.get();

	}

	@Override
	@Transactional
	public void save(Employee employee) {
		List<Role> roles = new ArrayList<Role>();
		for (Role r : employee.getRoles()) {
			roles.add(roleDao.findById(r.getId()).get());
		}
		employee.setRoles(roles);

		employeeDao.save(employee);

	}

	@Override
	@Transactional
	public boolean update(Employee newEmployee) {
		Optional<Employee> result = employeeDao.findById(newEmployee.getId());

		if (result.isPresent()) {
			Employee oldEmployee = result.get();

			// update only if you have the last version
			int oldVersion = oldEmployee.getVersion();
			if (oldVersion == newEmployee.getVersion()) {
				oldEmployee.setVersion(oldVersion + 1);
				oldEmployee.setUserName(newEmployee.getUserName());
				oldEmployee.setPassword(newEmployee.getPassword());
				oldEmployee.setFirstName(newEmployee.getFirstName());
				oldEmployee.setLastName(newEmployee.getLastName());
				oldEmployee.setEmail(newEmployee.getEmail());
				oldEmployee.setTopEmployee(newEmployee.isTopEmployee());

				oldEmployee.getRoles().clear();
				oldEmployee.getRoles().addAll(newEmployee.getRoles());

				oldEmployee.getTasks().clear();
				oldEmployee.getTasks().addAll(newEmployee.getTasks());
				
				

				employeeDao.save(oldEmployee);
				return true;
			}

			else {

				throw new RuntimeException("You are trying to update an older version of this employee, db:"
						+ oldVersion + ", your object: " + newEmployee.getVersion());

			}
		} else {
			throw new NullPointerException("Error, employee not found in the db");
		}
	}

	@Override
	@Transactional
	public void delete(int employeeId) {

		employeeDao.deleteById(employeeId);

	}

}
