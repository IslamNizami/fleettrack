package com.islamnizami.fleettrack.model.dto.location;

/** Payload broadcast over WebSocket (timestamp is an ISO-8601 string). */
public record VehicleLocationMessage(
        Long vehicleId,
        String licensePlate,
        Double latitude,
        Double longitude,
        String status,
        String timestamp
) {
}
