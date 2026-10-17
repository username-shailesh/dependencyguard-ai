package com.dependencyguard.model;

public class RiskScoreBreakdown {
    private double baseSeverityScore;
    private int affectedAppsCount;
    private double tierWeightedMultiplier;
    private double exposureDepthFactor;
    private double remediationAvailabilityFactor;
    private double finalScore;
    private String formulaExplanation;

    public RiskScoreBreakdown() {}

    public RiskScoreBreakdown(double baseSeverityScore, int affectedAppsCount, double tierWeightedMultiplier,
                              double exposureDepthFactor, double remediationAvailabilityFactor,
                              double finalScore, String formulaExplanation) {
        this.baseSeverityScore = baseSeverityScore;
        this.affectedAppsCount = affectedAppsCount;
        this.tierWeightedMultiplier = tierWeightedMultiplier;
        this.exposureDepthFactor = exposureDepthFactor;
        this.remediationAvailabilityFactor = remediationAvailabilityFactor;
        this.finalScore = finalScore;
        this.formulaExplanation = formulaExplanation;
    }

    public double getBaseSeverityScore() { return baseSeverityScore; }
    public void setBaseSeverityScore(double baseSeverityScore) { this.baseSeverityScore = baseSeverityScore; }

    public int getAffectedAppsCount() { return affectedAppsCount; }
    public void setAffectedAppsCount(int affectedAppsCount) { this.affectedAppsCount = affectedAppsCount; }

    public double getTierWeightedMultiplier() { return tierWeightedMultiplier; }
    public void setTierWeightedMultiplier(double tierWeightedMultiplier) { this.tierWeightedMultiplier = tierWeightedMultiplier; }

    public double getExposureDepthFactor() { return exposureDepthFactor; }
    public void setExposureDepthFactor(double exposureDepthFactor) { this.exposureDepthFactor = exposureDepthFactor; }

    public double getRemediationAvailabilityFactor() { return remediationAvailabilityFactor; }
    public void setRemediationAvailabilityFactor(double remediationAvailabilityFactor) { this.remediationAvailabilityFactor = remediationAvailabilityFactor; }

    public double getFinalScore() { return finalScore; }
    public void setFinalScore(double finalScore) { this.finalScore = finalScore; }

    public String getFormulaExplanation() { return formulaExplanation; }
    public void setFormulaExplanation(String formulaExplanation) { this.formulaExplanation = formulaExplanation; }
}
