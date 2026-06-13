package com.blps_lab1.demo.scheduler;

import com.blps_lab1.demo.services.api.IReservationDraftService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class DeleteExpiredDraftsJob extends QuartzJobBean {

    private final IReservationDraftService reservationDraftService;

    public DeleteExpiredDraftsJob(IReservationDraftService reservationDraftService) {
        this.reservationDraftService = reservationDraftService;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            System.out.println(">>> [QUARTZ CLUSTER] Фоновый поток кластера "
                    + context.getScheduler().getSchedulerInstanceId()
                    + " начинает очистку просроченных черновиков.");
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

        reservationDraftService.deleteExpiredDrafts();
    }
}