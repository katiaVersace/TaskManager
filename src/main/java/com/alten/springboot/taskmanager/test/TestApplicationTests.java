package com.alten.springboot.taskmanager.test;


import com.alten.springboot.taskmanager.TaskmanagerSpringBootApplication;
import com.alten.springboot.taskmanager.businessservice.EmployeeBusinessService;
import com.alten.springboot.taskmanager.dto.EmployeeDto;
import com.alten.springboot.taskmanager.dto.RandomPopulationInputDto;
import com.alten.springboot.taskmanager.dto.TaskDto;
import com.alten.springboot.taskmanager.dto.TeamDto;
import com.alten.springboot.taskmanager.model.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TaskmanagerSpringBootApplication.class)
@AutoConfigureMockMvc

public class TestApplicationTests {

    private static final String START_DATE = "2020-02-15";
    private static final String END_DATE = "2020-02-29";
    private static final int TEAMS_SIZE = 1;
    private static final int EMPLOYEES_SIZE = 10;
    private static final int TASKS_SIZE = 20;
    private static final int TASK_MAX_DURATION = 6;

    private static final String USER = "admin";
    private static final String PASSWORD = "admin";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;


    @Before
    public void setup() throws Exception {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .defaultRequest(get("/")
                        .with(user(USER).password(PASSWORD).roles("ADMIN")))
                .apply(springSecurity())
                .build();

        RandomPopulationInputDto input = new RandomPopulationInputDto();
        input.setStart(START_DATE);
        input.setEnd(END_DATE);
        input.setTeams_size(TEAMS_SIZE);
        input.setEmployees_size(EMPLOYEES_SIZE);
        input.setTasks_size(TASKS_SIZE);
        input.setTask_max_duration(TASK_MAX_DURATION);

        MvcResult result =mvc.perform( MockMvcRequestBuilders
                .post("/teams/randomPopulation")
                .content(mapper.writeValueAsString(input))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();

    }


    @Test
    public void testEmployeeAfterRandomPopulation() throws Exception {
        MvcResult result = mvc.perform(get("/employees").contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<EmployeeDto> employees = mapper.readValue(result.getResponse().getContentAsString(), mapper.getTypeFactory().constructCollectionType(List.class, EmployeeDto.class));
        //+1 PERCHÈ L'ADMIN ESISTE GIA'
        Assert.assertEquals(EMPLOYEES_SIZE+1,employees.size());

        result = mvc.perform(get("/teams").contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<TeamDto> teams = mapper.readValue(result.getResponse().getContentAsString(), mapper.getTypeFactory().constructCollectionType(List.class, TeamDto.class));
        Assert.assertEquals(TEAMS_SIZE,teams.size());

        result = mvc.perform(get("/tasks").contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<TaskDto> tasks = mapper.readValue(result.getResponse().getContentAsString(), mapper.getTypeFactory().constructCollectionType(List.class, TaskDto.class));
        Assert.assertEquals(TASKS_SIZE,tasks.size());
    }

    @Test
    public void testConstraintOneTaskForDay() throws Exception {
        MvcResult result = mvc.perform(get("/tasks").contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<TaskDto> tasks = mapper.readValue(result.getResponse().getContentAsString(), mapper.getTypeFactory().constructCollectionType(List.class, TaskDto.class));
       AtomicBoolean violation = new AtomicBoolean(false);
        tasks.stream().filter(t-> {
            try {
                return anotherTaskInSamePeriod(t.getEmployeeId(),LocalDate.parse(t.getExpectedStartTime()), LocalDate.parse(t.getExpectedEndTime()), t.getId());
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        }).findFirst().ifPresent(p -> violation.set(true));
        Assert.assertFalse(violation.get());
    }

    private boolean anotherTaskInSamePeriod(int employee_id, LocalDate start, LocalDate end, int task_id) throws Exception {
        MvcResult result = mvc.perform(get("/tasks/tasksByEmployee/"+employee_id+"/").contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<TaskDto> tasksByEmployee = mapper.readValue(result.getResponse().getContentAsString(), mapper.getTypeFactory().constructCollectionType(List.class, TaskDto.class));
        List<TaskDto> tasksInPeriod = tasksByEmployee.parallelStream().filter(t -> (EmployeeBusinessService.betweenTwoDate(start, LocalDate.parse(t.getExpectedStartTime()), LocalDate.parse(t.getExpectedEndTime()))
                || EmployeeBusinessService.betweenTwoDate(end, LocalDate.parse(t.getExpectedStartTime()), LocalDate.parse(t.getExpectedEndTime()))
                || EmployeeBusinessService.betweenTwoDate(LocalDate.parse(t.getExpectedStartTime()), start, end)
                || EmployeeBusinessService.betweenTwoDate(LocalDate.parse(t.getExpectedEndTime()), start, end))
                && t.getId()!=task_id)
                .collect(Collectors.toList());

        return tasksInPeriod.size()>0;
    }


}
