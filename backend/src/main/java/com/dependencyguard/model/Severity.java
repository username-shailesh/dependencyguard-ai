package com.dependencyguard.model;

public enum Severity {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW;

    public double getBaseScore() {
        return switch (this) {
            case CRITICAL -> 10.0;
            case HIGH -> 7.5;
            case MEDIUM -> 5.0;
            case LOW -> 2.5;
        };
    }
}
