package com.dependencyguard.repository;

import com.dependencyguard.model.*;
import java.util.List;
import java.util.Map;

public interface GraphRepository {
    boolean isConnected();
    void initializeAndSeed();
    GraphData getFullGraph();
    List<VulnerabilityFinding> getVulnerabilityFindings();
    VulnerabilityFinding getVulnerabilityBlastRadius(String cveOrPkgName);
    List<LicenseViolation> getLicenseViolations();
    List<Map<String, Object>> getSharedDependencies();
    List<DependencyPath> findPathsBetween(String appName, String targetPkgName);
    boolean applyRemediation(String currentPkgId, String patchedPkgId);
    boolean rollbackRemediation(String currentPkgId, String patchedPkgId);
    int getNodeCount();
    int getEdgeCount();
    GraphStatus getStatus();
}
