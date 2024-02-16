package com.zalando.ecommerce.repository;

import com.zalando.ecommerce.model.EmailVerificationToken;
import com.zalando.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Integer> {
    Optional<EmailVerificationToken> getEmailVerificationTokenByUser(User user);
    Optional<EmailVerificationToken> getEmailVerificationTokenByToken(String token);
}
