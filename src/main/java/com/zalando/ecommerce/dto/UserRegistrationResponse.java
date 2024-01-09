package com.zalando.ecommerce.dto;

import com.zalando.ecommerce.model.Role;
import lombok.*;

@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class UserRegistrationResponse {
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
