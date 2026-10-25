package com.islamnizami.fleettrack.scheduler;

import com.islamnizami.fleettrack.model.entity.MaintenanceRecord;
import com.islamnizami.fleettrack.model.enums.NotificationType;
import com.islamnizami.fleettrack.notification.NotificationPublisher;
import com.islamnizami.fleettrack.repository.MaintenanceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class MaintenanceReminderJob {

    private final MaintenanceRecordRepository repository;
    private final NotificationPublisher publisher;

    @Value("${app.maintenance.reminder-days:3}")
    private int reminderDays;

    @Scheduled(cron = "${app.maintenance.reminder-cron:0 0 8 * * *}")
    @Transactional
    public void sendReminders() {
        OffsetDateTime now = OffsetDateTime.now();

        List<MaintenanceRecord> dueSoon =
                repository.findDueSoonNotNotified(now, now.plusDays(reminderDays));
        for (MaintenanceRecord record : dueSoon) {
            publisher.publish(NotificationType.MAINTENANCE_DUE,
                    "Maintenance due soon for " + record.getVehicle().getLicensePlate()
                            + ": " + record.getDescription() + " (due " + record.getDueAt() + ")",
                    record.getVehicle().getId());
            record.setDueSoonNotifiedAt(now);
        }

        List<MaintenanceRecord> overdue = repository.findOverdueNotNotified(now);
        for (MaintenanceRecord record : overdue) {
            publisher.publish(NotificationType.MAINTENANCE_OVERDUE,
                    "Maintenance OVERDUE for " + record.getVehicle().getLicensePlate()
                            + ": " + record.getDescription() + " (was due " + record.getDueAt() + ")",
                    record.getVehicle().getId());
            record.setOverdueNotifiedAt(now);
            if (record.getDueSoonNotifiedAt() == null) {
                record.setDueSoonNotifiedAt(now);
            }
        }

        log.info("Maintenance reminder job finished: {} due soon, {} overdue",
                dueSoon.size(), overdue.size());
    }
}
