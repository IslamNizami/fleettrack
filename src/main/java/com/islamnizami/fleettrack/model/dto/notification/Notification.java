package com.islamnizami.fleettrack.model.dto.notification;

import com.islamnizami.fleettrack.model.enums.NotificationType;

public record Notification(
        NotificationType type,
        String message,
        Long vehicleId,
        String createdAt
) {
}
