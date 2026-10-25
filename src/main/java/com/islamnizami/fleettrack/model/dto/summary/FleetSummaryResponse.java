package com.islamnizami.fleettrack.model.dto.summary;

import com.islamnizami.fleettrack.model.enums.VehicleStatus;

import java.util.Map;

public record FleetSummaryResponse(
        long totalVehicles,
        Map<VehicleStatus, Long> vehiclesByStatus,
        long unassignedVehicles,
        long totalDrivers,
        long activeDrivers,
        long pendingMaintenance,
        long overdueMaintenance
) {
}
