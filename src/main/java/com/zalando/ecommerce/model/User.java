package com.zalando.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Setter @Getter @NoArgsConstructor @AllArgsConstructor
@ToString @EqualsAndHashCode
public class User {

    @Id @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @JsonIgnore
    private String password;

    @Enumerated
    @Column(name = "role_id")
    private Role role;

    @Column(name = "is_verified", columnDefinition = "boolean default false")
    private boolean isVerified; //TODO: implement email verification

    @Column(name = "is_archived", columnDefinition = "boolean default false")
    private boolean isArchived;

}
