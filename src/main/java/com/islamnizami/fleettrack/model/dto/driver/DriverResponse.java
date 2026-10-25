package com.islamnizami.fleettrack.model.dto.driver;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
public class DriverResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String licenseNumber;
    private boolean active;
    private OffsetDateTime createdAt;
}
