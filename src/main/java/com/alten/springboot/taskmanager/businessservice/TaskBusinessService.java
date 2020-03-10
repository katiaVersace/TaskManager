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
        LocalDate today = LocalDate.now();
        //se exTaskStart è < oggi oppure exTaskStart > exTaskEnd oppure ((realTaskStart!= null o realTaskEnd !=null) && (realTaskStart<oggi oppure realTaskStart>realTaskEnd)) le date non sono valide
        return !(theTask.getExpectedStartTime().isBefore(today) || theTask.getExpectedStartTime().isAfter(theTask.getExpectedEndTime()) ||
                ((theTask.getRealStartTime() != null || theTask.getRealEndTime() != null) && (theTask.getRealStartTime().isBefore(today) || theTask.getRealStartTime().isAfter(theTask.getRealEndTime()))));
    }

    @Override
    public TaskDto findById(int taskId) {
        Task task = taskDataService.findById(taskId);
        return (task != null) ? modelMapper.map(task, TaskDto.class) : null;
    }

    @Override
    public TaskDto save(TaskDto taskDto) {
        Task task = modelMapper.map(taskDto, Task.class);
        if (!checkDate(task))
            return null;

        Employee employee = employeeDataService.findById(taskDto.getEmployeeId());
        taskDto = null;
        if (EmployeeBusinessService.employeeAvailable(employee, task.getExpectedStartTime(), task.getExpectedEndTime())) {
            TeamBusinessService.assignTaskToEmployee(task, employee);
            taskDto = modelMapper.map(taskDataService.save(task), TaskDto.class);
            employeeDataService.update(employee);
        }

        return taskDto;
    }

    @Override
    public boolean update(TaskDto taskDto) {

        Task task = modelMapper.map(taskDto, Task.class);
        if (!checkDate(task)) {
            return false;
        }
        Task result = taskDataService.update(task);
        return result != null;
    }

    @Override
    public void delete(int taskId) {

        Task task = taskDataService.findById(taskId);
        Employee employee = employeeDataService.findById(task.getEmployee().getId());
        List<Task> tasks = employee.getTasks();

        Task taskToDelete = tasks.stream().filter(t -> t.getId() == taskId).findFirst().orElse(null);
        if (taskToDelete != null) {

            TeamBusinessService.assignTaskToEmployee(taskToDelete, null);
            employeeDataService.update(employee);
            taskDataService.delete(taskId);

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
