package com.zalando.ecommerce.service;

import com.zalando.ecommerce.exception.InvalidEmailVerificationTokenException;
import com.zalando.ecommerce.model.EmailVerificationToken;
import com.zalando.ecommerce.model.User;
import com.zalando.ecommerce.repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailTokenService {
    private final EmailVerificationTokenRepository tokenRepository;
    private final UserService userService;
    public User validateToken(String token) throws InvalidEmailVerificationTokenException {
        Optional<EmailVerificationToken> verificationToken = tokenRepository.getEmailVerificationTokenByToken(token);
        if(verificationToken.isPresent()){
            if(verificationToken.get().getExpiryDate().isBefore(LocalDateTime.now())){
                throw new InvalidEmailVerificationTokenException("The token provided is already expired. Registration entry will be removed.");
            }
            return userService.verifyUser(verificationToken.get().getUser());
        }else {
            throw new InvalidEmailVerificationTokenException("The provided token did not match any token in the system.");
        }
    }

    public void save(EmailVerificationToken verificationToken){
        tokenRepository.save(verificationToken);
    }
}
