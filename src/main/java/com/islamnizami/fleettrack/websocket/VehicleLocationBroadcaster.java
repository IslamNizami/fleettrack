package com.islamnizami.fleettrack.websocket;

import com.islamnizami.fleettrack.model.dto.location.VehicleLocationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.json.JsonMapper;


@Component
@RequiredArgsConstructor
public class VehicleLocationBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;
    private final JsonMapper jsonMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onLocationUpdated(VehicleLocationUpdatedEvent event) {
        VehicleLocationMessage message = event.message();
        String json = jsonMapper.writeValueAsString(message);

        messagingTemplate.convertAndSend("/topic/vehicles/locations", json);
        messagingTemplate.convertAndSend(
                "/topic/vehicles/" + message.vehicleId() + "/location", json);
    }
}
