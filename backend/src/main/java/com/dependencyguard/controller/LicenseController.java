package com.dependencyguard.controller;

import com.dependencyguard.model.LicenseViolation;
import com.dependencyguard.service.LicenseAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/licenses")
public class LicenseController {

    private final LicenseAnalysisService licenseService;

    public LicenseController(LicenseAnalysisService licenseService) {
        this.licenseService = licenseService;
    }

    @GetMapping("/violations")
    public ResponseEntity<List<LicenseViolation>> getViolations(
            @RequestParam(required = false) String app) {
        if (app != null && !app.isBlank()) {
            return ResponseEntity.ok(licenseService.getViolationsForApp(app));
        }
        return ResponseEntity.ok(licenseService.getAllViolations());
    }

    @GetMapping("/policy")
    public ResponseEntity<Map<String, String>> getPolicy() {
        return ResponseEntity.ok(Map.of(
                "policy", licenseService.getLicensePolicyDocumentation()
        ));
    }
}
