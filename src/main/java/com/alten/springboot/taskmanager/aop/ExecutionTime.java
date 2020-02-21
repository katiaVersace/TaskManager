package com.alten.springboot.taskmanager.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTime {

    @Around("execution(* com.alten.springboot.taskmanager.controller.ITeamController.assignTaskToTeam(..))")
    public Object calculateExecutionTime(ProceedingJoinPoint theProcJp) throws Throwable{

        long begin = System.currentTimeMillis();
        Object result = theProcJp.proceed();
        long end =System.currentTimeMillis();
        long duration = end - begin;
        System.out.println("Duration: "+ duration*0.001+ " seconds");
        return result;
    }
}
