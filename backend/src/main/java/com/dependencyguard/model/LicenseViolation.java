package com.dependencyguard.model;

public class LicenseViolation {
    private String id;
    private String appId;
    private String appName;
    private String environment;
    private String packageName;
    private String packageVersion;
    private String spdxId;
    private String riskLevel;
    private String category;
    private boolean commercialUseAllowed;
    private String pathDescription;
    private String policyRule;
    private String recommendation;
    private int depth;

    public LicenseViolation() {}

    public LicenseViolation(String id, String appId, String appName, String environment, String packageName,
                            String packageVersion, String spdxId, String riskLevel, String category,
                            boolean commercialUseAllowed, String pathDescription, String policyRule,
                            String recommendation, int depth) {
        this.id = id;
        this.appId = appId;
        this.appName = appName;
        this.environment = environment;
        this.packageName = packageName;
        this.packageVersion = packageVersion;
        this.spdxId = spdxId;
        this.riskLevel = riskLevel;
        this.category = category;
        this.commercialUseAllowed = commercialUseAllowed;
        this.pathDescription = pathDescription;
        this.policyRule = policyRule;
        this.recommendation = recommendation;
        this.depth = depth;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getPackageVersion() { return packageVersion; }
    public void setPackageVersion(String packageVersion) { this.packageVersion = packageVersion; }

    public String getSpdxId() { return spdxId; }
    public void setSpdxId(String spdxId) { this.spdxId = spdxId; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isCommercialUseAllowed() { return commercialUseAllowed; }
    public void setCommercialUseAllowed(boolean commercialUseAllowed) { this.commercialUseAllowed = commercialUseAllowed; }

    public String getPathDescription() { return pathDescription; }
    public void setPathDescription(String pathDescription) { this.pathDescription = pathDescription; }

    public String getPolicyRule() { return policyRule; }
    public void setPolicyRule(String policyRule) { this.policyRule = policyRule; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public int getDepth() { return depth; }
    public void setDepth(int depth) { this.depth = depth; }
}
