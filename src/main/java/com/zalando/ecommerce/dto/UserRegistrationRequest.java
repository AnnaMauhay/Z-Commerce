package com.zalando.ecommerce.dto;

import com.zalando.ecommerce.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class UserRegistrationRequest {
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @Email @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private Role role;
}
