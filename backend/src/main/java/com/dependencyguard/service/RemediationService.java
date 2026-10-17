package com.dependencyguard.service;

import com.dependencyguard.model.AffectedAppInfo;
import com.dependencyguard.model.RemediationPlan;
import com.dependencyguard.model.VulnerabilityFinding;
import com.dependencyguard.repository.GraphService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RemediationService {

    private final GraphService graphService;

    public RemediationService(GraphService graphService) {
        this.graphService = graphService;
    }

    public RemediationPlan previewRemediation(String cveId) {
        VulnerabilityFinding finding = graphService.getVulnerabilityBlastRadius(cveId);
        if (finding == null) {
            throw new IllegalArgumentException("Vulnerability not found for identifier: " + cveId);
        }

        RemediationPlan plan = new RemediationPlan();
        plan.setId("plan-preview-" + UUID.randomUUID().toString().substring(0, 8));
        plan.setFindingId(finding.getId());
        plan.setPackageName(finding.getPackageName());
        plan.setCurrentVersion(finding.getCurrentVersion());
        plan.setTargetVersion(finding.getPatchedVersion());

        List<String> appNames = finding.getAffectedApplications().stream()
                .map(AffectedAppInfo::getAppName)
                .toList();
        plan.setAffectedApplications(appNames);

        plan.setReason("Eliminates " + finding.getCveId() + " (" + finding.getTitle() + ") across " +
                appNames.size() + " affected service(s).");

        boolean isMajorBump = false;
        try {
            int curMajor = Integer.parseInt(finding.getCurrentVersion().split("\\.")[0]);
            int tarMajor = Integer.parseInt(finding.getPatchedVersion().split("\\.")[0]);
            isMajorBump = (tarMajor > curMajor);
        } catch (Exception ignored) {}

        plan.setBreakingChangeRisk(isMajorBump ?
                "Moderate: Major version upgrade detected. Review API contract deprecations." :
                "Low: Minor / patch update. Backward-compatible security release.");

        plan.setVerificationSteps(List.of(
                "Run unit and integration test suite across affected applications (" + String.join(", ", appNames) + ")",
                "Verify no transitive dependency conflict introduced with existing classpaths",
                "Deploy patch to Staging environment and run end-to-end smoke verification",
                "Verify vulnerability CVE resolution in DependencyGuard graph analysis"
        ));

        plan.setStatus("PROPOSED");
        plan.setTimestamp(Instant.now().toString());
        plan.setRollbackAvailable(false);
        plan.setGraphMutationSummary(String.format(
                "Simulation Preview: Will rewire incoming DEPENDS_ON edges from %s:%s to %s:%s for applications: %s.",
                finding.getPackageName(), finding.getCurrentVersion(),
                finding.getPackageName(), finding.getPatchedVersion(),
                String.join(", ", appNames)
        ));

        return plan;
    }

    public RemediationPlan applyRemediation(String cveId) {
        VulnerabilityFinding finding = graphService.getVulnerabilityBlastRadius(cveId);
        if (finding == null) {
            throw new IllegalArgumentException("Vulnerability not found for identifier: " + cveId);
        }

        String curPkgId = "pkg-" + finding.getPackageName() + "-" + finding.getCurrentVersion();
        String patchedPkgId = "pkg-" + finding.getPackageName() + "-" + finding.getPatchedVersion();

        boolean ok = graphService.applyRemediation(curPkgId, patchedPkgId);
        if (!ok) {
            throw new IllegalStateException("Failed to apply graph remediation for " + cveId);
        }

        RemediationPlan plan = previewRemediation(cveId);
        plan.setStatus("APPLIED");
        plan.setRollbackAvailable(true);
        plan.setGraphMutationSummary(String.format(
                "Remediation Applied: Rewired graph edges to %s:%s. Target vulnerability %s is now neutralized for connected applications.",
                finding.getPackageName(), finding.getPatchedVersion(), finding.getCveId()
        ));
        return plan;
    }

    public RemediationPlan rollbackRemediation(String cveId) {
        VulnerabilityFinding finding = graphService.getVulnerabilityBlastRadius(cveId);
        if (finding == null) {
            throw new IllegalArgumentException("Vulnerability not found for identifier: " + cveId);
        }

        String curPkgId = "pkg-" + finding.getPackageName() + "-" + finding.getCurrentVersion();
        String patchedPkgId = "pkg-" + finding.getPackageName() + "-" + finding.getPatchedVersion();

        boolean ok = graphService.rollbackRemediation(curPkgId, patchedPkgId);
        if (!ok) {
            throw new IllegalStateException("Failed to rollback remediation for " + cveId);
        }

        RemediationPlan plan = previewRemediation(cveId);
        plan.setStatus("ROLLED_BACK");
        plan.setRollbackAvailable(false);
        plan.setGraphMutationSummary(String.format(
                "Remediation Rolled Back: Restored graph edges back to original package %s:%s. Vulnerability %s restored to active state.",
                finding.getPackageName(), finding.getCurrentVersion(), finding.getCveId()
        ));
        return plan;
    }

    public List<RemediationPlan> getRemediationHistory() {
        return graphService.getRemediationHistory();
    }
}
