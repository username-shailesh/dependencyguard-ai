package com.dependencyguard.model;

public class GraphStatus {
    private boolean connected;
    private String host;
    private int port;
    private String graphName;
    private int nodeCount;
    private int edgeCount;
    private String clientLibrary;
    private String mode;
    private String message;

    public GraphStatus() {}

    public GraphStatus(boolean connected, String host, int port, String graphName, int nodeCount, int edgeCount,
                       String clientLibrary, String mode, String message) {
        this.connected = connected;
        this.host = host;
        this.port = port;
        this.graphName = graphName;
        this.nodeCount = nodeCount;
        this.edgeCount = edgeCount;
        this.clientLibrary = clientLibrary;
        this.mode = mode;
        this.message = message;
    }

    public boolean isConnected() { return connected; }
    public void setConnected(boolean connected) { this.connected = connected; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getGraphName() { return graphName; }
    public void setGraphName(String graphName) { this.graphName = graphName; }

    public int getNodeCount() { return nodeCount; }
    public void setNodeCount(int nodeCount) { this.nodeCount = nodeCount; }

    public int getEdgeCount() { return edgeCount; }
    public void setEdgeCount(int edgeCount) { this.edgeCount = edgeCount; }

    public String getClientLibrary() { return clientLibrary; }
    public void setClientLibrary(String clientLibrary) { this.clientLibrary = clientLibrary; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
