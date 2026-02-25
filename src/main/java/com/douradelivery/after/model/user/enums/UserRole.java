package com.douradelivery.after.model.user.enums;

public enum UserRole {
    ADMIN,
    CLIENT,
    DELIVERYMAN;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
