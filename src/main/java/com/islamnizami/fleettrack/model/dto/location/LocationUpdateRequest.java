package com.islamnizami.fleettrack.model.dto.location;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationUpdateRequest {

    @NotNull(message = "latitude can't be null.")
    @DecimalMin(value = "-90.0", message = "latitude must be between -90 and 90.")
    @DecimalMax(value = "90.0", message = "latitude must be between -90 and 90.")
    private Double latitude;

    @NotNull(message = "longitude can't be null.")
    @DecimalMin(value = "-180.0", message = "longitude must be between -180 and 180.")
    @DecimalMax(value = "180.0", message = "longitude must be between -180 and 180.")
    private Double longitude;
}
