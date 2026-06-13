package com.blps_lab1.demo.config;

import com.blps_lab1.demo.scheduler.DeleteExpiredDraftsJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail expiredDraftsJobDetail() {
        return JobBuilder.newJob(DeleteExpiredDraftsJob.class)
                .withIdentity("deleteExpiredDraftsJob")
                .storeDurably()
                .requestRecovery()
                .build();
    }

    @Bean
    public Trigger expiredDraftsJobTrigger(JobDetail expiredDraftsJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(expiredDraftsJobDetail)
                .withIdentity("expiredDraftsTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 */10 * * * ?"))
                .build();
    }
}