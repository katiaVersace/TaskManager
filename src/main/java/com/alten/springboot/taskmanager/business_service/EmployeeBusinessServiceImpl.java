package com.alten.springboot.taskmanager.business_service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.springboot.taskmanager.data_service.EmployeeDataService;
import com.alten.springboot.taskmanager.data_service.TeamDataService;
import com.alten.springboot.taskmanager.dto.EmployeeDto;
import com.alten.springboot.taskmanager.dto.TaskDto;
import com.alten.springboot.taskmanager.entity.Employee;
import com.alten.springboot.taskmanager.entity.Task;
import com.alten.springboot.taskmanager.entity.Team;

@Service
public class EmployeeBusinessServiceImpl implements EmployeeBusinessService {

	@Autowired
	private EmployeeDataService employeeDataService;

	@Autowired
	private TeamDataService teamDataService;

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
		employee.setPassword("{noop}"+employee.getPassword());

		employeeDataService.save(employee);

	}

	@Override
	public boolean update(EmployeeDto employeeDto) {
		Employee employee = modelMapper.map(employeeDto, Employee.class);

		if (employeeDataService.update(employee) != null)
			return true;
		else
			return false;
	}

	@Override
	public void delete(int employeeId) {
		Employee employee = employeeDataService.findById(employeeId);
		for (Task t : employee.getTasks()) {
			t.setEmployee(null);
		}
		employeeDataService.delete(employeeId);
	}

	@Override
	public List<EmployeeDto> getAvailableEmployeesByTeamAndTask(int teamId, TaskDto theTask) {
		Team theTeam = teamDataService.findById(teamId);
		List<EmployeeDto> result = new ArrayList<EmployeeDto>();

		for (Employee employee : theTeam.getEmployees()) {
			if (employeeAvailable(employee.getTasks(), LocalDate.parse(theTask.getExpectedStartTime()),
					LocalDate.parse(theTask.getExpectedEndTime()))) {
				result.add(modelMapper.map(employee, EmployeeDto.class));
			}
		}

		return result;

	}

	@Override
	public String getAvailabilityByEmployee(int employeeId, String start_date, String end_date) {
		Employee employee = employeeDataService.findById(employeeId);
		LocalDate start = LocalDate.parse(start_date);
		LocalDate end = LocalDate.parse(end_date);
		long schedule_size = ChronoUnit.DAYS.between(start, end) + 1;

		Boolean[] schedule = new Boolean[(int) schedule_size];
		Arrays.fill(schedule, Boolean.FALSE);

		for (Task task : getTasksInPeriod(employee, start, end)) {

			LocalDate taskStartDate = task.getExpectedStartTime();
			LocalDate taskEndDate = task.getExpectedEndTime();

			// check on task start and end because a task can end over the end of the period
			// specified or start before
			if (taskStartDate.isBefore(start)) {
				taskStartDate = start;
			}
			if (taskEndDate.isAfter(end)) {
				taskEndDate = end;
			}

			long diff1 = ChronoUnit.DAYS.between(start, taskStartDate);
			long task_duration = ChronoUnit.DAYS.between(taskStartDate, taskEndDate) + 1;

			for (int i = (int) diff1; i < diff1 + task_duration; i++) {

				schedule[i] = true;

			}

		}

		String result = "";
		result = result.concat(employee.getUserName() + " ");
		for (Boolean value : schedule) {
			if (value.booleanValue() == true) {
				result = result.concat("X ");
			} else {
				result = result.concat("_ ");
			}
		}

		return result;
	}

	private List<Task> getTasksInPeriod(Employee employee, LocalDate start, LocalDate end) {
		List<Task> tasks_in_period = new ArrayList<Task>();
		for (Task t : employee.getTasks()) {
			if (betweenTwoDate(start, t.getExpectedStartTime(), t.getExpectedEndTime())
					|| betweenTwoDate(end, t.getExpectedStartTime(), t.getExpectedEndTime())
					|| betweenTwoDate(t.getExpectedStartTime(), start, end)
					|| betweenTwoDate(t.getExpectedEndTime(), start, end)) {
				tasks_in_period.add(t);
			}
		}

		return tasks_in_period;
	}

	public boolean betweenTwoDate(LocalDate toCheck, LocalDate start, LocalDate end) {
		boolean result = (toCheck.isAfter(start) && toCheck.isBefore(end)) || toCheck.equals(start)
				|| toCheck.equals(end);
		return result;
	}

	public boolean employeeAvailable(List<Task> employee_tasks, LocalDate startTask, LocalDate endTask) {
		for (Task t : employee_tasks) {
			if (betweenTwoDate(startTask, t.getExpectedStartTime(), t.getExpectedEndTime())
					|| betweenTwoDate(endTask, t.getExpectedStartTime(), t.getExpectedEndTime())
					|| betweenTwoDate(t.getExpectedStartTime(), startTask, endTask)
					|| betweenTwoDate(t.getExpectedEndTime(), startTask, endTask)) {

				return false;

			}

		}

		return true;
	}

}
