package com.dependencyguard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("REST API End-to-End Integration Tests")
class ApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/graph/overview - returns valid security metrics")
    void testGetOverview() throws Exception {
        mockMvc.perform(get("/api/graph/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalApplications", is(5)))
                .andExpect(jsonPath("$.totalVulnerabilities", greaterThanOrEqualTo(4)))
                .andExpect(jsonPath("$.licenseViolations", greaterThan(0)))
                .andExpect(jsonPath("$.dataSource", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/graph/data - returns nodes and edges")
    void testGetGraphData() throws Exception {
        mockMvc.perform(get("/api/graph/data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodes", hasSize(greaterThan(10))))
                .andExpect(jsonPath("$.edges", hasSize(greaterThan(10))));
    }

    @Test
    @DisplayName("GET /api/vulnerabilities - returns prioritized findings")
    void testGetVulnerabilities() throws Exception {
        mockMvc.perform(get("/api/vulnerabilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(4))))
                .andExpect(jsonPath("$[0].cveId", notNullValue()))
                .andExpect(jsonPath("$[0].blastRadiusScore", greaterThan(0.0)));
    }

    @Test
    @DisplayName("GET /api/vulnerabilities/{cveId}/blast-radius - returns finding details")
    void testGetBlastRadius() throws Exception {
        mockMvc.perform(get("/api/vulnerabilities/CVE-2024-45321/blast-radius"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cveId", is("CVE-2024-45321")))
                .andExpect(jsonPath("$.packageName", is("auth-core")))
                .andExpect(jsonPath("$.affectedApplications", hasSize(2)));
    }

    @Test
    @DisplayName("POST /api/agent/investigate - processes natural language query")
    void testAgentInvestigate() throws Exception {
        String json = "{\"query\":\"Which applications are affected if auth-core is vulnerable?\"}";
        mockMvc.perform(post("/api/agent/investigate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intent", is("AFFECTED_APPLICATIONS")))
                .andExpect(jsonPath("$.grounded", is(true)))
                .andExpect(jsonPath("$.evidencePaths", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.answer", containsString("payment-service")));
    }

    @Test
    @DisplayName("GET /api/licenses/violations - returns compliance flags")
    void testGetLicenseViolations() throws Exception {
        mockMvc.perform(get("/api/licenses/violations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].spdxId", notNullValue()));
    }
}
