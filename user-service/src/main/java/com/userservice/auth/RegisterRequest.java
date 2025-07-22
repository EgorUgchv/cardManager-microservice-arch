package com.userservice.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequest {
    private String userId;
    @NotBlank
    private String firstName;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank
    private String password;
}
