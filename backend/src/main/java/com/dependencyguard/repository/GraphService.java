package com.dependencyguard.repository;

import com.dependencyguard.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GraphService implements GraphRepository {
    private static final Logger log = LoggerFactory.getLogger(GraphService.class);

    private final FalkorDbGraphRepository falkorDbRepo;
    private final InMemoryGraphEngine fallbackEngine;

    public GraphService(FalkorDbGraphRepository falkorDbRepo, InMemoryGraphEngine fallbackEngine) {
        this.falkorDbRepo = falkorDbRepo;
        this.fallbackEngine = fallbackEngine;
    }

    private GraphRepository active() {
        if (falkorDbRepo.isConnected()) {
            return falkorDbRepo;
        }
        return fallbackEngine;
    }

    public boolean isConnectedToFalkorDb() {
        return falkorDbRepo.isConnected();
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void initializeAndSeed() {
        if (falkorDbRepo.isConnected()) {
            falkorDbRepo.initializeAndSeed();
        }
        fallbackEngine.initializeAndSeed();
    }

    @Override
    public GraphData getFullGraph() {
        return active().getFullGraph();
    }

    @Override
    public List<VulnerabilityFinding> getVulnerabilityFindings() {
        return active().getVulnerabilityFindings();
    }

    @Override
    public VulnerabilityFinding getVulnerabilityBlastRadius(String cveOrPkgName) {
        return active().getVulnerabilityBlastRadius(cveOrPkgName);
    }

    @Override
    public List<LicenseViolation> getLicenseViolations() {
        return active().getLicenseViolations();
    }

    @Override
    public List<Map<String, Object>> getSharedDependencies() {
        return active().getSharedDependencies();
    }

    @Override
    public List<DependencyPath> findPathsBetween(String appName, String targetPkgName) {
        return active().findPathsBetween(appName, targetPkgName);
    }

    @Override
    public boolean applyRemediation(String currentPkgId, String patchedPkgId) {
        boolean success = active().applyRemediation(currentPkgId, patchedPkgId);
        if (falkorDbRepo.isConnected() && active() != fallbackEngine) {
            fallbackEngine.applyRemediation(currentPkgId, patchedPkgId);
        }
        return success;
    }

    @Override
    public boolean rollbackRemediation(String currentPkgId, String patchedPkgId) {
        boolean success = active().rollbackRemediation(currentPkgId, patchedPkgId);
        if (falkorDbRepo.isConnected() && active() != fallbackEngine) {
            fallbackEngine.rollbackRemediation(currentPkgId, patchedPkgId);
        }
        return success;
    }

    @Override
    public int getNodeCount() {
        return active().getNodeCount();
    }

    @Override
    public int getEdgeCount() {
        return active().getEdgeCount();
    }

    @Override
    public GraphStatus getStatus() {
        if (falkorDbRepo.isConnected()) {
            return falkorDbRepo.getStatus();
        }
        return fallbackEngine.getStatus();
    }

    public OverviewMetrics getOverviewMetrics() {
        OverviewMetrics m = new OverviewMetrics();
        List<VulnerabilityFinding> findings = getVulnerabilityFindings();
        List<LicenseViolation> violations = getLicenseViolations();
        GraphData gd = getFullGraph();

        int apps = 0;
        int pkgs = 0;
        for (GraphNode gn : gd.getNodes()) {
            if (gn.getType() == NodeType.APPLICATION) apps++;
            else if (gn.getType() == NodeType.PACKAGE) pkgs++;
        }

        int critCount = 0;
        double sumScore = 0.0;
        int highRiskPaths = 0;
        for (VulnerabilityFinding f : findings) {
            if (f.getSeverity() == Severity.CRITICAL) critCount++;
            sumScore += f.getBlastRadiusScore();
            if (f.getBlastRadiusScore() >= 25.0) {
                highRiskPaths += f.getPaths().size();
            }
        }

        m.setTotalApplications(apps);
        m.setTotalPackages(pkgs);
        m.setTotalVulnerabilities(findings.size());
        m.setCriticalVulnerabilities(critCount);
        m.setHighRiskPaths(highRiskPaths);
        m.setLicenseViolations(violations.size());
        m.setAverageRiskScore(findings.isEmpty() ? 0.0 : Math.round((sumScore / findings.size()) * 10.0) / 10.0);
        m.setFalkorDbConnected(falkorDbRepo.isConnected());
        m.setDataSource(falkorDbRepo.isConnected() ? "Live FalkorDB Graph Instance" : "Verified Synthetic Graph (FalkorDB Emulated Fallback)");
        return m;
    }

    public List<RemediationPlan> getRemediationHistory() {
        return fallbackEngine.getRemediationHistory();
    }
}
