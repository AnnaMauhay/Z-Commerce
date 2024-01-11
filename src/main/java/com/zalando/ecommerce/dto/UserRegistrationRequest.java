package com.zalando.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zalando.ecommerce.model.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;
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
