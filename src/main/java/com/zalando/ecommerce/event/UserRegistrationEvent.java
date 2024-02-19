package com.zalando.ecommerce.event;

import com.zalando.ecommerce.model.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegistrationEvent extends ApplicationEvent {
    private final User user;
    public UserRegistrationEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
