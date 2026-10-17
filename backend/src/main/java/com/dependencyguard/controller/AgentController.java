package com.dependencyguard.controller;

import com.dependencyguard.model.AgentResponse;
import com.dependencyguard.model.InvestigationRecord;
import com.dependencyguard.service.AgentOrchestrationService;
import com.dependencyguard.service.InvestigationMemoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final AgentOrchestrationService agentService;
    private final InvestigationMemoryService memoryService;

    public AgentController(AgentOrchestrationService agentService, InvestigationMemoryService memoryService) {
        this.agentService = agentService;
        this.memoryService = memoryService;
    }

    @PostMapping("/investigate")
    public ResponseEntity<AgentResponse> investigate(@RequestBody Map<String, String> request) {
        String query = request.getOrDefault("query", "");
        AgentResponse response = agentService.investigate(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<InvestigationRecord>> getHistory() {
        return ResponseEntity.ok(memoryService.getInvestigationHistory());
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<InvestigationRecord> getHistoryById(@PathVariable String id) {
        return memoryService.getInvestigation(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
