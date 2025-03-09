package com.tunisys.TimeSheetPfe.models;

public enum EPriority {
    LOW,
    MEDIUM,
    HIGH;
    public static EPriority fromString(String value) {
        for (EPriority priority : EPriority.values()) {
            if (priority.name().equalsIgnoreCase(value)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid EPriority value: " + value);
    }
}
