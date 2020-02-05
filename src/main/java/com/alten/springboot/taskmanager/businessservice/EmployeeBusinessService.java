package com.alten.springboot.taskmanager.businessservice;

import com.alten.springboot.taskmanager.dataservice.IEmployeeDataService;
import com.alten.springboot.taskmanager.dataservice.ITeamDataService;
import com.alten.springboot.taskmanager.dto.EmployeeDto;
import com.alten.springboot.taskmanager.dto.TaskDto;
import com.alten.springboot.taskmanager.model.Employee;
import com.alten.springboot.taskmanager.model.Task;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class EmployeeBusinessService implements IEmployeeBusinessService {

    @Autowired
    private IEmployeeDataService employeeDataService;

    @Autowired
    private ITeamDataService teamDataService;

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

        return employeeDataService.findAll().stream().map(employee -> modelMapper.map(employee, EmployeeDto.class)).collect(Collectors.toList());
    }

    @Override
    public EmployeeDto findById(int employeeId) {

        Employee employee = employeeDataService.findById(employeeId);
        return modelMapper.map(employee, EmployeeDto.class);

    }

    @Override
    public EmployeeDto save(EmployeeDto employeeDto) {

        Employee employee = modelMapper.map(employeeDto, Employee.class);
        employee.setPassword("{noop}" + employee.getPassword());
        return modelMapper.map(employeeDataService.save(employee), EmployeeDto.class);
    }

    @Override
    public boolean update(EmployeeDto employeeDto) {

        Employee employee = modelMapper.map(employeeDto, Employee.class);
        return (employeeDataService.update(employee) != null) ? true : false;
    }

    @Override
    public void delete(int employeeId) {
        Employee employee = employeeDataService.findById(employeeId);
        employee.getTasks().parallelStream().forEach(t -> t.setEmployee(null));
        employeeDataService.delete(employeeId);
    }

    @Override
    public List<EmployeeDto> getAvailableEmployeesByTeamAndTask(int teamId, TaskDto theTask) {

        return teamDataService.findById(teamId).getEmployees().stream().filter(employee -> employeeAvailable(employee, LocalDate.parse(theTask.getExpectedStartTime()),
                LocalDate.parse(theTask.getExpectedEndTime()))).map(employee -> modelMapper.map(employee, EmployeeDto.class)).collect(Collectors.toList());
    }

    @Override
    public String getAvailabilityByEmployee(int employeeId, String start_date, String end_date) {
        Employee employee = employeeDataService.findById(employeeId);
        LocalDate start = LocalDate.parse(start_date);
        LocalDate end = LocalDate.parse(end_date);
        long schedule_size = ChronoUnit.DAYS.between(start, end) + 1;

        return printEmployeeScheduling(employee, schedule_size, start, end);

    }

    public static String printEmployeeScheduling(Employee employee, long schedule_size, LocalDate start, LocalDate end) {

        String[] schedule = new String[(int) schedule_size];
        Arrays.fill(schedule, "__");

        employee.getTasks().stream().forEach(task -> {
            LocalDate taskStartDate = task.getExpectedStartTime(), taskEndDate = task.getExpectedEndTime();
            // check on task start and end because a task can end over the end of the period specified or start before
            taskStartDate = (taskStartDate.isBefore(start)) ? start : taskStartDate;
            taskEndDate = (taskEndDate.isAfter(end)) ? end : taskEndDate;
            long taskStart = ChronoUnit.DAYS.between(start, taskStartDate), taskDuration = ChronoUnit.DAYS.between(taskStartDate, taskEndDate) + 1;
            String stringToPrint = task.getDescription();
            stringToPrint = (stringToPrint.length() < 2) ? "0" + stringToPrint : stringToPrint;
            String finalStringToPrint = stringToPrint;
            IntStream.range((int) taskStart, (int) (taskStart + taskDuration))
                    .forEach(i -> schedule[i] = finalStringToPrint);
        });

        StringBuilder sb = new StringBuilder();
        Stream.of(schedule).forEach(availability -> sb.append(availability + "  "));
        sb.append(employee.getUserName());
        return sb.toString();

    }

    public static boolean betweenTwoDate(LocalDate toCheck, LocalDate start, LocalDate end) {
        boolean result = (toCheck.isAfter(start) && toCheck.isBefore(end)) || toCheck.equals(start)
                || toCheck.equals(end);
        return result;
    }

    static boolean employeeAvailable(Employee e, LocalDate startTask, LocalDate endTask) {

        Optional<Task> result = e.getTasks().stream().filter(t -> betweenTwoDate(startTask, t.getExpectedStartTime(), t.getExpectedEndTime())
                || betweenTwoDate(endTask, t.getExpectedStartTime(), t.getExpectedEndTime())
                || betweenTwoDate(t.getExpectedStartTime(), startTask, endTask)
                || betweenTwoDate(t.getExpectedEndTime(), startTask, endTask))
                .findFirst();

        if (result.isPresent()) return false;

        return true;
    }

}
