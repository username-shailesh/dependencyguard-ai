package com.dependencyguard.model;

import java.util.HashMap;
import java.util.Map;

public class GraphNode {
    private String id;
    private String label;
    private String title;
    private String subtitle;
    private NodeType type;
    private Map<String, Object> properties = new HashMap<>();

    public GraphNode() {}

    public GraphNode(String id, String label, String title, String subtitle, NodeType type, Map<String, Object> properties) {
        this.id = id;
        this.label = label;
        this.title = title;
        this.subtitle = subtitle;
        this.type = type;
        if (properties != null) {
            this.properties.putAll(properties);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public NodeType getType() { return type; }
    public void setType(NodeType type) { this.type = type; }

    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }
}
