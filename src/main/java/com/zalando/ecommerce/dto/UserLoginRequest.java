package com.zalando.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class UserLoginRequest {
    @Email
    @NotNull (message = "Email must not be null.")
    @NotEmpty(message = "Email must not be empty.")
    private String email;

    @NotNull (message = "Password must not be null.")
    @NotEmpty(message = "Password must not be empty.")
    private String password;
}
