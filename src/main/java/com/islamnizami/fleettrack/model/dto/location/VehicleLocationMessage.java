package com.islamnizami.fleettrack.model.dto.location;

public record VehicleLocationMessage(
        Long vehicleId,
        String licensePlate,
        Double latitude,
        Double longitude,
        String status,
        String timestamp
) {
}
