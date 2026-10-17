package com.dependencyguard.controller;

import com.dependencyguard.model.RemediationPlan;
import com.dependencyguard.service.RemediationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/remediation")
public class RemediationController {

    private final RemediationService remediationService;

    public RemediationController(RemediationService remediationService) {
        this.remediationService = remediationService;
    }

    @GetMapping("/preview/{cveId}")
    public ResponseEntity<RemediationPlan> preview(@PathVariable String cveId) {
        return ResponseEntity.ok(remediationService.previewRemediation(cveId));
    }

    @PostMapping("/apply/{cveId}")
    public ResponseEntity<RemediationPlan> apply(@PathVariable String cveId) {
        return ResponseEntity.ok(remediationService.applyRemediation(cveId));
    }

    @PostMapping("/rollback/{cveId}")
    public ResponseEntity<RemediationPlan> rollback(@PathVariable String cveId) {
        return ResponseEntity.ok(remediationService.rollbackRemediation(cveId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<RemediationPlan>> getHistory() {
        return ResponseEntity.ok(remediationService.getRemediationHistory());
    }
}
