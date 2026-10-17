package com.dependencyguard.model;

public enum CriticalTier {
    TIER_1(1.5),
    TIER_2(1.2),
    TIER_3(1.0);

    private final double multiplier;

    CriticalTier(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
