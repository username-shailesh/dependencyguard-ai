package com.dependencyguard.service;

import com.dependencyguard.model.RiskScoreBreakdown;
import com.dependencyguard.model.VulnerabilityFinding;
import com.dependencyguard.repository.GraphService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiskAnalysisService {

    private final GraphService graphService;

    public RiskAnalysisService(GraphService graphService) {
        this.graphService = graphService;
    }

    public List<VulnerabilityFinding> getPrioritizedRisks() {
        List<VulnerabilityFinding> findings = graphService.getVulnerabilityFindings();
        findings.sort((a, b) -> Double.compare(b.getBlastRadiusScore(), a.getBlastRadiusScore()));
        return findings;
    }

    public VulnerabilityFinding getFindingByCve(String cveId) {
        return graphService.getVulnerabilityBlastRadius(cveId);
    }

    public String getRiskFormulaDocumentation() {
        return """
        ### DependencyGuard AI Blast Radius Risk Score (BRS) Formula
        
        The Blast Radius Score (BRS) is a transparent, graph-aware supply-chain risk index (0.0 to 100.0)
        computed from multi-hop topology and organizational context:
        
        $$BRS = \\min(100.0, \\; BaseSeverity \\times (1.0 + 0.35 \\times N_{apps}) \\times W_{tier} \\times F_{depth} \\times M_{patch})$$
        
        Where:
        - **BaseSeverity**: 10.0 (CRITICAL), 7.5 (HIGH), 5.0 (MEDIUM), 2.5 (LOW).
        - **$N_{apps}$**: Number of distinct upstream applications reaching the vulnerability via direct or transitive dependencies.
        - **$W_{tier}$**: Criticality tier weight multiplier of affected applications (Tier 1 = 1.5×, Tier 2 = 1.2×, Tier 3 = 1.0×).
        - **$F_{depth}$**: Exposure depth factor $\\frac{1}{1 + 0.15 \\times (\\text{minDepth} - 1)}$. Direct dependencies have higher immediate exploitability ($F=1.0$).
        - **$M_{patch}$**: Remediation modifier (0.95 if verified patched version is available; 1.25 for unpatched zero-days).
        
        *Notice: BRS is an internal supply-chain blast-radius score and is explicitly distinct from standard CVSS.*
        """;
    }
}
