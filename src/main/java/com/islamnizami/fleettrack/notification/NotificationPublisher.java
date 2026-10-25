package com.islamnizami.fleettrack.notification;

import com.islamnizami.fleettrack.model.dto.notification.Notification;
import com.islamnizami.fleettrack.model.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private static final int MAX_RECENT = 100;

    private final StringRedisTemplate redisTemplate;
    private final JsonMapper jsonMapper;

    @Value("${app.notifications.channel:fleet.notifications}")
    private String channel;

    @Value("${app.notifications.recent-key:fleet:notifications:recent}")
    private String recentKey;

    @Async
    public void publish(NotificationType type, String message, Long vehicleId) {
        try {
            Notification notification = new Notification(
                    type, message, vehicleId, OffsetDateTime.now().toString());
            String json = jsonMapper.writeValueAsString(notification);

            redisTemplate.opsForList().leftPush(recentKey, json);
            redisTemplate.opsForList().trim(recentKey, 0, MAX_RECENT - 1);
            redisTemplate.convertAndSend(channel, json);
        } catch (RuntimeException ex) {
            log.warn("Could not publish notification {}: {}", type, ex.getMessage());
        }
    }

    public List<Notification> recent(int limit) {
        int size = Math.max(1, Math.min(limit, MAX_RECENT));
        List<String> raw = redisTemplate.opsForList().range(recentKey, 0, size - 1);
        List<Notification> result = new ArrayList<>();
        if (raw == null) {
            return result;
        }
        for (String json : raw) {
            try {
                result.add(jsonMapper.readValue(json, Notification.class));
            } catch (RuntimeException ex) {
                log.warn("Skipping unreadable notification: {}", ex.getMessage());
            }
        }
        return result;
    }
}
