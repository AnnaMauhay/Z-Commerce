package com.zalando.ecommerce.dto;

import com.zalando.ecommerce.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class UserRegistrationRequest {
    @NotNull(message = "First name must not be null.")
    @NotEmpty(message = "First name must not be empty.")
    private String firstName;

    @NotNull (message = "Last name must not be null.")
    @NotEmpty(message = "Last name must not be empty.")
    private String lastName;

    @Email
    @NotNull (message = "Email must not be null.")
    @NotEmpty(message = "Email must not be empty.")
    private String email;

    @NotNull (message = "Password must not be null.")
    @NotEmpty(message = "Password must not be empty.")
    private String password;

    @NotNull(message = "Role must not be null.")
    private Role role;
}
