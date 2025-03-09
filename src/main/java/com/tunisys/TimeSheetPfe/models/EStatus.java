package com.tunisys.TimeSheetPfe.models;

public enum EStatus {
    NOT_STARTED,
    PROGRESS,
    FINISHED,
    IMPROVED,
    DISAPPROVED;

    public static EStatus fromString(String value) {
        for (EStatus status : EStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid EStatus value: " + value);
    }
}
