package com.zalando.ecommerce.model;

import lombok.Getter;

@Getter
public enum UserPermissions {
    READ_PRODUCT("product:read"),
    WRITE_PRODUCT("product:write");

    private String permission;

    public void setPermission(String permission) {
        this.permission = permission;
    }

    UserPermissions(String permission) {
        this.permission = permission;
    }
}
