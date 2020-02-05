package com.alten.springboot.taskmanager.businessservice;

import com.alten.springboot.taskmanager.dataservice.IEmployeeDataService;
import com.alten.springboot.taskmanager.dataservice.ITaskDataService;
import com.alten.springboot.taskmanager.dto.TaskDto;
import com.alten.springboot.taskmanager.model.Employee;
import com.alten.springboot.taskmanager.model.Task;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskBusinessService implements ITaskBusinessService {

    @Autowired
    private ITaskDataService taskDataService;

    @Autowired
    private IEmployeeDataService employeeDataService;

    @Autowired
    private ModelMapper modelMapper;

    public static boolean checkDate(Task theTask) {
        // check sulle date
        LocalDate today = LocalDate.now();
        if ((!((theTask.getExpectedStartTime().isAfter(today) || theTask.getExpectedStartTime().equals(today))
                && (theTask.getExpectedStartTime().isBefore(theTask.getExpectedEndTime())
                || theTask.getExpectedStartTime().equals(theTask.getExpectedEndTime()))))
                || ((theTask.getRealStartTime() != null || theTask.getRealEndTime() != null) &&
                !((theTask.getRealStartTime().isAfter(today) || theTask.getRealStartTime().equals(today))
                        && (theTask.getRealStartTime().isBefore(theTask.getRealEndTime())
                        || theTask.getRealStartTime().equals(theTask.getRealEndTime())))))
            return false;

        return true;
    }

    @Override
    public TaskDto findById(int taskId) {
        Task task = taskDataService.findById(taskId);

        TaskDto taskDto = null;
        if (task != null) {
            taskDto = modelMapper.map(task, TaskDto.class);
        }
        return taskDto;

    }

    @Override
    public TaskDto save(TaskDto taskDto) {
        Task task = modelMapper.map(taskDto, Task.class);

        if (!checkDate(task))
            return null;

        Employee employee = employeeDataService.findById(taskDto.getEmployeeId());
        taskDto = null;
        if (EmployeeBusinessService.employeeAvailable(employee, task.getExpectedStartTime(), task.getExpectedEndTime())) {
            task.setEmployee(employee);
            List<Task> tasks = employee.getTasks();
            if (!tasks.contains(task))
                tasks.add(task);

            taskDto = modelMapper.map(taskDataService.save(task), TaskDto.class);

            if (tasks.size() >= 5 && !employee.isTopEmployee()) {

                employee.setTopEmployee(true);
                employeeDataService.update(employee);
            }
        }

        return taskDto;
    }

    @Override
    public boolean update(TaskDto taskDto) {

        Task task = modelMapper.map(taskDto, Task.class);
        //task.setRealStartTime(LocalDate.parse(taskDto.getRealStartTime()));
        //task.setRealEndTime(LocalDate.parse(taskDto.getRealEndTime()));
        if (!checkDate(task)) {
            return false;
        }
        Task result = taskDataService.update(task);

        return (result != null) ? true : false;
    }

    @Override
    public void delete(int taskId) {

        Task task = taskDataService.findById(taskId);
        Employee employee = employeeDataService.findById(task.getEmployee().getId());
        List<Task> tasks = employee.getTasks();

        Task taskToDelete = tasks.stream().filter(t -> t.getId() == taskId).findFirst().orElse(null);
        if (taskToDelete != null) {
            tasks.remove(taskToDelete);
            taskToDelete.setEmployee(null);

            if (employee.getTasks().size() < 5 && employee.isTopEmployee()) {
                employee.setTopEmployee(false);

            }
            employeeDataService.update(employee);

        }
    }

    @Override
    public List<TaskDto> findAll() {
        return taskDataService.findAll().stream().map(task -> modelMapper.map(task, TaskDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> findByEmployeeId(int employeeId) {
        return taskDataService.findByEmployeeId(employeeId).stream().map(task -> modelMapper.map(task, TaskDto.class)).collect(Collectors.toList());
    }
}
