package com.dependencyguard.model;

import java.util.ArrayList;
import java.util.List;

public class AgentResponse {
    private String query;
    private String intent;
    private String answer;
    private boolean grounded = true;
    private List<DependencyPath> evidencePaths = new ArrayList<>();
    private List<VulnerabilityFinding> findings = new ArrayList<>();
    private RemediationPlan suggestedRemediation;
    private String cypherQueryUsed;
    private long executionTimeMs;
    private String dataSource;
    private boolean connectedToFalkorDb;

    public AgentResponse() {}

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public boolean isGrounded() { return grounded; }
    public void setGrounded(boolean grounded) { this.grounded = grounded; }

    public List<DependencyPath> getEvidencePaths() { return evidencePaths; }
    public void setEvidencePaths(List<DependencyPath> evidencePaths) { this.evidencePaths = evidencePaths; }

    public List<VulnerabilityFinding> getFindings() { return findings; }
    public void setFindings(List<VulnerabilityFinding> findings) { this.findings = findings; }

    public RemediationPlan getSuggestedRemediation() { return suggestedRemediation; }
    public void setSuggestedRemediation(RemediationPlan suggestedRemediation) { this.suggestedRemediation = suggestedRemediation; }

    public String getCypherQueryUsed() { return cypherQueryUsed; }
    public void setCypherQueryUsed(String cypherQueryUsed) { this.cypherQueryUsed = cypherQueryUsed; }

    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public boolean isConnectedToFalkorDb() { return connectedToFalkorDb; }
    public void setConnectedToFalkorDb(boolean connectedToFalkorDb) { this.connectedToFalkorDb = connectedToFalkorDb; }
}
