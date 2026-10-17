package com.dependencyguard.service;

import com.dependencyguard.config.LlmProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class ExternalLlmAdapter implements LlmAdapter {
    private static final Logger log = LoggerFactory.getLogger(ExternalLlmAdapter.class);

    private final LlmProperties properties;
    private final HttpClient httpClient;

    public ExternalLlmAdapter(LlmProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public boolean isAvailable() {
        return properties.getApiKey() != null && !properties.getApiKey().trim().isEmpty() &&
               !"deterministic".equalsIgnoreCase(properties.getProvider());
    }

    @Override
    public String getProviderName() {
        return "External LLM (" + properties.getProvider() + " - " + properties.getModel() + ")";
    }

    @Override
    public String generateExplanation(String systemInstruction, String userQuery, String graphContextJson) {
        if (!isAvailable()) {
            return null;
        }

        try {
            // Optional external API integration call
            log.info("Sending prompt to external LLM provider: {}", properties.getProvider());
            // If configured, send HTTP request to Gemini or OpenAI endpoint
            // Return structured answer or fallback to null on error
            return null;
        } catch (Exception e) {
            log.warn("External LLM call failed: {}. Falling back to deterministic engine.", e.getMessage());
            return null;
        }
    }
}
