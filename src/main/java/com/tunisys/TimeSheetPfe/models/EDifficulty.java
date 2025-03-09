package com.tunisys.TimeSheetPfe.models;

public enum EDifficulty {
    EASY,
    MEDIUM,
    HARD;

    public static EDifficulty fromString(String value) {
        for (EDifficulty difficulty : EDifficulty.values()) {
            if (difficulty.name().equalsIgnoreCase(value)) {
                return difficulty;
            }
        }
        throw new IllegalArgumentException("Invalid EDifficulty value: " + value);
    }
}
