package com.islamnizami.fleettrack.model.dto.vehicle;

import com.islamnizami.fleettrack.model.enums.VehicleStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class VehicleResponse {

    private Long id;
    private String make;
    private String model;
    private Integer modelYear;
    private String licensePlate;
    private VehicleStatus status;
    private Double latitude;
    private Double longitude;
    private OffsetDateTime locationUpdatedAt;
    private Long driverId;
    private String driverName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
