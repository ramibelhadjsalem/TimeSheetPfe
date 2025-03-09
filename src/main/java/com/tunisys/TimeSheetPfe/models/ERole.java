package com.tunisys.TimeSheetPfe.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ERole {
    ROLE_EMPLOYEE,
    ROLE_MANAGER,
    ROLE_ADMIN;

    @JsonCreator
    public static ERole saveTypeforValue(String value) {
        return ERole.valueOf(value.toUpperCase());
    }
}
