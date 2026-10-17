package com.dependencyguard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "falkordb")
public class FalkorDbProperties {
    private String host = "localhost";
    private int port = 6379;
    private String graphName = "dependencyguard";
    private String password = "";
    private int timeoutMs = 5000;
    private boolean autoSeed = true;

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getGraphName() { return graphName; }
    public void setGraphName(String graphName) { this.graphName = graphName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; }

    public boolean isAutoSeed() { return autoSeed; }
    public void setAutoSeed(boolean autoSeed) { this.autoSeed = autoSeed; }
}
