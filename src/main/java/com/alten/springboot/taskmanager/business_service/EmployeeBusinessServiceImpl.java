package com.alten.springboot.taskmanager.business_service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.springboot.taskmanager.data_service.EmployeeDataService;
import com.alten.springboot.taskmanager.dto.EmployeeDto;
import com.alten.springboot.taskmanager.entity.Employee;
import com.alten.springboot.taskmanager.entity.Task;

@Service
public class EmployeeBusinessServiceImpl implements EmployeeBusinessService {

	@Autowired
	private EmployeeDataService employeeDataService;
	
	@Autowired
	private ModelMapper modelMapper;

	
	@Override
	public EmployeeDto findByUserName(String userName) {
		Employee employee = employeeDataService.findByUserName(userName);
		EmployeeDto employeeDto = null;
		if (employee != null) {
			employeeDto = modelMapper.map(employee, EmployeeDto.class);
		}
		return employeeDto;
		

	}

	
	@Override
	public List<EmployeeDto> findAll() {
		List<EmployeeDto> employeesDto = new ArrayList<EmployeeDto>();
		List<Employee> employees = employeeDataService.findAll();
		for (Employee employee : employees) {
			employeesDto.add(modelMapper.map(employee, EmployeeDto.class));
		}
		
		return employeesDto;
	}

	@Override
	public EmployeeDto findById(int employeeId) {
		Employee employee = employeeDataService.findById(employeeId);
		EmployeeDto employeeDto = modelMapper.map(employee, EmployeeDto.class);
		return employeeDto;

	}

	@Override
	public void save(EmployeeDto employeeDto) {
		Employee employee = modelMapper.map(employeeDto, Employee.class);
		
		employeeDataService.save(employee);

	}

	@Override
	public boolean update(EmployeeDto employeeDto) {
		Employee employee = modelMapper.map(employeeDto, Employee.class);

		return employeeDataService.update(employee);
	}
	
	@Override
	public void delete(int employeeId) {
		Employee employee = employeeDataService.findById(employeeId);
		for (Task t : employee.getTasks()) {
			t.setEmployee(null);
		}
		//salvare i task??
		employeeDataService.delete(employeeId);
	}

	

}
