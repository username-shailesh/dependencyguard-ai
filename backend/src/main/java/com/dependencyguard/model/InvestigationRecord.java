package com.dependencyguard.model;

import java.util.ArrayList;
import java.util.List;

public class InvestigationRecord {
    private String id;
    private String timestamp;
    private String userQuery;
    private String intent;
    private List<String> entityIds = new ArrayList<>();
    private int findingsCount;
    private String summary;
    private List<String> groundedEvidence = new ArrayList<>();
    private double riskScore;
    private boolean activeInGraph = true;

    public InvestigationRecord() {}

    public InvestigationRecord(String id, String timestamp, String userQuery, String intent, List<String> entityIds,
                               int findingsCount, String summary, List<String> groundedEvidence, double riskScore, boolean activeInGraph) {
        this.id = id;
        this.timestamp = timestamp;
        this.userQuery = userQuery;
        this.intent = intent;
        this.entityIds = entityIds != null ? entityIds : new ArrayList<>();
        this.findingsCount = findingsCount;
        this.summary = summary;
        this.groundedEvidence = groundedEvidence != null ? groundedEvidence : new ArrayList<>();
        this.riskScore = riskScore;
        this.activeInGraph = activeInGraph;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getUserQuery() { return userQuery; }
    public void setUserQuery(String userQuery) { this.userQuery = userQuery; }

    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }

    public List<String> getEntityIds() { return entityIds; }
    public void setEntityIds(List<String> entityIds) { this.entityIds = entityIds; }

    public int getFindingsCount() { return findingsCount; }
    public void setFindingsCount(int findingsCount) { this.findingsCount = findingsCount; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getGroundedEvidence() { return groundedEvidence; }
    public void setGroundedEvidence(List<String> groundedEvidence) { this.groundedEvidence = groundedEvidence; }

    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }

    public boolean isActiveInGraph() { return activeInGraph; }
    public void setActiveInGraph(boolean activeInGraph) { this.activeInGraph = activeInGraph; }
}
