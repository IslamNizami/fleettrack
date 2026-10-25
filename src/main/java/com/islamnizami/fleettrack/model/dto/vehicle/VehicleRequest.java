package com.islamnizami.fleettrack.model.dto.vehicle;

import com.islamnizami.fleettrack.model.enums.VehicleStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleRequest {

    @NotBlank(message = "make can't be empty.")
    @Size(max = 100)
    private String make;

    @NotBlank(message = "model cant be empty.")
    @Size(max = 100)
    private String model;

    @NotNull(message = "Year of model cant be null.")
    @Min(1886)
    @Max(2100)
    private Integer modelYear;

    @NotBlank(message = "License Plate can't be empty.")
    @Size(max = 20)
    private String licensePlate;

    private VehicleStatus status;
}
