package com.dependencyguard.model;

import java.util.ArrayList;
import java.util.List;

public class RemediationPlan {
    private String id;
    private String findingId;
    private String packageName;
    private String currentVersion;
    private String targetVersion;
    private List<String> affectedApplications = new ArrayList<>();
    private String reason;
    private String breakingChangeRisk;
    private List<String> verificationSteps = new ArrayList<>();
    private String status = "PROPOSED"; // PROPOSED, APPLIED, ROLLED_BACK
    private String timestamp;
    private boolean rollbackAvailable;
    private String graphMutationSummary;

    public RemediationPlan() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFindingId() { return findingId; }
    public void setFindingId(String findingId) { this.findingId = findingId; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getCurrentVersion() { return currentVersion; }
    public void setCurrentVersion(String currentVersion) { this.currentVersion = currentVersion; }

    public String getTargetVersion() { return targetVersion; }
    public void setTargetVersion(String targetVersion) { this.targetVersion = targetVersion; }

    public List<String> getAffectedApplications() { return affectedApplications; }
    public void setAffectedApplications(List<String> affectedApplications) { this.affectedApplications = affectedApplications; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getBreakingChangeRisk() { return breakingChangeRisk; }
    public void setBreakingChangeRisk(String breakingChangeRisk) { this.breakingChangeRisk = breakingChangeRisk; }

    public List<String> getVerificationSteps() { return verificationSteps; }
    public void setVerificationSteps(List<String> verificationSteps) { this.verificationSteps = verificationSteps; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public boolean isRollbackAvailable() { return rollbackAvailable; }
    public void setRollbackAvailable(boolean rollbackAvailable) { this.rollbackAvailable = rollbackAvailable; }

    public String getGraphMutationSummary() { return graphMutationSummary; }
    public void setGraphMutationSummary(String graphMutationSummary) { this.graphMutationSummary = graphMutationSummary; }
}
