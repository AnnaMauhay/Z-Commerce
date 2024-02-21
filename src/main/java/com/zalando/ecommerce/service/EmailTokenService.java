package com.zalando.ecommerce.service;

import com.zalando.ecommerce.exception.InvalidEmailVerificationTokenException;
import com.zalando.ecommerce.model.EmailVerificationToken;
import com.zalando.ecommerce.model.User;
import com.zalando.ecommerce.repository.EmailVerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailTokenService {
    private final EmailVerificationTokenRepository tokenRepository;
    private final UserService userService;

    @Transactional
    public User validateToken(String token) throws InvalidEmailVerificationTokenException {
        Optional<EmailVerificationToken> verificationToken = tokenRepository.getEmailVerificationTokenByToken(token);
        if(verificationToken.isPresent()){
            if(verificationToken.get().getExpiryDate().isBefore(LocalDateTime.now())){
                throw new InvalidEmailVerificationTokenException("The token provided is already expired.");
            }
            tokenRepository.delete(verificationToken.get());
            return userService.verifyUser(verificationToken.get().getUser());
        }else {
            throw new InvalidEmailVerificationTokenException("The provided token did not match any token in the system.");
        }
    }
    public EmailVerificationToken generateVerificationToken(User user) {
        EmailVerificationToken verificationToken = new EmailVerificationToken(RandomStringUtils.random(10, true, true), user);
        tokenRepository.save(verificationToken);
        return verificationToken;
    }
}
