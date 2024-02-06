package com.zalando.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
@Component
@Getter
public class CustomUserDetails implements UserDetails {
    private final User user;
    @Autowired
    public CustomUserDetails(User user){
        this.user = user;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRole().getGrantedAuthorities();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !user.isArchived();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isArchived();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !user.isArchived();
    }

    @Override
    public boolean isEnabled() {
        return !user.isArchived();
    }
}
