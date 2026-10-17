package com.dependencyguard.model;

import java.util.ArrayList;
import java.util.List;

public class DependencyPath {
    private String pathId;
    private List<String> nodeIds = new ArrayList<>();
    private List<String> nodeLabels = new ArrayList<>();
    private int depth;
    private String formattedPath;

    public DependencyPath() {}

    public DependencyPath(String pathId, List<String> nodeIds, List<String> nodeLabels, int depth, String formattedPath) {
        this.pathId = pathId;
        this.nodeIds = nodeIds != null ? nodeIds : new ArrayList<>();
        this.nodeLabels = nodeLabels != null ? nodeLabels : new ArrayList<>();
        this.depth = depth;
        this.formattedPath = formattedPath;
    }

    public String getPathId() { return pathId; }
    public void setPathId(String pathId) { this.pathId = pathId; }

    public List<String> getNodeIds() { return nodeIds; }
    public void setNodeIds(List<String> nodeIds) { this.nodeIds = nodeIds; }

    public List<String> getNodeLabels() { return nodeLabels; }
    public void setNodeLabels(List<String> nodeLabels) { this.nodeLabels = nodeLabels; }

    public int getDepth() { return depth; }
    public void setDepth(int depth) { this.depth = depth; }

    public String getFormattedPath() { return formattedPath; }
    public void setFormattedPath(String formattedPath) { this.formattedPath = formattedPath; }
}
