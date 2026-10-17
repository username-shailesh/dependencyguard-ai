package com.dependencyguard.controller;

import com.dependencyguard.model.GraphData;
import com.dependencyguard.model.GraphStatus;
import com.dependencyguard.model.OverviewMetrics;
import com.dependencyguard.repository.GraphService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/graph")
public class GraphController {

    private final GraphService graphService;

    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping("/overview")
    public ResponseEntity<OverviewMetrics> getOverviewMetrics() {
        return ResponseEntity.ok(graphService.getOverviewMetrics());
    }

    @GetMapping("/data")
    public ResponseEntity<GraphData> getGraphData() {
        return ResponseEntity.ok(graphService.getFullGraph());
    }

    @GetMapping("/status")
    public ResponseEntity<GraphStatus> getGraphStatus() {
        return ResponseEntity.ok(graphService.getStatus());
    }

    @PostMapping("/seed")
    public ResponseEntity<Map<String, Object>> seedGraph() {
        graphService.initializeAndSeed();
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Graph seeded successfully with synthetic software supply-chain dataset",
                "nodes", graphService.getNodeCount(),
                "edges", graphService.getEdgeCount(),
                "connectedToFalkorDb", graphService.isConnectedToFalkorDb()
        ));
    }

    @GetMapping("/shared")
    public ResponseEntity<List<Map<String, Object>>> getSharedDependencies() {
        return ResponseEntity.ok(graphService.getSharedDependencies());
    }
}
