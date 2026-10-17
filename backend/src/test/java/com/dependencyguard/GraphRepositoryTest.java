package com.dependencyguard;

import com.dependencyguard.model.*;
import com.dependencyguard.repository.InMemoryGraphEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Graph Repository & Traversal Tests")
class GraphRepositoryTest {

    private InMemoryGraphEngine graphEngine;

    @BeforeEach
    void setUp() {
        graphEngine = new InMemoryGraphEngine();
        graphEngine.initializeAndSeed();
    }

    @Test
    @DisplayName("Should initialize graph with applications, packages, vulnerabilities, and licenses")
    void testGraphInitialization() {
        GraphData data = graphEngine.getFullGraph();
        assertNotNull(data);
        assertTrue(data.getNodes().size() >= 15, "Expected at least 15 nodes in seeded graph");
        assertTrue(data.getEdges().size() >= 15, "Expected at least 15 edges in seeded graph");

        long appCount = data.getNodes().stream().filter(n -> n.getType() == NodeType.APPLICATION).count();
        assertEquals(5, appCount, "Seeded graph should contain 5 distinct enterprise applications");

        long pkgCount = data.getNodes().stream().filter(n -> n.getType() == NodeType.PACKAGE).count();
        assertTrue(pkgCount >= 10, "Seeded graph should contain at least 10 packages");
    }

    @Test
    @DisplayName("Should find direct and transitive multi-hop dependencies")
    void testMultiHopTraversal() {
        // payment-service -> auth-core -> jwt-verifier -> crypto-vault (3 hops)
        List<DependencyPath> paths = graphEngine.findPathsBetween("payment-service", "crypto-vault");
        assertNotNull(paths);
        assertFalse(paths.isEmpty(), "Should find multi-hop path from payment-service to crypto-vault");

        DependencyPath path = paths.get(0);
        assertTrue(path.getDepth() >= 2, "Path depth should be at least 2 hops");
        assertTrue(path.getFormattedPath().contains("auth-core"), "Path should traverse auth-core");
        assertTrue(path.getFormattedPath().contains("jwt-verifier"), "Path should traverse jwt-verifier");
    }

    @Test
    @DisplayName("Should identify shared dependencies across multiple applications")
    void testSharedDependencies() {
        List<Map<String, Object>> shared = graphEngine.getSharedDependencies();
        assertNotNull(shared);
        assertFalse(shared.isEmpty(), "Shared dependencies list should not be empty");

        // auth-core is shared by payment-service and auth-gateway
        boolean hasAuthCore = shared.stream().anyMatch(m -> "auth-core".equals(m.get("packageName")));
        assertTrue(hasAuthCore, "auth-core should be identified as a shared dependency");

        // http-client is shared by customer-portal and inventory-api
        boolean hasHttpClient = shared.stream().anyMatch(m -> "http-client".equals(m.get("packageName")));
        assertTrue(hasHttpClient, "http-client should be identified as a shared dependency");
    }

    @Test
    @DisplayName("Should detect license policy violations for copyleft licenses")
    void testLicenseViolations() {
        List<LicenseViolation> violations = graphEngine.getLicenseViolations();
        assertNotNull(violations);
        assertFalse(violations.isEmpty(), "Should detect copyleft license policy violations");

        boolean hasGpl = violations.stream().anyMatch(v -> "GPL-3.0".equalsIgnoreCase(v.getSpdxId()));
        assertTrue(hasGpl, "Should flag GPL-3.0 copyleft violation");

        boolean hasAgpl = violations.stream().anyMatch(v -> "AGPL-3.0".equalsIgnoreCase(v.getSpdxId()));
        assertTrue(hasAgpl, "Should flag AGPL-3.0 network copyleft violation");
    }
}
