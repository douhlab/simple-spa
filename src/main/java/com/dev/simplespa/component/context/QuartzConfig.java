package com.dev.simplespa.component.context;

import jakarta.validation.constraints.NotBlank;

import com.dev.simplespa.component.jobs.MyQuartzJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@EnableConfigurationProperties(QuartzConfig.QuartzConfiguration.class)
@RequiredArgsConstructor
public class QuartzConfig {

    private final QuartzConfiguration quartzConfiguration;

    @Bean
    public JobDetail myJobDetail() {
        return JobBuilder.newJob(MyQuartzJob.class)
                .withIdentity("myJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger myJobTrigger() {

        return TriggerBuilder.newTrigger()
                .forJob(myJobDetail())
                .withIdentity("myTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(quartzConfiguration.myQuartzJob()))
                .build();
    }

    @ConfigurationProperties("spa.quartz.job.cron-schedule")
    @Validated
    public record QuartzConfiguration(@NotBlank String myQuartzJob) {
    }
}
