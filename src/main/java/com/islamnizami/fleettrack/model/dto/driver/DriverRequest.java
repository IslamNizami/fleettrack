
package com.islamnizami.fleettrack.model.dto.driver;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverRequest {

    @NotBlank(message = "First Name can't be blank.")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Last name can't be empty.")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "email can't be empty.")
    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 30)
    private String phone;

    @NotBlank(message = "License number can't empty.")
    @Size(max = 100)
    private String licenseNumber;

    /** Optional. When omitted on update the current value is kept; new drivers default to active. */
    private Boolean active;
}
