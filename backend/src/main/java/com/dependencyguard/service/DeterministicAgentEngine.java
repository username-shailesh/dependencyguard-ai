package com.dependencyguard.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DeterministicAgentEngine implements LlmAdapter {
    private static final Logger log = LoggerFactory.getLogger(DeterministicAgentEngine.class);

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getProviderName() {
        return "Deterministic Rule-Based Grounding Engine";
    }

    @Override
    public String generateExplanation(String systemInstruction, String userQuery, String graphContextJson) {
        log.info("Generating deterministic grounded response for query: '{}'", userQuery);
        // The orchestration service handles semantic intent mapping and invokes the tailored templates
        return "Grounded Graph Explanation generated based on verified FalkorDB records:\n\n" + graphContextJson;
    }
}
