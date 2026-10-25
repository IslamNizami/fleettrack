package com.islamnizami.fleettrack.service;

import com.islamnizami.fleettrack.model.dto.summary.FleetSummaryResponse;
import com.islamnizami.fleettrack.model.enums.VehicleStatus;
import com.islamnizami.fleettrack.repository.DriverRepository;
import com.islamnizami.fleettrack.repository.MaintenanceRecordRepository;
import com.islamnizami.fleettrack.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FleetSummaryService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final MaintenanceRecordRepository maintenanceRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "fleet-summary", key = "'current'")
    public FleetSummaryResponse getSummary() {
        Map<VehicleStatus, Long> byStatus = new EnumMap<>(VehicleStatus.class);
        for (VehicleStatus status : VehicleStatus.values()) {
            byStatus.put(status, vehicleRepository.countByStatus(status));
        }

        return new FleetSummaryResponse(
                vehicleRepository.count(),
                byStatus,
                vehicleRepository.countByDriverIsNull(),
                driverRepository.count(),
                driverRepository.countByActive(true),
                maintenanceRepository.countByCompletedFalse(),
                maintenanceRepository.countByCompletedFalseAndDueAtBefore(OffsetDateTime.now()));
    }
}
