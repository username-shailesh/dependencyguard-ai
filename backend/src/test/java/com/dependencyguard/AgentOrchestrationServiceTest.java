package com.dependencyguard;

import com.dependencyguard.config.LlmProperties;
import com.dependencyguard.model.AgentResponse;
import com.dependencyguard.repository.FalkorDbGraphRepository;
import com.dependencyguard.repository.GraphService;
import com.dependencyguard.repository.InMemoryGraphEngine;
import com.dependencyguard.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AI Investigation Agent Controlled Workflow Tests")
class AgentOrchestrationServiceTest {

    private AgentOrchestrationService agentService;

    @BeforeEach
    void setUp() {
        InMemoryGraphEngine fallbackEngine = new InMemoryGraphEngine();
        fallbackEngine.initializeAndSeed();

        FalkorDbGraphRepository mockFalkor = Mockito.mock(FalkorDbGraphRepository.class);
        Mockito.when(mockFalkor.isConnected()).thenReturn(false);

        GraphService graphService = new GraphService(mockFalkor, fallbackEngine);
        RiskAnalysisService riskAnalysisService = new RiskAnalysisService(graphService);
        RemediationService remediationService = new RemediationService(graphService);
        LicenseAnalysisService licenseService = new LicenseAnalysisService(graphService);
        InvestigationMemoryService memoryService = new InvestigationMemoryService(graphService);
        DeterministicAgentEngine deterministicEngine = new DeterministicAgentEngine();
        ExternalLlmAdapter externalLlmAdapter = new ExternalLlmAdapter(new LlmProperties());

        agentService = new AgentOrchestrationService(
                graphService,
                riskAnalysisService,
                remediationService,
                licenseService,
                memoryService,
                deterministicEngine,
                externalLlmAdapter
        );
    }

    @Test
    @DisplayName("Query 1: Which applications are affected if auth-core is vulnerable?")
    void testQueryAffectedApplications() {
        AgentResponse res = agentService.investigate("Which applications are affected if auth-core is vulnerable?");
        assertNotNull(res);
        assertTrue(res.isGrounded(), "Agent response must be grounded in verified graph data");
        assertEquals("AFFECTED_APPLICATIONS", res.getIntent());
        assertNotNull(res.getCypherQueryUsed());
        assertTrue(res.getAnswer().contains("payment-service"));
        assertTrue(res.getAnswer().contains("auth-gateway"));
        assertFalse(res.getEvidencePaths().isEmpty(), "Must return evidence paths");
        assertNotNull(res.getSuggestedRemediation());
    }

    @Test
    @DisplayName("Query 2: Show me the highest-priority security risks.")
    void testQueryPrioritizeRisks() {
        AgentResponse res = agentService.investigate("Show me the highest-priority security risks.");
        assertNotNull(res);
        assertEquals("PRIORITIZE_RISKS", res.getIntent());
        assertTrue(res.getAnswer().contains("Prioritized Security Risks"));
        assertFalse(res.getFindings().isEmpty(), "Should return prioritized findings");
        assertTrue(res.getFindings().get(0).getBlastRadiusScore() >= res.getFindings().get(1).getBlastRadiusScore(),
                "Findings must be ranked in descending risk score order");
    }

    @Test
    @DisplayName("Query 3: Which vulnerable package affects the largest number of applications?")
    void testQueryMostWidespread() {
        AgentResponse res = agentService.investigate("Which vulnerable package affects the largest number of applications?");
        assertNotNull(res);
        assertEquals("MOST_WIDESPREAD", res.getIntent());
        assertTrue(res.getAnswer().contains("Most Widespread Vulnerable Package"));
        assertFalse(res.getFindings().isEmpty());
    }

    @Test
    @DisplayName("Query 4: Explain how this application reaches the vulnerable dependency.")
    void testQueryExplainPath() {
        AgentResponse res = agentService.investigate("Explain how payment-service reaches crypto-vault.");
        assertNotNull(res);
        assertEquals("EXPLAIN_PATH", res.getIntent());
        assertTrue(res.getAnswer().contains("Multi-Hop Dependency Path Explanation"));
        assertFalse(res.getEvidencePaths().isEmpty(), "Evidence paths must not be empty");
    }

    @Test
    @DisplayName("Query 5: What should I fix first, and why?")
    void testQueryWhatToFixFirst() {
        AgentResponse res = agentService.investigate("What should I fix first, and why?");
        assertNotNull(res);
        assertEquals("REMEDIATION_ORDER", res.getIntent());
        assertTrue(res.getAnswer().contains("Recommended Remediation Roadmap"));
        assertTrue(res.getAnswer().contains("Priority 1"));
    }

    @Test
    @DisplayName("Query 6: Safe handling of unsupported/unknown questions")
    void testQueryUnknown() {
        AgentResponse res = agentService.investigate("What is the weather in Tokyo?");
        assertNotNull(res);
        assertEquals("UNKNOWN", res.getIntent());
        assertTrue(res.getAnswer().contains("could not map your query"));
        assertTrue(res.isGrounded(), "Must not invent hallucinations for unknown queries");
    }
}
