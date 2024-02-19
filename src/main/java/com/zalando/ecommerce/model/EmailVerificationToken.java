package com.zalando.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "email_verification_token")
public class EmailVerificationToken {
    @Id
    @Column(name = "token_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int tokenId;

    @Column(name = "token")
    private String token;

    @Column(name = "expiry_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime expiryDate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public EmailVerificationToken(String token, User user) {
        this.token = token;
        this.expiryDate = LocalDateTime.now().plusHours(2);
        this.user = user;
    }
}
