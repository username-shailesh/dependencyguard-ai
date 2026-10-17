package com.dependencyguard.service;

import com.dependencyguard.model.LicenseViolation;
import com.dependencyguard.repository.GraphService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LicenseAnalysisService {

    private final GraphService graphService;

    public LicenseAnalysisService(GraphService graphService) {
        this.graphService = graphService;
    }

    public List<LicenseViolation> getAllViolations() {
        return graphService.getLicenseViolations();
    }

    public List<LicenseViolation> getViolationsForApp(String appNameOrId) {
        return graphService.getLicenseViolations().stream()
                .filter(v -> v.getAppId().equalsIgnoreCase(appNameOrId) || v.getAppName().equalsIgnoreCase(appNameOrId))
                .toList();
    }

    public String getLicensePolicyDocumentation() {
        return """
        ### Enterprise Software Supply-Chain License Policy (SEC-LIC-04)
        
        **Policy Directive**: All proprietary applications distributed or operated as a hosted service (SaaS)
        must avoid direct or transitive dependencies licensed under strong copyleft licenses (e.g., GPL-3.0)
        or network copyleft licenses (e.g., AGPL-3.0) unless granted an explicit legal exception.
        
        *Permissible Licenses*: MIT, Apache-2.0, BSD-2/3-Clause, ISC.
        *Conditional Review*: LGPL-2.1/3.0, MPL-2.0.
        *High Risk Policy Flag*: GPL-2.0, GPL-3.0, AGPL-3.0, SSPL.
        
        *Notice*: These findings are policy flags requiring internal engineering and legal review, not definitive legal conclusions.
        """;
    }
}
