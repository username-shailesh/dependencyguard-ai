package com.dependencyguard.model;

import java.util.HashMap;
import java.util.Map;

public class GraphEdge {
    private String id;
    private String source;
    private String target;
    private String label;
    private boolean direct = true;
    private String scope = "compile";
    private int depth = 1;
    private Map<String, Object> properties = new HashMap<>();

    public GraphEdge() {}

    public GraphEdge(String id, String source, String target, String label, boolean direct, String scope, int depth) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.label = label;
        this.direct = direct;
        this.scope = scope;
        this.depth = depth;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public boolean isDirect() { return direct; }
    public void setDirect(boolean direct) { this.direct = direct; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public int getDepth() { return depth; }
    public void setDepth(int depth) { this.depth = depth; }

    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }
}
