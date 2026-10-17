package com.dependencyguard;

import com.dependencyguard.model.RemediationPlan;
import com.dependencyguard.model.VulnerabilityFinding;
import com.dependencyguard.repository.FalkorDbGraphRepository;
import com.dependencyguard.repository.GraphService;
import com.dependencyguard.repository.InMemoryGraphEngine;
import com.dependencyguard.service.RemediationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Remediation Simulation & Rollback Tests")
class RemediationServiceTest {

    private InMemoryGraphEngine fallbackEngine;
    private GraphService graphService;
    private RemediationService remediationService;

    @BeforeEach
    void setUp() {
        fallbackEngine = new InMemoryGraphEngine();
        fallbackEngine.initializeAndSeed();

        FalkorDbGraphRepository mockFalkor = Mockito.mock(FalkorDbGraphRepository.class);
        Mockito.when(mockFalkor.isConnected()).thenReturn(false);

        graphService = new GraphService(mockFalkor, fallbackEngine);
        remediationService = new RemediationService(graphService);
    }

    @Test
    @DisplayName("Should generate preview of remediation before graph mutation")
    void testPreviewRemediation() {
        RemediationPlan plan = remediationService.previewRemediation("CVE-2024-45321");
        assertNotNull(plan);
        assertEquals("PROPOSED", plan.getStatus());
        assertEquals("auth-core", plan.getPackageName());
        assertEquals("1.2.0", plan.getCurrentVersion());
        assertEquals("1.2.4", plan.getTargetVersion());
        assertFalse(plan.getAffectedApplications().isEmpty());
        assertFalse(plan.getVerificationSteps().isEmpty());
        assertTrue(plan.getGraphMutationSummary().contains("Simulation Preview"));
    }

    @Test
    @DisplayName("Should apply remediation, rewire edges, and support rollback")
    void testApplyAndRollbackRemediation() {
        // Initial state: CVE-2024-45321 has 2 affected apps
        VulnerabilityFinding initialFinding = graphService.getVulnerabilityBlastRadius("CVE-2024-45321");
        assertEquals(2, initialFinding.getAffectedApplications().size());

        // 1. Apply remediation
        RemediationPlan appliedPlan = remediationService.applyRemediation("CVE-2024-45321");
        assertNotNull(appliedPlan);
        assertEquals("APPLIED", appliedPlan.getStatus());
        assertTrue(appliedPlan.isRollbackAvailable());

        // After applying, applications no longer depend on 1.2.0 (now depend on 1.2.4)
        VulnerabilityFinding postFinding = graphService.getVulnerabilityBlastRadius("CVE-2024-45321");
        assertEquals(0, postFinding.getAffectedApplications().size(), "Vulnerability should have 0 affected apps after remediation");

        // 2. Rollback remediation
        RemediationPlan rollbackPlan = remediationService.rollbackRemediation("CVE-2024-45321");
        assertNotNull(rollbackPlan);
        assertEquals("ROLLED_BACK", rollbackPlan.getStatus());
        assertFalse(rollbackPlan.isRollbackAvailable());

        // After rollback, original edges are restored!
        VulnerabilityFinding restoredFinding = graphService.getVulnerabilityBlastRadius("CVE-2024-45321");
        assertEquals(2, restoredFinding.getAffectedApplications().size(), "Vulnerability should restore 2 affected apps after rollback");
    }
}
