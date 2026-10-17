package com.dependencyguard.model;

public class AffectedAppInfo {
    private String appId;
    private String appName;
    private String environment;
    private CriticalTier criticalTier;
    private int depth;
    private boolean direct;
    private String pathString;

    public AffectedAppInfo() {}

    public AffectedAppInfo(String appId, String appName, String environment, CriticalTier criticalTier, int depth, boolean direct, String pathString) {
        this.appId = appId;
        this.appName = appName;
        this.environment = environment;
        this.criticalTier = criticalTier;
        this.depth = depth;
        this.direct = direct;
        this.pathString = pathString;
    }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public CriticalTier getCriticalTier() { return criticalTier; }
    public void setCriticalTier(CriticalTier criticalTier) { this.criticalTier = criticalTier; }

    public int getDepth() { return depth; }
    public void setDepth(int depth) { this.depth = depth; }

    public boolean isDirect() { return direct; }
    public void setDirect(boolean direct) { this.direct = direct; }

    public String getPathString() { return pathString; }
    public void setPathString(String pathString) { this.pathString = pathString; }
}
