package com.dependencyguard.service;

public interface LlmAdapter {
    String generateExplanation(String systemInstruction, String userQuery, String graphContextJson);
    boolean isAvailable();
    String getProviderName();
}
