package com.islamnizami.fleettrack.scheduler;

import com.islamnizami.fleettrack.repository.MaintenanceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanupJob {

    private final MaintenanceRecordRepository repository;

    @Value("${app.cleanup.completed-maintenance-retention-days:90}")
    private int retentionDays;

    @Scheduled(cron = "${app.cleanup.cron:0 30 2 * * *}")
    @Transactional
    public void cleanOldMaintenanceRecords() {
        OffsetDateTime cutoff = OffsetDateTime.now().minusDays(retentionDays);
        int deleted = repository.deleteCompletedBefore(cutoff);
        log.info("Cleanup job removed {} completed maintenance records older than {}",
                deleted, cutoff);
    }
}
