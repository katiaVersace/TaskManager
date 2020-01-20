package com.alten.springboot.taskmanager.businessservice;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.springboot.taskmanager.dataservice.IEmployeeDataService;
import com.alten.springboot.taskmanager.dataservice.ITaskDataService;
import com.alten.springboot.taskmanager.dataservice.ITeamDataService;
import com.alten.springboot.taskmanager.dto.TaskDto;
import com.alten.springboot.taskmanager.dto.TeamDto;
import com.alten.springboot.taskmanager.model.Employee;
import com.alten.springboot.taskmanager.model.Task;
import com.alten.springboot.taskmanager.model.Team;

@Service
public class TeamBusinessService implements ITeamBusinessService {

	@Autowired
	private ITeamDataService teamDataService;

	@Autowired
	private IEmployeeDataService employeeDataService;

	@Autowired
	private ITaskDataService taskDataService;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public List<TeamDto> findAll() {
		List<Team> teams = teamDataService.findAll();

		List<TeamDto> teamsDto = new ArrayList<TeamDto>();
		for (Team team : teams) {
			teamsDto.add(modelMapper.map(team, TeamDto.class));
		}
		return teamsDto;
	}

	@Override
	public TeamDto findById(int teamId) {
		Team team = teamDataService.findById(teamId);

		TeamDto teamDto = null;
		if (team != null) {

			teamDto = modelMapper.map(team, TeamDto.class);
		}

		return teamDto;
	}

	@Override
	public void save(TeamDto teamDto) {
		Team team = modelMapper.map(teamDto, Team.class);
		teamDataService.save(team);

	}

	@Override
	public boolean update(TeamDto teamDto) {
		Team newTeam = modelMapper.map(teamDto, Team.class);
		if (teamDataService.update(newTeam) != null)
			return true;
		else
			return false;
	}

	@Override
	public void delete(int teamId) {
		teamDataService.delete(teamId);

	}

	@Override
	public String randomPopulation(String start_date, String end_date, int teams_size, int employees_size,
			int tasks_size, int task_max_duration) {

		// input
		LocalDate start = LocalDate.parse(start_date);
		LocalDate end = LocalDate.parse(end_date);

		int nRandomChars = 2;

		// Check input
		long days_max = ChronoUnit.DAYS.between(start, end) + 1;
		if (teams_size > (employees_size / 2) || tasks_size < employees_size || task_max_duration > days_max) {
			return "invalid input";
		}

		// create Employees
		List<Employee> employees = new ArrayList<Employee>();
		for (int i = 0; i < employees_size; i++) {

			String generatedString = RandomStringUtils.randomAlphabetic(nRandomChars).toUpperCase();
			Employee employee = new Employee("EMP_" + generatedString, generatedString, "Name_" + generatedString,
					"LastName_" + generatedString, generatedString + "@alten.it", false);
			employee = employeeDataService.save(employee);
			employees.add(employee);

		}

		// create Teams
		List<Team> teams = new ArrayList<Team>();
		for (int i = 0; i < teams_size; i++) {

			String generatedString = RandomStringUtils.randomAlphabetic(nRandomChars).toUpperCase();
			Team team = new Team("TEAM_" + generatedString);
			team = teamDataService.save(team);
			teams.add(team);

		}

		// assign team to employees
		int team_index = 0;
		for (Employee employee : employees) {
			Team currentTeam = teams.get(team_index);
			currentTeam.getEmployees().add(employee);
			currentTeam = teamDataService.update(currentTeam);
			teams.set(team_index, currentTeam);

			team_index++;
			if (team_index == teams_size)
				team_index = 0;
		}

		// create Tasks
		Random rn = new Random();

		List<Task> tasks = new ArrayList<Task>();
		for (int i = 0; i < tasks_size; i++) {
			int duration = rn.nextInt(task_max_duration) + 1;

			String generatedString = RandomStringUtils.randomAlphabetic(nRandomChars).toUpperCase();

			LocalDate endMinusDuration = end.plusDays(-duration);

			LocalDate expectedStartTime = between(start, endMinusDuration);

			LocalDate expected_end_time = expectedStartTime.plusDays(duration - 1);
			Task task = new Task(generatedString, expectedStartTime, null, expected_end_time, null);
			tasks.add(task);

		}

		// assign at least 1 task for each employee
		int task_index = 0;
		for (Employee employee : employees) {
			Task currentTask = tasks.get(task_index);
			currentTask.setEmployee(employee);
			employee.getTasks().add(currentTask);
			currentTask = taskDataService.save(currentTask);
			tasks.set(task_index, currentTask);
			if (employee.getTasks().size() >= 5) {

				employee.setTopEmployee(true);

			}
			employees.set(employees.indexOf(employee), employeeDataService.update(employee));

			task_index++;
		}

		// assign remaining task
		List<Task> remainingTasks = new ArrayList<Task>(tasks.subList(task_index, tasks.size()));

		boolean availability = true;
		while (remainingTasks.size() != 0 && availability) {

			if (assignTask(remainingTasks.get(0), employees, start, end)) {
				remainingTasks.remove(0);
			} else {
				availability = false;

			}

		}

		String result = "";
		for (Employee employee : employees) {
			result = result.concat(printEmployeeScheduling(employee, days_max, start, end) + "\n");

		}

		System.out.println("\nRESULT:");
		for (int i = 0; i < days_max; i++) {
			LocalDate currentDay = start.plusDays(i);
			String day = String.valueOf(currentDay.getDayOfMonth());
			if (currentDay.getDayOfMonth() < 10) {
				day = "0" + day;
			}
			System.out.print(day + "  ");
		}
		System.out.println("\n" + result);
		return result;
	}

	private String printEmployeeScheduling(Employee employee, long schedule_size, LocalDate start, LocalDate end) {

		String[] schedule = new String[(int) schedule_size];
		Arrays.fill(schedule, "__");

		for (Task task : employee.getTasks()) {
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

			String stringToPrint = task.getDescription();
			if (stringToPrint.length() < 2)
				stringToPrint = "0" + stringToPrint;

			for (int i = (int) diff1; i < diff1 + task_duration; i++) {
				schedule[i] = stringToPrint;
			}
		}

		String result = "";
		for (String c : schedule)
			result = result.concat(c + "  ");
		result = result.concat(employee.getUserName());

		return result;

	}

	public boolean assignTask(Task task, List<Employee> employees, LocalDate start, LocalDate end) {
		// per non assegnare tutti i task al primo impiegato
		Collections.shuffle(employees);

		for (Employee employee : employees) {
			if (employeeAvailable(employee, task.getExpectedStartTime(), task.getExpectedEndTime())) {
				task.setEmployee(employee);
				employee.getTasks().add(task);
				task = taskDataService.save(task);
				if (employee.getTasks().size() >= 5) {

					employee.setTopEmployee(true);

				}
				employees.set(employees.indexOf(employee), employeeDataService.update(employee));
				return true;
			}
		}

		// Non sono riuscito ad assegnarlo quindi devo modificare la data del task
		for (Employee employee : employees) {
			LocalDate availability = getAvailability(employee, start, end);
			if (availability != null) {
				System.out.println("Non sono riuscito ad assegnare il " + task.getDescription()
						+ " quindi ho modificato la sua data e l'ho assegnato a : " + employee.getUserName());
				task.setExpectedStartTime(availability);
				task.setExpectedEndTime(availability);

				task.setEmployee(employee);
				employee.getTasks().add(task);
				task = taskDataService.save(task);
				if (employee.getTasks().size() >= 5) {

					employee.setTopEmployee(true);

				}
				employees.set(employees.indexOf(employee), employeeDataService.update(employee));
				return true;
			}
		}

		// Nessun impiegato ha una disponibilità
		return false;
	}

	public LocalDate getAvailability(Employee employee, LocalDate start, LocalDate end) {
		LocalDate currentDay = start;
		while (currentDay.compareTo(end) <= 0) {

			if (employeeAvailable(employee, currentDay, currentDay)) {
				return currentDay;
			}

			currentDay = currentDay.plusDays(1);
		}

		return null;
	}

	public LocalDate between(LocalDate startInclusive, LocalDate endInclusive) {
		Random rn = new Random();

		long days_max = (ChronoUnit.DAYS.between(startInclusive, endInclusive)) + 1;
		int days = rn.nextInt((int) days_max);

		LocalDate randomDate = startInclusive.plusDays(days);

		return randomDate;
	}

	public boolean betweenTwoDate(LocalDate toCheck, LocalDate start, LocalDate end) {
		boolean result = (toCheck.isAfter(start) && toCheck.isBefore(end)) || toCheck.equals(start)
				|| toCheck.equals(end);
		return result;
	}

	public boolean employeeAvailable(Employee e, LocalDate startTask, LocalDate endTask) {
		for (Task t : e.getTasks()) {
			if (betweenTwoDate(startTask, t.getExpectedStartTime(), t.getExpectedEndTime())
					|| betweenTwoDate(endTask, t.getExpectedStartTime(), t.getExpectedEndTime())
					|| betweenTwoDate(t.getExpectedStartTime(), startTask, endTask)
					|| betweenTwoDate(t.getExpectedEndTime(), startTask, endTask)) {

				return false;

			}

		}

		return true;
	}

	@Override
	public TaskDto tryAssignTaskToTeam(String start_date, String end_date, int team_id, TaskDto theTaskDto) {

		Task theTask = modelMapper.map(theTaskDto, Task.class);
		Team theTeam = teamDataService.findById(team_id);

		LocalDate start = LocalDate.parse(start_date);
		LocalDate end = LocalDate.parse(end_date);

		if (!checkDate(theTask))
			return null;

		long days_max = ChronoUnit.DAYS.between(start, end) + 1;

		// order
		orderByTasksNumberInPeriod(theTeam.getEmployees(), theTask.getExpectedStartTime(),
				theTask.getExpectedEndTime());

		List<Task> visitedTasks = new ArrayList<Task>();
		HashMap<Task, Employee> oldAssignments = new HashMap<Task, Employee>();
		if (checkNoSolution(theTask.getExpectedStartTime(), theTask.getExpectedEndTime(), theTeam.getEmployees())
				|| !assignTaskToTeam(theTask, theTeam.getEmployees(), visitedTasks, oldAssignments)) {
			System.out.println("Impossible to assign the task");
			return null;
		} else {
			List<Employee> employeesToSave = new ArrayList<Employee>();
			for (Task taskInSolution : visitedTasks) {
				System.out
						.println(taskInSolution.getDescription() + " -> " + taskInSolution.getEmployee().getUserName());

				Task savedTask = taskDataService.save(taskInSolution);
				Employee oldEmployee = oldAssignments.get(taskInSolution);

				if (oldEmployee != null) {
					employeesToSave.add(oldEmployee);
					employeesToSave.add(taskInSolution.getEmployee());

				} else {
					theTaskDto = modelMapper.map(savedTask, TaskDto.class);
					employeesToSave.add(taskInSolution.getEmployee());
				}

			}
			for (Employee e : employeesToSave) {

				employeeDataService.save(e);

			}

			String result = "";
			start = start.minusDays(15);
			end = end.plusDays(15);
			days_max = (ChronoUnit.DAYS.between(start, end)) + 1;
			for (Employee employee : theTeam.getEmployees()) {
				result = result.concat(printEmployeeScheduling(employee, days_max, start, end) + "\n");

			}

			System.out.println("\nRESULT:");
			for (int i = 0; i < days_max; i++) {
				LocalDate currentDay = start.plusDays(i);
				String day = String.valueOf(currentDay.getDayOfMonth());
				if (currentDay.getDayOfMonth() < 10) {
					day = "0" + day;
				}
				System.out.print(day + "  ");
			}
			System.out.println("\n" + result);

			return theTaskDto;
		}

	}

	public boolean checkDate(Task theTask) {
		// check sulle date
		LocalDate today = LocalDate.now();
		if (!((theTask.getExpectedStartTime().isAfter(today) || theTask.getExpectedStartTime().equals(today))
				&& (theTask.getExpectedStartTime().isBefore(theTask.getExpectedEndTime())
						|| theTask.getExpectedStartTime().equals(theTask.getExpectedEndTime()))))
			return false;

		if (theTask.getRealStartTime() != null || theTask.getRealEndTime() != null) {
			if (!((theTask.getRealStartTime().isAfter(today) || theTask.getRealStartTime().equals(today))
					&& (theTask.getRealStartTime().isBefore(theTask.getRealEndTime())
							|| theTask.getRealStartTime().equals(theTask.getRealEndTime()))))
				return false;
		}
		return true;
	}

	public boolean assignTaskToTeam(Task task, Set<Employee> team, List<Task> visitedTasks,
			HashMap<Task, Employee> oldAssignments) {

		visitedTasks.add(task);
		Employee oldEmployee = task.getEmployee();

		// caso base positivo
		for (Employee employee : team) {
			if (employee != task.getEmployee()) {
				if (employeeAvailable(employee, task.getExpectedStartTime(), task.getExpectedEndTime())) {

					assignTaskToEmployee(task, employee);
					oldAssignments.put(task, oldEmployee);
					return true;
				}
			}
		}

		// passo ricorsivo
		for (Employee employee : team) {
			if (employee != task.getEmployee()) {

				List<Task> tasksInPeriod = getTasksInPeriod(employee, task.getExpectedStartTime(),
						task.getExpectedEndTime());

				// forzatamente assegno il task all'impiegato e cerco di spostare i task di
				// intralcio
				assignTaskToEmployee(task, employee);

				boolean atLeastOneFailed = false;
				for (Task taskToRearrange : tasksInPeriod) {
					if (visitedTasks.contains(taskToRearrange)) {
						atLeastOneFailed = true;
						break;
					}
					HashMap<Task, Employee> oldAssignmentsForBranch = new HashMap<Task, Employee>();
					if (taskInProgress(taskToRearrange)
							|| !assignTaskToTeam(taskToRearrange, team, visitedTasks, oldAssignmentsForBranch)) {
						atLeastOneFailed = true;
						break;
					} else {
						// tutti i branch hanno restituito true quindi faccio merge dei vecchi
						// assegnamenti dei task in quei branch
						for (HashMap.Entry<Task, Employee> entry : oldAssignmentsForBranch.entrySet()) {
							oldAssignments.put(entry.getKey(), entry.getValue());
						}
					}

				}
				if (atLeastOneFailed) {

					if (oldEmployee != null) {
						oldEmployee.getTasks().add(task);
					}
					employee.getTasks().remove(task);
					task.setEmployee(oldEmployee);

					// ripristino i task dei branch che hanno restituito true
					for (HashMap.Entry<Task, Employee> entry : oldAssignments.entrySet()) {
						Task taskToRevert = entry.getKey();
						assignTaskToEmployee(taskToRevert, entry.getValue());
						visitedTasks.remove(taskToRevert);
					}
					oldAssignments.clear();

				} else {

					oldAssignments.put(task, oldEmployee);
					return true;

				}

			}

		}

		visitedTasks.remove(task);
		return false;
	}

	public void assignTaskToEmployee(Task task, Employee employee) {
		if (task.getEmployee() != null) {
			task.getEmployee().getTasks().remove(task);
			if (task.getEmployee().getTasks().size() < 5) {

				task.getEmployee().setTopEmployee(false);

			}
		}
		employee.getTasks().add(task);
		if (employee.getTasks().size() >= 5) {

			employee.setTopEmployee(true);

		}
		task.setEmployee(employee);
	}

	private boolean taskInProgress(Task task_to_rearrange) {
		LocalDate today = LocalDate.now();
		if ((task_to_rearrange.getExpectedStartTime().isBefore(today)
				&& task_to_rearrange.getExpectedEndTime().isAfter(today))
				|| task_to_rearrange.getExpectedStartTime().equals(today)
				|| task_to_rearrange.getExpectedEndTime().equals(today))
			return true;

		else
			return false;
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

	@Override
	public void deleteAll() {
		teamDataService.deleteAll();

	}

	public boolean checkNoSolution(LocalDate start, LocalDate end, Set<Employee> employees) {

		LocalDate currentDay = start;
		while (currentDay.compareTo(end) <= 0) {
			boolean atLeastOne = false;
			for (Employee employee : employees) {
				if (employeeAvailable(employee, currentDay, currentDay)) {
					atLeastOne = true;
					break;
				}
			}
			if (!atLeastOne) {
				return true;
			}
			currentDay = currentDay.plusDays(1);
		}
		return false;

	}

	public void orderByTasksNumberInPeriod(Set<Employee> employees, LocalDate start, LocalDate end) {

		List<Employee> orderedEmployees = new ArrayList<Employee>(employees);

		Collections.sort(orderedEmployees, new Comparator<Employee>() {
			public int compare(Employee e1, Employee e2) {
				if (getTasksInPeriod(e1, start, end).size() == getTasksInPeriod(e2, start, end).size())
					return 0;
				return getTasksInPeriod(e1, start, end).size() < getTasksInPeriod(e2, start, end).size() ? -1 : 1;
			}
		});

		employees = new HashSet<Employee>(orderedEmployees);
	}

	public String toString(int i, int radix) { // SORGENTE RICAVATO dal toString di java > Integer.toString(n, 16);
		String SYMBOLS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_-";
		char[] digits = SYMBOLS.toCharArray();
		char[] buf = new char[33];
		boolean negative = (i < 0);
		int charPos = 32;

		if (!negative) {
			i = -i;
		}

		while (i <= -radix) {
			buf[charPos--] = digits[-(i % radix)];
			i = i / radix;
		}
		buf[charPos] = digits[-i];

		if (negative) {
			buf[--charPos] = '-';
		}

		return new String(buf, charPos, (33 - charPos));
	}

}
