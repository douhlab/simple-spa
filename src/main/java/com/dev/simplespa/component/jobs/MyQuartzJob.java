package com.dev.simplespa.component.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
public class MyQuartzJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Quartz job executed at " + java.time.LocalDateTime.now());
        // Here you can call your DAO, send emails, etc.
    }
}
