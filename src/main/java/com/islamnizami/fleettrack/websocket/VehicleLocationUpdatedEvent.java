package com.islamnizami.fleettrack.websocket;

import com.islamnizami.fleettrack.model.dto.location.VehicleLocationMessage;

public record VehicleLocationUpdatedEvent(VehicleLocationMessage message) {
}
