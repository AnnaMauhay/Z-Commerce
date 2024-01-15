package com.zalando.ecommerce.model;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;
@Getter
public enum Role {
    CUSTOMER(Set.of(UserPermissions.READ_PRODUCT)),
    SELLER(Set.of(UserPermissions.READ_PRODUCT, UserPermissions.WRITE_PRODUCT)),
    ADMIN(Set.of(UserPermissions.READ_PRODUCT, UserPermissions.WRITE_PRODUCT));

    private final Set<UserPermissions> userPermissions;
    Role(Set<UserPermissions> userPermissions) {
        this.userPermissions = userPermissions;
    }
    public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
        Set<SimpleGrantedAuthority> permissions = getUserPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_"+this.name()));
        return permissions;
    }
}
