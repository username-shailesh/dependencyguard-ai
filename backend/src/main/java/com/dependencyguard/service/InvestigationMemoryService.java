package com.dependencyguard.service;

import com.dependencyguard.model.InvestigationRecord;
import com.dependencyguard.model.VulnerabilityFinding;
import com.dependencyguard.repository.GraphService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class InvestigationMemoryService {

    private final GraphService graphService;
    private final List<InvestigationRecord> history = new CopyOnWriteArrayList<>();
    private final Map<String, InvestigationRecord> historyById = new ConcurrentHashMap<>();

    public InvestigationMemoryService(GraphService graphService) {
        this.graphService = graphService;
        seedDefaultHistory();
    }

    private void seedDefaultHistory() {
        recordInvestigation(
                "Which applications are affected if auth-core is vulnerable?",
                "VULNERABILITY_IMPACT",
                List.of("pkg-auth-core-1.2.0", "vuln-cve-2024-45321"),
                2,
                "auth-core 1.2.0 is affected by CVE-2024-45321 (CVSS 9.8). Affects payment-service and auth-gateway.",
                List.of("payment-service -> auth-core:1.2.0", "auth-gateway -> auth-core:1.2.0"),
                94.1
        );
        recordInvestigation(
                "Show me the highest-priority security risks.",
                "PRIORITIZE_RISKS",
                List.of("vuln-cve-2024-45321", "vuln-cve-2021-44228", "vuln-cve-2024-21626"),
                5,
                "Ranked 5 active vulnerabilities by blast radius. Top priority: CVE-2024-45321 (94.1) followed by CVE-2021-44228 (92.5).",
                List.of("payment-service -> auth-core:1.2.0", "payment-service -> logging-facade:2.14.0"),
                94.1
        );
    }

    public synchronized InvestigationRecord recordInvestigation(String query, String intent, List<String> entityIds,
                                                               int findingsCount, String summary,
                                                               List<String> groundedEvidence, double riskScore) {
        String id = "inv-" + UUID.randomUUID().toString().substring(0, 8);
        InvestigationRecord record = new InvestigationRecord(
                id,
                Instant.now().toString(),
                query,
                intent,
                entityIds,
                findingsCount,
                summary,
                groundedEvidence,
                riskScore,
                true
        );
        history.add(0, record);
        historyById.put(id, record);
        return record;
    }

    public List<InvestigationRecord> getInvestigationHistory() {
        // Re-validate time-sensitive facts against the current live graph before returning
        for (InvestigationRecord record : history) {
            validateAgainstCurrentGraph(record);
        }
        return new ArrayList<>(history);
    }

    public Optional<InvestigationRecord> getInvestigation(String id) {
        InvestigationRecord record = historyById.get(id);
        if (record != null) {
            validateAgainstCurrentGraph(record);
            return Optional.of(record);
        }
        return Optional.empty();
    }

    /**
     * Grounds memory against the current graph: checks if previously flagged entities are still vulnerable.
     */
    public void validateAgainstCurrentGraph(InvestigationRecord record) {
        if (record.getEntityIds() == null) return;
        boolean stillActive = false;

        for (String entityId : record.getEntityIds()) {
            VulnerabilityFinding currentFinding = graphService.getVulnerabilityBlastRadius(entityId);
            if (currentFinding != null && !currentFinding.getAffectedApplications().isEmpty()) {
                stillActive = true;
                break;
            }
        }

        record.setActiveInGraph(stillActive);
    }
}
