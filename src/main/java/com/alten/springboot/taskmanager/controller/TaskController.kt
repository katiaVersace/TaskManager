package com.alten.springboot.taskmanager.controller

import com.alten.springboot.taskmanager.businessservice.ITaskBusinessService
import com.alten.springboot.taskmanager.dto.TaskDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestBody

@Component
open class TaskController(@Autowired private val taskService: ITaskBusinessService) : ITaskController {


    override fun getTasks(): List<TaskDto?>? {
        return taskService!!.findAll()
    }

    override fun getTask(taskId: String?): TaskDto? {
        return taskService!!.findById(taskId!!.toInt())
    }

    override fun addTask(theTask: TaskDto?): TaskDto? {
        theTask!!.id = 0 // cioè inserisco, perche provo ad aggiornare ma l'id 0 non esiste
        return taskService!!.save(theTask)
    }

    override fun updateTaskAdmin(@RequestBody theTask: TaskDto?): TaskDto? {
        taskService!!.update(theTask)
        return theTask
    }

    override fun updateTask(@RequestBody theTask: TaskDto?): TaskDto? {
        val oldTask = taskService!!.findById(theTask!!.id)
        if (theTask.realStartTime != null) {
            oldTask.realStartTime = theTask.realStartTime
        }
        if (theTask.realEndTime != null) {
            oldTask.realEndTime = theTask.realEndTime
        }
        taskService.update(oldTask)
        return oldTask
    }

    override fun deleteTask(taskId: String?): String? {
        taskService!!.delete(taskId!!.toInt())
        return "Deleted task with id: $taskId"
    }

    override fun getTasksByEmployeeId(employeeId: String?): List<TaskDto?>? {
        return taskService!!.findByEmployeeId(employeeId!!.toInt())
    }
}