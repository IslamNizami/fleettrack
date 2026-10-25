package com.islamnizami.fleettrack.model.dto.Login;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Username can't be blank.")
    private String username;

    @NotBlank(message = "password can't be blank.")
    private String password;
}
