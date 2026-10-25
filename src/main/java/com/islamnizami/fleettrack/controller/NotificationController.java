package com.islamnizami.fleettrack.controller;

import com.islamnizami.fleettrack.model.dto.notification.Notification;
import com.islamnizami.fleettrack.notification.NotificationPublisher;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationPublisher publisher;

    @GetMapping
    public List<Notification> recent(@RequestParam(defaultValue = "20") int limit) {
        return publisher.recent(limit);
    }
}
