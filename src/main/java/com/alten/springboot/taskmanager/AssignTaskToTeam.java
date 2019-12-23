package com.alten.springboot.taskmanager;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alten.springboot.taskmanager.entity.Employee;
import com.alten.springboot.taskmanager.entity.Task;
import com.alten.springboot.taskmanager.entity.Team;

public class AssignTaskToTeam {

	public static void main(String[] args) {
		
		Team team1 = new Team("Team1");
		
		Employee luca = new Employee();
		luca.setUserName("Luca");
		Employee katia = new Employee();
		katia.setUserName("Katia");
		
		team1.getEmployees().add(luca);
		team1.getEmployees().add(katia);
		
		LocalDate start = LocalDate.parse("2019-12-16");
		LocalDate end =  LocalDate.parse("2019-12-23");
		long days_max = ChronoUnit.DAYS.between(start, end)+1;
		
		Task task_A = new Task("TASK A", LocalDate.parse("2019-12-18"), null, LocalDate.parse("2019-12-21"), null);
		Task task_B = new Task("TASK B", LocalDate.parse("2019-12-21"), null, LocalDate.parse("2019-12-22"), null);
		Task task_C = new Task("TASK C", LocalDate.parse("2019-12-17"), null, LocalDate.parse("2019-12-19"), null);
		Task task_D = new Task("TASK D", LocalDate.parse("2019-12-17"), null, LocalDate.parse("2019-12-17"), null);
		Task task_E = new Task("TASK E", LocalDate.parse("2019-12-23"), null, LocalDate.parse("2019-12-23"), null);
		
		luca.getTasks().add(task_D);
		luca.getTasks().add(task_B);
		task_D.setEmployee(luca);
		task_B.setEmployee(luca);
		
		katia.getTasks().add(task_C);
		katia.getTasks().add(task_E);
		task_C.setEmployee(katia);
		task_E.setEmployee(katia);
		
		printEmployeeScheduling(luca, days_max, start, end);
		printEmployeeScheduling(katia, days_max, start, end);
		
		System.out.println("Provo ad assegnare TASK_A");
		
		List<Task> visti = new ArrayList<>();
		assignTaskToTeam(task_A, team1.getEmployees(), visti);
		
		printEmployeeScheduling(luca, days_max, start, end);
		printEmployeeScheduling(katia, days_max, start, end);
		
	}
	
private static boolean assignTaskToTeam(Task task, List<Employee> team, List<Task> visti) {
	
	//caso base negativo
	if(visti.contains(task)) {
		return false;
	}
	
	//caso base positivo
	for(Employee employee: team) {
		if(employee != task.getEmployee()) {
			if(employeeAvailable(employee, task.getExpectedStartTime(), task.getExpectedEndTime())) {
				employee.getTasks().add(task);
				task.setEmployee(employee);
				return true;
			}
		}
	}
	
	//passo ricorsivo
	for(Employee employee: team) {
		if(employee != task.getEmployee()) {
			
			if(!visti.contains(task))
				visti.add(task);
			
			List<Task> tasks_in_period = getTasksInPeriod(employee, task.getExpectedStartTime(),task.getExpectedEndTime());
			
			if(tasks_in_period.size()==1) { //se ci sono più task da spostare per ora non considero quell'impiegato
				
			Task task_to_rearrange = tasks_in_period.get(0);
			
			//if(!taskInProgress(task_to_rearrange)) { 
				if(assignTaskToTeam(task_to_rearrange, team, visti))
				{
					employee.getTasks().add(task);
					task.setEmployee(employee);
					return true;
				}
			//}
			}
			
		}
	}
		return false;
	}

private static boolean taskInProgress(Task task_to_rearrange) {
	LocalDate today = LocalDate.now();
	if((task_to_rearrange.getExpectedStartTime().isBefore(today) && task_to_rearrange.getExpectedEndTime().isAfter(today)) || task_to_rearrange.getExpectedStartTime().equals(today) || task_to_rearrange.getExpectedEndTime().equals(today) )
	return true;
	
	else
		return false;
}

private static List<Task> getTasksInPeriod(Employee employee, LocalDate start, LocalDate end) {
	List<Task> tasks_in_period = new ArrayList<Task>();
	for(Task t: employee.getTasks())
	{
		if (betweenTwoDate(start, t.getExpectedStartTime(), t.getExpectedEndTime())  ||  betweenTwoDate(end, t.getExpectedStartTime(), t.getExpectedEndTime())
				||	betweenTwoDate(t.getExpectedStartTime(), start, end)  ||  betweenTwoDate(t.getExpectedEndTime(), start, end)
					) {
			tasks_in_period.add(t);
		}
	}
	
	return tasks_in_period;
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
		
		
		
		for(Boolean value: schedule) {
			if(value.booleanValue()==true) {
				System.out.print("X ");
			}
			else {
				System.out.print("_ ");
			}
		}
		System.out.println(" "+employee.getUserName() + " ");
	}

}
