package com.alten.springboot.taskmanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import com.alten.springboot.taskmanager.entity.Employee;
import com.alten.springboot.taskmanager.entity.Task;
import com.alten.springboot.taskmanager.entity.Team;

public class PopulateDb {

	public static void main(String[] args) {
		
		//input
		LocalDate start = LocalDate.parse("2019-12-16");
		LocalDate end =  LocalDate.parse("2019-12-20");

		int teams_size = 1, employees_size = 2, tasks_size = 4, task_max_duration = 3, nRandomChars = 2;
		
		// Check input
		long days_max = ChronoUnit.DAYS.between(start, end)+1;
		if (teams_size > (employees_size / 2) || tasks_size < employees_size || task_max_duration >days_max ) {
			System.out.println("invalid input");
			return;
		}

		// clear database

		// create Employees
		List<Employee> employees = new ArrayList<Employee>();
		for (int i = 0; i < employees_size; i++) {

			String generatedString = RandomStringUtils.randomAlphabetic(nRandomChars).toUpperCase();
			Employee employee = new Employee("EMP_" + generatedString, generatedString, "Name_" + generatedString,
					"LastName_" + generatedString, generatedString + "@alten.it", false);
			employee.setId(i + 1);
			employees.add(employee);

			// System.out.println("Employees:\n" + employee);

		}

		// create Teams
		List<Team> teams = new ArrayList<Team>();
		for (int i = 0; i < teams_size; i++) {

			String generatedString = RandomStringUtils.randomAlphabetic(nRandomChars).toUpperCase();
			Team team = new Team("TEAM_" + generatedString);
			team.setId(i + 1);
			teams.add(team);

			// System.out.println("Team:\n" + team);

		}

		// assign team to employees
		int team_index = 0;
		for (Employee employee : employees) {
			teams.get(team_index).getEmployees().add(employee);
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
//			data.setTime(end);
//			data.add(Calendar.DAY_OF_MONTH, -1 * (duration ));
			
			LocalDate endMinusDuration = end.plusDays(-duration);
			
			//System.out.println("duration: "+duration+" end Max: "+data.getTime());

			LocalDate expectedStartTime = between(start, endMinusDuration);

			LocalDate expected_end_time = expectedStartTime.plusDays(duration - 1);
			Task task = new Task("Task: " + generatedString, expectedStartTime, null, expected_end_time, null);
			task.setId(i + 1);
			tasks.add(task);
			// System.out.println("Task \n" + task);

		}

		// assign at least 1 task for each employee
		int task_index = 0;
		for (Employee employee : employees) {
			tasks.get(task_index).setEmployee(employee);
			employee.getTasks().add(tasks.get(task_index));
			
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

		for (Task t : tasks) {
			if(t.getEmployee()!=null)
			System.out.println(t);
		}
		

		printOutput(start, end, tasks);
		System.out.println("\nAvailability: ");
		for (Employee employee : employees) {
		printEmployeeScheduling(employee, days_max, start, end);
		System.out.println("");
		
		}

	}

	private static void printEmployeeScheduling(Employee employee, long schedule_size, LocalDate start, LocalDate end) {
		
		Boolean[] schedule = new Boolean[(int) schedule_size];
		Arrays.fill(schedule, Boolean.FALSE);
		
		for (Task task : employee.getTasks()) {
			//System.out.println("Task start "+t.getExpectedStartTime()+" , End: "+t.getExpectedEndTime());
			long diff1 = ChronoUnit.DAYS.between(start, task.getExpectedStartTime());
			long task_duration = ChronoUnit.DAYS.between(task.getExpectedStartTime(), task.getExpectedEndTime())+1;
			long diff2 = ChronoUnit.DAYS.between(task.getExpectedEndTime(), end);
			
			//System.out.println("diff1: "+diff1+", task_dur: "+task_duration+", diff2 "+diff2);
			
			

			for (int i = (int) diff1; i < diff1+task_duration; i++) {
				schedule[i] = true;
			}

			
		}
		
		
		System.out.print(employee.getUserName() + " ");
		for(Boolean value: schedule) {
			if(value.booleanValue()==true) {
				System.out.print("X ");
			}
			else {
				System.out.print("_ ");
			}
		}
		
	}

	public static void printOutput(LocalDate start, LocalDate end, List<Task> tasks) {
		for (Task t : tasks) {
			if(t.getEmployee()!=null) {
			System.out.print(t.getEmployee().getUserName() + ", " + t.getDescription() + " ");

			long diff1 = ChronoUnit.DAYS.between(start, t.getExpectedStartTime());
			long task_duration = ChronoUnit.DAYS.between(t.getExpectedStartTime(), t.getExpectedEndTime())
					+ 1;
					long diff2 = ChronoUnit.DAYS.between(t.getExpectedEndTime(), end);

//			System.out.println("\ndiff1: "+diff1);
//			System.out.println("task_duration: "+task_duration);

//			System.out.println("diff2: "+diff2);
//			System.out.println(end+ "\n"+ t.getExpectedEndTime());
//			System.out.println(end.getTime()+ "\n"+ t.getExpectedEndTime().getTime());

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

	public static boolean assignTask(Task task, List<Employee> employees, LocalDate start, LocalDate end) {
		// per non assegnare tutti i task al primo impiegato
		Collections.shuffle(employees);
		
		for (Employee employee : employees) {
			if (employeeAvailable(employee, task.getExpectedStartTime(), task.getExpectedEndTime())) {
				task.setEmployee(employee);
				employee.getTasks().add(task);
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
				return true;
			}
		}

		// Nessun impiegato ha una disponibilità
		return false;
	}

	public static LocalDate getAvailability(Employee employee, LocalDate start, LocalDate end) {
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

	public static LocalDate between(LocalDate startInclusive, LocalDate endInclusive) {
		Random rn = new Random();
		
		long days_max = (ChronoUnit.DAYS.between(startInclusive,endInclusive))+1;
		int days = rn.nextInt((int)days_max) ;
		
		LocalDate randomDate = startInclusive.plusDays(days);
		
		return randomDate;
	}
	
	public static boolean betweenTwoDate(LocalDate toCheck, LocalDate start, LocalDate end) {
		boolean result = (toCheck.isAfter(start) && toCheck.isBefore(end)) || toCheck.equals(start) || toCheck.equals(end);
		return result;
	}

	public static boolean employeeAvailable(Employee e, LocalDate startTask, LocalDate endTask) {
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

}
