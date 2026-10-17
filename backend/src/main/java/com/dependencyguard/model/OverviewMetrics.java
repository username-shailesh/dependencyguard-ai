package com.dependencyguard.model;

public class OverviewMetrics {
    private int totalApplications;
    private int totalPackages;
    private int totalVulnerabilities;
    private int criticalVulnerabilities;
    private int highRiskPaths;
    private int licenseViolations;
    private double averageRiskScore;
    private boolean falkorDbConnected;
    private String dataSource;

    public OverviewMetrics() {}

    public int getTotalApplications() { return totalApplications; }
    public void setTotalApplications(int totalApplications) { this.totalApplications = totalApplications; }

    public int getTotalPackages() { return totalPackages; }
    public void setTotalPackages(int totalPackages) { this.totalPackages = totalPackages; }

    public int getTotalVulnerabilities() { return totalVulnerabilities; }
    public void setTotalVulnerabilities(int totalVulnerabilities) { this.totalVulnerabilities = totalVulnerabilities; }

    public int getCriticalVulnerabilities() { return criticalVulnerabilities; }
    public void setCriticalVulnerabilities(int criticalVulnerabilities) { this.criticalVulnerabilities = criticalVulnerabilities; }

    public int getHighRiskPaths() { return highRiskPaths; }
    public void setHighRiskPaths(int highRiskPaths) { this.highRiskPaths = highRiskPaths; }

    public int getLicenseViolations() { return licenseViolations; }
    public void setLicenseViolations(int licenseViolations) { this.licenseViolations = licenseViolations; }

    public double getAverageRiskScore() { return averageRiskScore; }
    public void setAverageRiskScore(double averageRiskScore) { this.averageRiskScore = averageRiskScore; }

    public boolean isFalkorDbConnected() { return falkorDbConnected; }
    public void setFalkorDbConnected(boolean falkorDbConnected) { this.falkorDbConnected = falkorDbConnected; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
}
