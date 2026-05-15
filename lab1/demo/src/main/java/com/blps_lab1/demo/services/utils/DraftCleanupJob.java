package com.blps_lab1.demo.services.utils;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@EnableScheduling
public class DraftCleanupJob {

    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void cleanupExpiredDrafts() {
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(30);

    }
}
