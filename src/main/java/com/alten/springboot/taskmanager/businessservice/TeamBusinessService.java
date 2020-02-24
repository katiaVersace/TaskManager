package com.alten.springboot.taskmanager.businessservice;

import com.alten.springboot.taskmanager.dataservice.IEmployeeDataService;
import com.alten.springboot.taskmanager.dataservice.ITaskDataService;
import com.alten.springboot.taskmanager.dataservice.ITeamDataService;
import com.alten.springboot.taskmanager.dto.TaskDto;
import com.alten.springboot.taskmanager.dto.TeamDto;
import com.alten.springboot.taskmanager.model.Employee;
import com.alten.springboot.taskmanager.model.Task;
import com.alten.springboot.taskmanager.model.Team;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class TeamBusinessService implements ITeamBusinessService {

    private final int NRANDOMCHARS = 2;

    private final int PARALLELISM = 4;

    @Autowired
    private ITeamDataService teamDataService;

    @Autowired
    private IEmployeeDataService employeeDataService;

    @Autowired
    private ITaskDataService taskDataService;

    @Autowired
    private ModelMapper modelMapper;

    public static void assignTaskToEmployee(Task task, Employee employee) {
        if (task.getEmployee() != null) {
            task.getEmployee().getTasks().remove(task);
            if (task.getEmployee().getTasks().size() < 5 && task.getEmployee().isTopEmployee()) {
                task.getEmployee().setTopEmployee(false);
            }
        }
        if (employee != null) {
            employee.getTasks().add(task);
            if (employee.getTasks().size() >= 5 && !employee.isTopEmployee()) {
                employee.setTopEmployee(true);
            }
        }
        task.setEmployee(employee);
    }

    @Override
    public List<TeamDto> findAll() {

        return teamDataService.findAll().parallelStream().map(team -> modelMapper.map(team, TeamDto.class)).collect(Collectors.toList());
    }

    @Override
    public TeamDto findById(int teamId) {
        Team team = teamDataService.findById(teamId);
        return (team != null) ? modelMapper.map(team, TeamDto.class) : null;

    }

    @Override
    public TeamDto save(TeamDto teamDto) {
        return modelMapper.map(teamDataService.save(modelMapper.map(teamDto, Team.class)), TeamDto.class);
    }

    @Override
    public boolean update(TeamDto teamDto) {
        return teamDataService.update(modelMapper.map(teamDto, Team.class)) != null ? true : false;
    }

    @Override
    public void delete(int teamId) {
        teamDataService.delete(teamId);
    }

    @Override
    public void deleteAll() {
        teamDataService.deleteAll();
    }

    @Override
    public String randomPopulation(String start_date, String end_date, int teams_size, int employees_size,
                                   int tasks_size, int task_max_duration) {

        // input
        LocalDate start = LocalDate.parse(start_date), end = LocalDate.parse(end_date);

        // Check input
        long days_max = ChronoUnit.DAYS.between(start, end) + 1;
        if (teams_size > (employees_size / 2) || tasks_size < employees_size || task_max_duration > days_max) {
            return "invalid input";
        }

        // create Employees
        List<Employee> employees = IntStream.range(0, employees_size).parallel()
                .mapToObj(i -> {
                    String generatedString = RandomStringUtils.randomAlphabetic(NRANDOMCHARS).toUpperCase();
                    Employee employee = new Employee("EMP_" + generatedString, generatedString, "Name_" + generatedString,
                            "LastName_" + generatedString, generatedString + "@alten.it", false);
                    return employee;
                }).collect(Collectors.toList());


        // create Teams
        List<Team> teams = IntStream.range(0, teams_size).parallel()
                .mapToObj(i -> {
                    String generatedString = RandomStringUtils.randomAlphabetic(NRANDOMCHARS).toUpperCase();
                    Team team = new Team("TEAM_" + generatedString);
                    return team;
                }).collect(Collectors.toList());


        // assign employees to teams balancing the teams
        int employees_for_team = employees_size / teams_size;
        AtomicInteger difference = new AtomicInteger(employees_size % teams_size), current_emp = new AtomicInteger();

        teams.stream().forEach(t -> {
                    IntStream.range(current_emp.get(), current_emp.get() + employees_for_team + difference.get())

                    .forEach(i -> t.getEmployees().add(employees.get(i)));
                    current_emp.addAndGet(employees_for_team + difference.get());
                    difference.set(0);
                }
        );


        // create Tasks
        List<Task> tasks = IntStream.range(0, tasks_size).parallel()
                .mapToObj(i -> {
                    int duration = new Random().nextInt(task_max_duration) + 1;
                    String generatedString = RandomStringUtils.randomAlphabetic(NRANDOMCHARS).toUpperCase();
                    LocalDate endMinusDuration = end.plusDays(-duration);
                    LocalDate expectedStartTime = between(start, endMinusDuration);
                    LocalDate expectedEndTime = expectedStartTime.plusDays(duration - 1);
                    Task task = new Task(generatedString, expectedStartTime, null, expectedEndTime, null);

                    return task;
                }).collect(Collectors.toList());

        // assign at least 1 task for each employee
        IntStream.range(0, employees.size()).parallel()
                .forEach(i -> assignTaskToEmployee(tasks.get(i), employees.get(i)));

        // assign remaining task
        List<Task> remainingTasks = new ArrayList<>(tasks.subList(employees.size(), tasks.size()));

        //Non posso farlo in parallelo perchè altrimenti potrebbe capitare che due task che avvengono nello stesso periodo
        //siano assegnati entrambi allo stesso impiegato
        remainingTasks.stream().forEach(task -> {
            if (!assignTaskRandom(task, employees, start, end)) {
                return;
            }
        });

        //PERSIST
        employeeDataService.saveAll(employees);
        teamDataService.saveAll(teams);
        taskDataService.saveAll(tasks);

        //OUTPUT
        StringBuilder sb = new StringBuilder();
        sb.append("Scheduling:\n" + printDays(start, days_max) + "\n");
        sb.append(employees.stream().map(e -> EmployeeBusinessService.printEmployeeScheduling(e, days_max, start, end)).collect(Collectors.joining("\n")));
        sb.append("\n" + printFreeEmployees(start, end, days_max, employees));
        System.out.println(sb.toString());

        return sb.toString();
    }

    @Override
    public TaskDto tryAssignTaskToTeam(String start_date, String end_date, int team_id, TaskDto theTaskDto) {

        Task task = modelMapper.map(theTaskDto, Task.class);
        Team team = teamDataService.findById(team_id);
        LocalDate start = LocalDate.parse(start_date), end = LocalDate.parse(end_date);

        if (!TaskBusinessService.checkDate(task))
            return null;

        // sort employees in  base alla loro disponibilità nel periodo del task
        List<Employee> ordered_employees = new ArrayList<>();
        ordered_employees.addAll(team.getEmployees());
        Collections.sort(ordered_employees, Comparator.comparingInt(e -> getTasksInPeriod(e, task.getExpectedStartTime(), task.getExpectedEndTime()).size()));

        List<Task> visitedTasks = new ArrayList<>();
        HashMap<Task, Employee> oldAssignments = new HashMap<Task, Employee>();
        if (checkNoSolution(task.getExpectedStartTime(), task.getExpectedEndTime(), ordered_employees)
                || !assignTaskToTeam(task, team.getEmployees(), visitedTasks, oldAssignments)) {

            System.out.println("Impossible to assign the task");
            return null;

        } else {

            //PERSIST
            theTaskDto = visitedTasks.stream().filter(t -> oldAssignments.get(t) == null).map(t-> {
                t=taskDataService.save(t);
               return modelMapper.map(t, TaskDto.class);})
                    .findFirst().orElse(null);


            taskDataService.saveAll(visitedTasks);
            List<Employee> employeesToSave = new ArrayList<>(visitedTasks.stream().map(t->t.getEmployee()).collect(Collectors.toList()));
            employeesToSave.addAll(oldAssignments.entrySet().stream().map(entry -> entry.getValue()).filter(e->e!=null).collect(Collectors.toList()));
            employeeDataService.saveAll(employeesToSave);

            //OUTPUT
            System.out.println("Solution: ");
            visitedTasks.stream().forEach(taskInSolution ->
                    System.out.println(String.format("%s -> %s", taskInSolution.getDescription(), taskInSolution.getEmployee().getUserName()))
            );
            LocalDate final_start = start.minusDays(10), final_end = end.plusDays(10);
            long final_days_max = (ChronoUnit.DAYS.between(final_start, final_end)) + 1;
            System.out.println("New scheduling:\n" + printDays(final_start, final_days_max));
            team.getEmployees().stream().map(e -> EmployeeBusinessService.printEmployeeScheduling(e, final_days_max, final_start, final_end)).forEach(System.out::println);
            System.out.println(printFreeEmployees(final_start, final_end, final_days_max, new ArrayList<>(team.getEmployees())));

            return theTaskDto;
        }

    }

    private String printDays(LocalDate start, long daysMax) {

        StringBuilder sb = new StringBuilder();
        IntStream.range(0, (int) daysMax)
                .forEach(i -> {
                    LocalDate currentDay = start.plusDays(i);
                    String day = String.valueOf(currentDay.getDayOfMonth());
                    if (currentDay.getDayOfMonth() < 10) {
                        day = "0" + day;
                    }
                    sb.append(day + "  ");
                });
        return sb.toString();
    }

    public boolean assignTaskRandom(Task task, List<Employee> employees, LocalDate start, LocalDate end) {

        // per non assegnare tutti i task al primo impiegato
        Collections.shuffle(employees);
        AtomicBoolean scheduled = new AtomicBoolean(false);
        ForkJoinPool customThreadPool = new ForkJoinPool(PARALLELISM);
        try {

            customThreadPool.submit(() ->

                    employees.parallelStream().filter(employee -> EmployeeBusinessService.employeeAvailable(employee, task.getExpectedStartTime(), task.getExpectedEndTime()))
                            .findFirst().ifPresent(employee -> {
                        assignTaskToEmployee(task, employee);
                        scheduled.set(true);
                    })

            ).get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (scheduled.get()) {
            return true;
        }

        //nessun impiegato è disponibile in quel periodo quindi mi  prendo le disponibilità, ove ce ne siano
        employees.parallelStream().filter(employee -> getAvailability(employee, start, end) != null).findFirst().ifPresent(employee -> {
            LocalDate availability = getAvailability(employee, start, end);
            task.setExpectedStartTime(availability);
            task.setExpectedEndTime(availability);
            assignTaskToEmployee(task, employee);
            scheduled.set(true);
        });

        return scheduled.get();
    }

    public LocalDate getAvailability(Employee employee, LocalDate start, LocalDate end) {

        return IntStream.range(0, (int) (ChronoUnit.DAYS.between(start, end) + 1)).parallel()
                .filter(i -> EmployeeBusinessService.employeeAvailable(employee, start.plusDays(i), start.plusDays(i)))
                .mapToObj(i -> start.plusDays(i))
                .findFirst().orElse(null);

    }

    public LocalDate between(LocalDate startInclusive, LocalDate endInclusive) {

        long daysMax = (ChronoUnit.DAYS.between(startInclusive, endInclusive)) + 1;
        return startInclusive.plusDays(new Random().nextInt((int) daysMax));
    }

    public boolean assignTaskToTeam(Task task, Set<Employee> team, List<Task> visitedTasks,
                                    HashMap<Task, Employee> oldAssignments) {

        visitedTasks.add(task);
        Employee oldEmployee = task.getEmployee();

        // caso base positivo (c'è un impiegato libero)
        AtomicBoolean scheduled = new AtomicBoolean(false);
        ForkJoinPool customThreadPool = new ForkJoinPool(PARALLELISM); // you might need to adjust this value to find optimal performance
        try {

            customThreadPool.submit(() ->
                    team.parallelStream().filter(employee -> employee != task.getEmployee() && EmployeeBusinessService.employeeAvailable(employee, task.getExpectedStartTime(), task.getExpectedEndTime()))
                            .findFirst().ifPresent(newEmployee -> {
                        assignTaskToEmployee(task, newEmployee);
                        oldAssignments.put(task, oldEmployee);
                        scheduled.set(true);
                    })

            ).get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (scheduled.get()) return true;

        // passo ricorsivo
        Optional<Employee> result = team.stream().filter(employee -> employee != task.getEmployee() && reassign(employee, task, visitedTasks, team, oldAssignments, oldEmployee) == true).findFirst();
        if (result.isPresent()) return true;

        visitedTasks.remove(task);
        return false;
    }

    public boolean reassign(Employee employee, Task task, List<Task> visitedTasks, Set<Employee> team, HashMap<Task, Employee> oldAssignments, Employee oldEmployee) {

        List<Task> tasksInPeriod = getTasksInPeriod(employee, task.getExpectedStartTime(),
                task.getExpectedEndTime());

        // forzatamente assegno il task all'impiegato e cerco di spostare i task di intralcio
        assignTaskToEmployee(task, employee);
        AtomicBoolean atLeastOneFailed = new AtomicBoolean(false);

        tasksInPeriod.stream().forEach(taskToRearrange -> {
            if (visitedTasks.contains(taskToRearrange)) {
                atLeastOneFailed.set(true);
                return;
            }
            HashMap<Task, Employee> oldAssignmentsForBranch = new HashMap<Task, Employee>();
            if (taskInProgress(taskToRearrange)
                    || !assignTaskToTeam(taskToRearrange, team, visitedTasks, oldAssignmentsForBranch)) {
                atLeastOneFailed.set(true);
                return;
            } else {
                // tutti i branch hanno restituito true quindi faccio merge dei vecchi assegnamenti dei task in quei branch
                oldAssignments.putAll(oldAssignmentsForBranch);
            }

        });

        //se almeno uno degli assegnamenti dei task di intralcio fallisce, ripristino
        if (atLeastOneFailed.get()) {

            assignTaskToEmployee(task, oldEmployee);
            // ripristino i task dei branch che hanno restituito true
            oldAssignments.entrySet().parallelStream().forEach(entry -> {
                Task taskToRevert = entry.getKey();
                assignTaskToEmployee(taskToRevert, entry.getValue());
                visitedTasks.remove(taskToRevert);
            });
            oldAssignments.clear();
            return false;
        } else {
            oldAssignments.put(task, oldEmployee);
            return true;
        }
    }

    private boolean taskInProgress(Task task_to_rearrange) {

        LocalDate today = LocalDate.now();
        return (task_to_rearrange.getExpectedStartTime().isBefore(today)
                && task_to_rearrange.getExpectedEndTime().isAfter(today))
                || task_to_rearrange.getExpectedStartTime().equals(today)
                || task_to_rearrange.getExpectedEndTime().equals(today);
    }

    private List<Task> getTasksInPeriod(Employee employee, LocalDate start, LocalDate end) {
        return employee.getTasks().parallelStream().filter(t -> EmployeeBusinessService.betweenTwoDate(start, t.getExpectedStartTime(), t.getExpectedEndTime())
                || EmployeeBusinessService.betweenTwoDate(end, t.getExpectedStartTime(), t.getExpectedEndTime())
                || EmployeeBusinessService.betweenTwoDate(t.getExpectedStartTime(), start, end)
                || EmployeeBusinessService.betweenTwoDate(t.getExpectedEndTime(), start, end)).collect(Collectors.toList());
    }

    public boolean checkNoSolution(LocalDate start, LocalDate end, List<Employee> employees) {

        int day_problem = IntStream.range(0, (int) (ChronoUnit.DAYS.between(start, end) + 1)).parallel()
                .filter(i ->
                        //non è disponibile un impiegato in quel giorno
                        (!employees.parallelStream().filter(employee -> EmployeeBusinessService.employeeAvailable(employee, start.plusDays(i), start.plusDays(i))).findFirst().isPresent())

                ).findFirst().orElse(-1);

        if (day_problem == -1) return false;

        return true;


    }

    private String printFreeEmployees(LocalDate start, LocalDate end, long scheduleSize, List<Employee> employees) {

        Integer[] freeEmployees = new Integer[(int) scheduleSize];
        Arrays.fill(freeEmployees, 0);

        IntStream.range(0, (int) (ChronoUnit.DAYS.between(start, end) + 1)).parallel()
                .forEach(i -> {
                    //parallel potrebbe sporcare i dati
                    employees.stream().filter(employee -> EmployeeBusinessService.employeeAvailable(employee, start.plusDays(i), start.plusDays(i))).forEach((e) -> freeEmployees[i]++);
                });

        StringBuilder sb = new StringBuilder();
        Stream.of(freeEmployees).forEach(nFreeEmployees -> {
            if (nFreeEmployees < 10) {
                sb.append("0" + nFreeEmployees + "  ");
            } else
                sb.append(nFreeEmployees + "  ");
        });

        sb.append("Free Employees");
        return sb.toString();
    }

}
