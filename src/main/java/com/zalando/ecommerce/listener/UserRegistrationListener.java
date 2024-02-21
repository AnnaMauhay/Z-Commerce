package com.zalando.ecommerce.listener;

import com.zalando.ecommerce.event.UserRegistrationEvent;
import com.zalando.ecommerce.model.EmailVerificationToken;
import com.zalando.ecommerce.model.User;
import com.zalando.ecommerce.service.EmailTokenService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class UserRegistrationListener implements ApplicationListener<UserRegistrationEvent> {
    private final EmailTokenService emailTokenService;
    private final Logger logger = LoggerFactory.getLogger(UserRegistrationListener.class);
    @Override
    public void onApplicationEvent(UserRegistrationEvent notification) {
        EmailVerificationToken verificationToken = emailTokenService.generateVerificationToken(notification.getUser());
        logger.info("User " + notification.getUser().getEmail() +
                " have registered. Waiting for email verification. "
        + verificationToken.getToken());
    }
}
