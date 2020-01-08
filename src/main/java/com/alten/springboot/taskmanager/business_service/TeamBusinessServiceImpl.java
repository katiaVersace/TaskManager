package com.alten.springboot.taskmanager.business_service;



import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.springboot.taskmanager.data_service.EmployeeDataService;
import com.alten.springboot.taskmanager.data_service.TaskDataService;
import com.alten.springboot.taskmanager.data_service.TeamDataService;
import com.alten.springboot.taskmanager.dto.EmployeeDto;
import com.alten.springboot.taskmanager.dto.TaskDto;
import com.alten.springboot.taskmanager.dto.TeamDto;
import com.alten.springboot.taskmanager.entity.Employee;
import com.alten.springboot.taskmanager.entity.Task;
import com.alten.springboot.taskmanager.entity.Team;

@Service
public class TeamBusinessServiceImpl implements TeamBusinessService {

	@Autowired
	private TeamDataService teamDataService;
	
	@Autowired
	private EmployeeDataService employeeDataService;
	
	@Autowired
	private TaskDataService taskDataService;

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
		if( teamDataService.update(newTeam) != null)
			return true;
		else return false;
	}

	@Override
	public void delete(int teamId) {
		 teamDataService.delete(teamId);

	}

	@Override
	public String populateDB() {
		
		return "ok";

	}

	@Override
	public String randomPopulation(String start_date, String end_date, int teams_size, int employees_size, int tasks_size,
			int task_max_duration) {
		
		//input
				LocalDate start = LocalDate.parse(start_date);
				LocalDate end =  LocalDate.parse(end_date);

				int nRandomChars = 2;
				
				// Check input
				long days_max = ChronoUnit.DAYS.between(start, end)+1;
				if (teams_size > (employees_size / 2) || tasks_size < employees_size || task_max_duration >days_max ) {
					return "invalid input";
				}

				// clear database

				// create Employees
				List<Employee> employees = new ArrayList<Employee>();
				for (int i = 0; i < employees_size; i++) {

					String generatedString = RandomStringUtils.randomAlphabetic(nRandomChars).toUpperCase();
					Employee employee = new Employee("EMP_" + generatedString, generatedString, "Name_" + generatedString,
							"LastName_" + generatedString, generatedString + "@alten.it", false);
					//employee.setId(i + 1);
					employee = employeeDataService.save(employee);
					employees.add(employee);
					// System.out.println("Employees:\n" + employee);

				}

				// create Teams
				List<Team> teams = new ArrayList<Team>();
				for (int i = 0; i < teams_size; i++) {

					String generatedString = RandomStringUtils.randomAlphabetic(nRandomChars).toUpperCase();
					Team team = new Team("TEAM_" + generatedString);
					//team.setId(i + 1);
					team = teamDataService.save(team);
					teams.add(team);
					
					// System.out.println("Team:\n" + team);

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
//					data.setTime(end);
//					data.add(Calendar.DAY_OF_MONTH, -1 * (duration ));
					
					LocalDate endMinusDuration = end.plusDays(-duration);
					
					//System.out.println("duration: "+duration+" end Max: "+data.getTime());

					LocalDate expectedStartTime = between(start, endMinusDuration);

					LocalDate expected_end_time = expectedStartTime.plusDays(duration - 1);
					Task task = new Task("Task: " + generatedString, expectedStartTime, null, expected_end_time, null);
					//task.setId(i + 1);
					tasks.add(task);
					// System.out.println("Task \n" + task);

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
						employee = employeeDataService.update(employee);
					}
					
				//	System.out.println("Assigned "+ tasks.get(task_index).getDescription()+", to employee "+employee.getUserName()+", with start "+tasks.get(task_index).getExpectedStartTime());
					task_index++;
				}
				
				// assign remaining task
				List<Task> remainingTasks = new ArrayList<Task>(tasks.subList(task_index, tasks.size()));
				
				boolean availability = true;
				while (remainingTasks.size() != 0 && availability) {
				//	System.out.println("Trying to assign " + remainingTasks.get(0).getDescription());
					if (assignTask(remainingTasks.get(0), employees, start, end)) {
						remainingTasks.remove(0);
					} else {
						availability = false;
						System.out.println("Non sono riuscito ad assegnare i rimanenti "+remainingTasks.size()+" tasks, nessun impiegato ha più disponibilità nel periodo indicato");
					}

				}

				
				

				String result ="";
				for (Employee employee : employees) {
					result = result.concat(printEmployeeScheduling(employee, days_max, start, end)+"\n");
							
				}
				
				System.out.println("RESULT: \n"+result);
				return result;
	}

	
	
	
	private String printEmployeeScheduling(Employee employee, long schedule_size, LocalDate start, LocalDate end) {
		
		String result = "";
		
		Boolean[] schedule = new Boolean[(int) schedule_size];
		Arrays.fill(schedule, Boolean.FALSE);
		
		for (Task task : employee.getTasks()) {
			//System.out.println("Task start "+t.getExpectedStartTime()+" , End: "+t.getExpectedEndTime());
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
			

			for (int i = (int) diff1; i < diff1+task_duration; i++) {
				schedule[i] = true;
			}

			
		}
		
		
		result = result.concat(employee.getUserName() + " ");
		for(Boolean value: schedule) {
			if(value.booleanValue()==true) {
				result = 	result.concat("X ");
			}
			else {
				result = 	result.concat("_ ");
			}
		}
		
		return result;
		
	}

	public void printOutput(LocalDate start, LocalDate end, List<Task> tasks) {
		for (Task t : tasks) {
			if(t.getEmployee()!=null) {
			System.out.print(t.getEmployee().getUserName() + ", " + t.getDescription() + " ");

			long diff1 = ChronoUnit.DAYS.between(start, t.getExpectedStartTime());
			long task_duration = ChronoUnit.DAYS.between(t.getExpectedStartTime(), t.getExpectedEndTime())
					+ 1;
					long diff2 = ChronoUnit.DAYS.between(t.getExpectedEndTime(), end);


			for (int i = 0; i < diff1; i++) {
				System.out.print("_ ");
			}

			for (int i = 0; i < task_duration; i++) {
				System.out.print("X ");
			}

			for (int i = 0; i < diff2; i++) {
				System.out.print("_ ");
			}

			
			System.out.println("");
			}
		}

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
					employeeDataService.update(employee);
				}
				return true;
			}
		}

		// Non sono riuscito ad assegnarlo quindi devo modificare la data del task
		for (Employee employee : employees) {
			LocalDate availability = getAvailability(employee, start, end);
			if (availability != null) {
				System.out.println("Non sono riuscito ad assegnare il "+task.getDescription()+" quindi ho modificato la sua data e l'ho assegnato a : "+employee.getUserName()
						);
				task.setExpectedStartTime(availability);
				task.setExpectedEndTime(availability);

				task.setEmployee(employee);
				employee.getTasks().add(task);
				task = taskDataService.save(task);
				if (employee.getTasks().size() >= 5) {

					employee.setTopEmployee(true);
					employee = employeeDataService.update(employee);
				}
				return true;
			}
		}

		// Nessun impiegato ha una disponibilità
		return false;
	}

	public LocalDate getAvailability(Employee employee, LocalDate start, LocalDate end) {
		Calendar data = Calendar.getInstance();
		LocalDate currentDay = start;
		while ( currentDay.compareTo(end)<=0 ) {
		//	System.out.println("currnt day: "+currentDay+ " End day: "+end);

			if (employeeAvailable(employee, currentDay, currentDay)) {
				return currentDay;
			}

			
			currentDay = currentDay.plusDays(1);
		}

		return null;
	}

	public LocalDate between(LocalDate startInclusive, LocalDate endInclusive) {
		Random rn = new Random();
		
		long days_max = (ChronoUnit.DAYS.between(startInclusive,endInclusive))+1;
		int days = rn.nextInt((int)days_max) ;
		
		LocalDate randomDate = startInclusive.plusDays(days);
		
		return randomDate;
	}
	
	public boolean betweenTwoDate(LocalDate toCheck, LocalDate start, LocalDate end) {
		boolean result = (toCheck.isAfter(start) && toCheck.isBefore(end)) || toCheck.equals(start) || toCheck.equals(end);
		return result;
	}

	public boolean employeeAvailable(Employee e, LocalDate startTask, LocalDate endTask) {
		for (Task t : e.getTasks()) {
			if (betweenTwoDate(startTask, t.getExpectedStartTime(), t.getExpectedEndTime())  ||  betweenTwoDate(endTask, t.getExpectedStartTime(), t.getExpectedEndTime())
				||	betweenTwoDate(t.getExpectedStartTime(), startTask, endTask)  ||  betweenTwoDate(t.getExpectedEndTime(), startTask, endTask)
					) {
			//	System.out.println(e.getUserName() + " Not available for task with start in " + startTask);

				return false;

			}

		}

		//System.out.println(e.getUserName() + "  available for task with start in " + startTask);
		return true;
	}

	
	@Override
	public TaskDto tryAssignTaskToTeam(String start_date, String end_date, int team_id, TaskDto theTaskDto) {
		
		Task theTask = modelMapper.map(theTaskDto, Task.class);
		Team theTeam = teamDataService.findById(team_id);
		
		LocalDate start = LocalDate.parse(start_date);
		LocalDate end =  LocalDate.parse(end_date);
		
		if(!checkDate(theTask)) return null;
			
		long days_max = ChronoUnit.DAYS.between(start, end)+1;
		
		List<Task> visti = new ArrayList<>();
		Map<Task, Employee> solution = new HashMap<>();
		if(assignTaskToTeam(theTask, theTeam.getEmployees(), visti, solution)) {
			for (Map.Entry<Task, Employee> entry : solution.entrySet()) {
				System.out.println("Task : " + entry.getKey().getDescription() + " Employee : " + entry.getValue().getUserName());
				Employee oldEmployee = entry.getKey().getEmployee();
				if (oldEmployee != null) {
					oldEmployee.getTasks().remove(entry.getKey());
					oldEmployee = employeeDataService.update(oldEmployee);
				}
				entry.getValue().getTasks().add(entry.getKey());
				entry.getKey().setEmployee(entry.getValue());
				theTaskDto = modelMapper.map( taskDataService.save(entry.getKey()), TaskDto.class);
			}
			
			for(Employee e: theTeam.getEmployees()) {
			System.out.println(printEmployeeScheduling(e, days_max, start, end));
			}
			
			
		return theTaskDto;
		}
		else {
			System.out.println("Assegnamento fallito");
		return null;
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
			if (!((theTask.getRealStartTime().isAfter(today)
					|| theTask.getRealStartTime().equals(today))
					&& (theTask.getRealStartTime().isBefore(theTask.getRealEndTime())
							|| theTask.getRealStartTime().equals(theTask.getRealEndTime()))))
				return false;
		}
		return  true;
	}

	private boolean assignTaskToTeam(Task task, Set<Employee> team, List<Task> visti, Map<Task, Employee> solution ) {

		// caso base negativo
		if (visti.contains(task)) {
			return false;
		}

		// caso base positivo
		for (Employee employee : team) {
			if (employee != task.getEmployee()) {
				if (employeeAvailable(employee, task.getExpectedStartTime(), task.getExpectedEndTime())) {
					

					solution.put(task, employee);
					
					return true;
				}
			}
		}

		// passo ricorsivo
		for (Employee employee : team) {
			if (employee != task.getEmployee()) {

				if (!visti.contains(task))
					visti.add(task);

				List<Task> tasks_in_period = getTasksInPeriod(employee, task.getExpectedStartTime(),
						task.getExpectedEndTime());
				
				int result = 1;
				Map <Task, Employee> partialSolution = new HashMap<Task, Employee>();
				for(Task task_to_rearrange: tasks_in_period) {
					if (taskInProgress(task_to_rearrange) || !assignTaskToTeam(task_to_rearrange, team, visti, partialSolution)) {
						
						result= -result;
					break;	
					}
					
					
				}
				
				if(result > 0) 
				{
					//copy
					for (Map.Entry<Task, Employee> entry : partialSolution.entrySet()) {
						solution.put(entry.getKey(), entry.getValue());
					}
					
					solution.put(task, employee);
					return true;
				}
					


			}
		}
		return false;
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

	
	
	public void printTaskSchedule(Task task, LocalDate start, LocalDate end) {
		
			
			

			long diff1 = ChronoUnit.DAYS.between(start, task.getExpectedStartTime());
			long task_duration = ChronoUnit.DAYS.between(task.getExpectedStartTime(), task.getExpectedEndTime())
					+ 1;
					long diff2 = ChronoUnit.DAYS.between(task.getExpectedEndTime(), end);

			for (int i = 0; i < diff1; i++) {
				System.out.print("_ ");
			}

			for (int i = 0; i < task_duration; i++) {
				System.out.print("X ");
			}

			for (int i = 0; i < diff2; i++) {
				System.out.print("_ ");
			}

			//System.out.println(task.getDescription() + " ");
			System.out.println();
			
		

	}

	@Override
	public void deleteAll() {
		teamDataService.deleteAll();
		
	}



}
