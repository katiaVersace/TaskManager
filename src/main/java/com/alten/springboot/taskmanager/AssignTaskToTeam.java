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

		testPositivo();
	
		System.out.println("-----------------");

		testNegativo();
	}

	public static void testPositivo() {
		System.out.println("Test Positivo: ");
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
		
		
		System.out.println("Prima");
		printEmployeeScheduling(luca, days_max, start, end);
		printEmployeeScheduling(katia, days_max, start, end);
		
		System.out.print("Provo ad assegnare TASK_A: ");
		printTaskSchedule(task_A, start, end);

		List<Task> visti = new ArrayList<>();
		if(assignTaskToTeam(task_A, team1.getEmployees(), visti))
			System.out.println("Assegnamento riuscito"+", all'impiegato: "+task_A.getEmployee().getUserName());
		else System.out.println("Assegnamento fallito");
		printEmployeeScheduling(luca, days_max, start, end);
		printEmployeeScheduling(katia, days_max, start, end);	
		
	}

	public static void testNegativo() {
		System.out.println("Test Negativo: ");
		Team team1 = new Team("Team1");
		Employee A = new Employee();
		A.setUserName("A");
		Employee B = new Employee();
		B.setUserName("B");
		Employee C = new Employee();
		C.setUserName("C");
		Employee D = new Employee();
		D.setUserName("D");
		
		team1.getEmployees().add(A);
		team1.getEmployees().add(B);
		team1.getEmployees().add(C);
		team1.getEmployees().add(D);
		
		LocalDate start = LocalDate.parse("2019-12-16");
		LocalDate end =  LocalDate.parse("2019-12-22");
		long days_max = ChronoUnit.DAYS.between(start, end)+1;
		
		Task task_A = new Task("TASK A", LocalDate.parse("2019-12-19"), null, LocalDate.parse("2019-12-22"), null);
		Task task_B = new Task("TASK B", LocalDate.parse("2019-12-16"), null, LocalDate.parse("2019-12-20"), null);
		Task task_C = new Task("TASK C", LocalDate.parse("2019-12-18"), null, LocalDate.parse("2019-12-20"), null);
		Task task_D = new Task("TASK D", LocalDate.parse("2019-12-18"), null, LocalDate.parse("2019-12-18"), null);
		Task task_E = new Task("TASK E", LocalDate.parse("2019-12-20"), null, LocalDate.parse("2019-12-22"), null);
		Task task_F = new Task("TASK F", LocalDate.parse("2019-12-17"), null, LocalDate.parse("2019-12-18"), null);
		Task task_G = new Task("TASK G", LocalDate.parse("2019-12-19"), null, LocalDate.parse("2019-12-20"), null);
		
		A.getTasks().add(task_B);
		task_B.setEmployee(A);
		
		B.getTasks().add(task_C);
		task_C.setEmployee(B);
		
		C.getTasks().add(task_D);
		task_D.setEmployee(C);
		
		C.getTasks().add(task_E);
		task_E.setEmployee(C);
		
		D.getTasks().add(task_F);
		task_F.setEmployee(D);
		
		D.getTasks().add(task_G);
		task_G.setEmployee(D);
		
		System.out.println("Prima");
		
		for(Employee e: team1.getEmployees()) {
			printEmployeeScheduling(e, days_max, start, end);
		}
		
		System.out.print("Provo ad assegnare TASK_A: ");
		printTaskSchedule(task_A, start, end);
		
		List<Task> visti = new ArrayList<>();
		if(assignTaskToTeam(task_A, team1.getEmployees(), visti))
			System.out.println("Assegnamento riuscito"+", All'impiegato: "+task_A.getEmployee().getUserName());
		else System.out.println("Assegnamento fallito");
		
		System.out.println("Dopo");
		
		for(Employee e: team1.getEmployees()) {
			printEmployeeScheduling(e, days_max, start, end);
		}
		
	}

	private static boolean assignTaskToTeam(Task task, List<Employee> team, List<Task> visti) {

		// caso base negativo
		if (visti.contains(task)) {
			return false;
		}

		// caso base positivo
		for (Employee employee : team) {
			if (employee != task.getEmployee()) {
				if (employeeAvailable(employee, task.getExpectedStartTime(), task.getExpectedEndTime())) {
					Employee oldEmployee = task.getEmployee();
					if (oldEmployee != null)
						oldEmployee.getTasks().remove(task);
					employee.getTasks().add(task);
					task.setEmployee(employee);
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

				if (tasks_in_period.size() == 1) { // se ci sono più task da spostare per ora non considero
													// quell'impiegato

					Task task_to_rearrange = tasks_in_period.get(0);

					// if(!taskInProgress(task_to_rearrange)) {
					if (assignTaskToTeam(task_to_rearrange, team, visti)) {
						Employee oldEmployee = task.getEmployee();
						if (oldEmployee != null)
							oldEmployee.getTasks().remove(task);
						employee.getTasks().add(task);
						task.setEmployee(employee);
						return true;
					}
					// }
				}

			}
		}
		return false;
	}

	private static boolean taskInProgress(Task task_to_rearrange) {
		LocalDate today = LocalDate.now();
		if ((task_to_rearrange.getExpectedStartTime().isBefore(today)
				&& task_to_rearrange.getExpectedEndTime().isAfter(today))
				|| task_to_rearrange.getExpectedStartTime().equals(today)
				|| task_to_rearrange.getExpectedEndTime().equals(today))
			return true;

		else
			return false;
	}

	private static List<Task> getTasksInPeriod(Employee employee, LocalDate start, LocalDate end) {
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

	public static boolean betweenTwoDate(LocalDate toCheck, LocalDate start, LocalDate end) {
		boolean result = (toCheck.isAfter(start) && toCheck.isBefore(end)) || toCheck.equals(start)
				|| toCheck.equals(end);
		return result;
	}

	public static boolean employeeAvailable(Employee e, LocalDate startTask, LocalDate endTask) {
		for (Task t : e.getTasks()) {
			if (betweenTwoDate(startTask, t.getExpectedStartTime(), t.getExpectedEndTime())
					|| betweenTwoDate(endTask, t.getExpectedStartTime(), t.getExpectedEndTime())
					|| betweenTwoDate(t.getExpectedStartTime(), startTask, endTask)
					|| betweenTwoDate(t.getExpectedEndTime(), startTask, endTask)) {
				// System.out.println(e.getUserName() + " Not available for task with start in "
				// + startTask);

				return false;

			}

		}

		// System.out.println(e.getUserName() + " available for task with start in " +
		// startTask);
		return true;
	}

	private static void printEmployeeScheduling(Employee employee, long schedule_size, LocalDate start, LocalDate end) {

		// System.out.println(employee);
		Boolean[] schedule = new Boolean[(int) schedule_size];
		Arrays.fill(schedule, Boolean.FALSE);

		for (Task task : employee.getTasks()) {
			// System.out.println("Task start "+t.getExpectedStartTime()+" , End:
			// "+t.getExpectedEndTime());
			long diff1 = ChronoUnit.DAYS.between(start, task.getExpectedStartTime());
			long task_duration = ChronoUnit.DAYS.between(task.getExpectedStartTime(), task.getExpectedEndTime()) + 1;

			// System.out.println("diff1: "+diff1+", task_dur: "+task_duration);

			// System.out.println(task);

			for (int i = (int) diff1; i < diff1 + task_duration; i++) {
				schedule[i] = true;
			}

		}

		for (Boolean value : schedule) {
			if (value.booleanValue() == true) {
				System.out.print("X ");
			} else {
				System.out.print("_ ");
			}
		}
		System.out.println(" " + employee.getUserName() + " ");
	}
	public static void printTaskSchedule(Task task, LocalDate start, LocalDate end) {
		
			
			

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
}
