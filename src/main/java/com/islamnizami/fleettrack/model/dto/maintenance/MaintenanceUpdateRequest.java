package com.islamnizami.fleettrack.model.dto.maintenance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class MaintenanceUpdateRequest {

    @NotBlank(message = "description can't be empty.")
    @Size(max = 500)
    private String description;

    @NotNull(message = "dueAt can't be null.")
    private OffsetDateTime dueAt;
}
