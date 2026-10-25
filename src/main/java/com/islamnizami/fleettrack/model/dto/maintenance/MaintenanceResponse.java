package com.islamnizami.fleettrack.model.dto.maintenance;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class MaintenanceResponse {

    private Long id;
    private Long vehicleId;
    private String vehicleLicensePlate;
    private String description;
    private OffsetDateTime dueAt;
    private boolean completed;
    private OffsetDateTime completedAt;
    private boolean overdue;
    private OffsetDateTime createdAt;
}
