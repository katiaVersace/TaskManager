package com.alten.springboot.taskmanager.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
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
import com.alten.springboot.taskmanager.dto.EmployeeDto;
import com.alten.springboot.taskmanager.entity.Employee;
import com.alten.springboot.taskmanager.entity.Role;
import com.alten.springboot.taskmanager.entity.Task;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeDao;

	@Autowired
	private RoleRepository roleDao;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	@Transactional
	public EmployeeDto findByUserName(String userName) {

		Employee employee = employeeDao.findByUserName(userName);
		EmployeeDto EmployeeDtoLight = null;
		if (employee != null) {
			EmployeeDtoLight = modelMapper.map(employee, EmployeeDto.class);
		}
		return EmployeeDtoLight;

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
	public List<EmployeeDto> findAll() {
		List<Employee> employees = employeeDao.findAll();
		List<EmployeeDto> employeesDto = new ArrayList<EmployeeDto>();
		for (Employee employee : employees) {
			employeesDto.add(modelMapper.map(employee, EmployeeDto.class));
		}
		return employeesDto;
	}

	@Override
	@Transactional
	public EmployeeDto findById(int employeeId) {
		Optional<Employee> result = employeeDao.findById(employeeId);

		EmployeeDto EmployeeDtoLight = null;
		if (result.isPresent()) {

			EmployeeDtoLight = modelMapper.map(result.get(), EmployeeDto.class);
		}

		return EmployeeDtoLight;

	}

	@Override
	@Transactional
	public void save(EmployeeDto employeeDtoLight) {
		Employee employee = modelMapper.map(employeeDtoLight, Employee.class);
		List<Role> roles = new ArrayList<Role>();
		for (Role r : employee.getRoles()) {
			roles.add(roleDao.findById(r.getId()).get());
		}
		employee.setRoles(roles);
		employeeDao.save(employee);

	}

	@Override
	@Transactional
	public boolean update(EmployeeDto employeeDtoLight) {
		Optional<Employee> result = employeeDao.findById(employeeDtoLight.getId());

		if (result.isPresent()) {
			Employee oldEmployee = result.get();
			Employee newEmployee = modelMapper.map(employeeDtoLight, Employee.class);

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

//				oldEmployee.getTasks().clear();
//				oldEmployee.getTasks().addAll(newEmployee.getTasks());

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
		Employee theEmployee = employeeDao.findById(employeeId).get();

		if (theEmployee != null) {

			for (Task t : theEmployee.getTasks()) {
				t.setEmployee(null);
			}
			employeeDao.deleteById(employeeId);
		}
	}

	

}
