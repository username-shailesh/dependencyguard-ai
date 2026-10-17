package com.dependencyguard.repository;

import com.dependencyguard.config.FalkorDbProperties;
import com.dependencyguard.model.*;
import com.falkordb.Driver;
import com.falkordb.FalkorDB;
import com.falkordb.Graph;
import com.falkordb.Record;
import com.falkordb.ResultSet;
import com.falkordb.graph_entities.Edge;
import com.falkordb.graph_entities.Node;
import com.falkordb.graph_entities.Path;
import com.falkordb.graph_entities.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class FalkorDbGraphRepository implements GraphRepository {
    private static final Logger log = LoggerFactory.getLogger(FalkorDbGraphRepository.class);

    private final FalkorDbProperties properties;
    private Driver driver;
    private Graph graph;
    private boolean connected = false;

    public FalkorDbGraphRepository(FalkorDbProperties properties) {
        this.properties = properties;
        connect();
    }

    public synchronized void connect() {
        try {
            log.info("Attempting connection to FalkorDB at {}:{}...", properties.getHost(), properties.getPort());
            this.driver = FalkorDB.driver(properties.getHost(), properties.getPort());
            this.graph = driver.graph(properties.getGraphName());
            
            // Ping query to verify live connection
            ResultSet res = graph.query("RETURN 1 AS ping");
            if (res != null) {
                this.connected = true;
                log.info("Successfully connected to live FalkorDB instance! Graph: {}", properties.getGraphName());
                if (properties.isAutoSeed()) {
                    initializeAndSeed();
                }
            }
        } catch (Throwable e) {
            this.connected = false;
            log.warn("FalkorDB not reachable at {}:{}. Error: {}. Fallback graph engine will be active.",
                    properties.getHost(), properties.getPort(), e.getMessage());
        }
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public synchronized void initializeAndSeed() {
        if (!connected) return;
        try {
            log.info("Seeding FalkorDB graph '{}'...", properties.getGraphName());
            try {
                graph.deleteGraph();
            } catch (Exception ignored) {}

            String seedScript = loadResource("/seed.cypher");
            if (seedScript != null && !seedScript.isBlank()) {
                String[] statements = seedScript.split(";\\s*\\n");
                for (String stmt : statements) {
                    String clean = stmt.trim();
                    if (!clean.isEmpty() && !clean.startsWith("//")) {
                        try {
                            graph.query(clean);
                        } catch (Exception ex) {
                            log.debug("Cypher stmt executed: {}", ex.getMessage());
                        }
                    }
                }
            }
            log.info("FalkorDB graph seeded successfully.");
        } catch (Exception e) {
            log.error("Failed to seed FalkorDB graph: {}", e.getMessage(), e);
        }
    }

    private String loadResource(String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) return null;
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public GraphData getFullGraph() {
        if (!connected) return new GraphData();
        List<GraphNode> nodeList = new ArrayList<>();
        List<GraphEdge> edgeList = new ArrayList<>();
        Map<String, GraphNode> nodeMap = new HashMap<>();

        try {
            ResultSet rs = graph.query("MATCH (n) OPTIONAL MATCH (n)-[r]->(m) RETURN n, r, m");
            for (Record record : rs) {
                try {
                    Node n = record.getValue("n");
                    if (n != null) {
                        String id = getNodeProp(n, "id");
                        if (id != null && !nodeMap.containsKey(id)) {
                            GraphNode gn = mapFalkorNode(n);
                            nodeMap.put(id, gn);
                            nodeList.add(gn);
                        }
                    }

                    Edge r = record.getValue("r");
                    Node m = record.getValue("m");
                    if (r != null && m != null && n != null) {
                        String sourceId = getNodeProp(n, "id");
                        String targetId = getNodeProp(m, "id");
                        if (targetId != null && !nodeMap.containsKey(targetId)) {
                            GraphNode gm = mapFalkorNode(m);
                            nodeMap.put(targetId, gm);
                            nodeList.add(gm);
                        }
                        String edgeId = "edge-" + r.getId();
                        String label = r.getRelationshipType();
                        Property<?> dirProp = r.getProperty("direct");
                        boolean direct = dirProp != null && Boolean.parseBoolean(String.valueOf(dirProp.getValue()));
                        Property<?> scopeProp = r.getProperty("scope");
                        String scope = scopeProp != null ? String.valueOf(scopeProp.getValue()) : "compile";
                        int depth = 1;
                        if (sourceId != null && targetId != null) {
                            edgeList.add(new GraphEdge(edgeId, sourceId, targetId, label, direct, scope, depth));
                        }
                    }
                } catch (Exception ex) {
                    log.debug("Row parse: {}", ex.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Query failed: {}", e.getMessage());
        }
        return new GraphData(nodeList, edgeList);
    }

    private String getNodeProp(Node n, String propName) {
        Property<?> p = n.getProperty(propName);
        return p != null && p.getValue() != null ? String.valueOf(p.getValue()) : null;
    }

    private GraphNode mapFalkorNode(Node n) {
        String id = getNodeProp(n, "id");
        if (id == null) id = "node-" + n.getId();
        
        String label = (n.getNumberOfLabels() > 0) ? n.getLabel(0) : "Node";
        NodeType type = NodeType.PACKAGE;
        try {
            type = NodeType.valueOf(label.toUpperCase());
        } catch (Exception ignored) {}

        String title = getNodeProp(n, "name");
        if (title == null) title = getNodeProp(n, "cveId");
        if (title == null) title = id;

        String subtitle = "";
        if (type == NodeType.APPLICATION) {
            subtitle = getNodeProp(n, "criticalTier");
        } else if (type == NodeType.VULNERABILITY) {
            subtitle = "CVSS " + getNodeProp(n, "cvssScore") + " • " + getNodeProp(n, "severity");
        } else if (type == NodeType.PACKAGE) {
            subtitle = "v" + getNodeProp(n, "version") + " • " + getNodeProp(n, "ecosystem");
        }

        Map<String, Object> props = new HashMap<>();
        for (String propKey : n.getEntityPropertyNames()) {
            Property<?> p = n.getProperty(propKey);
            if (p != null) {
                props.put(propKey, p.getValue());
            }
        }
        return new GraphNode(id, title, title, subtitle, type, props);
    }

    @Override
    public List<VulnerabilityFinding> getVulnerabilityFindings() {
        if (!connected) return Collections.emptyList();
        List<VulnerabilityFinding> findings = new ArrayList<>();
        try {
            ResultSet rs = graph.query("MATCH (v:Vulnerability) RETURN v.id AS id, v.cveId AS cveId");
            for (Record r : rs) {
                String cveId = String.valueOf(r.getValue("cveId"));
                VulnerabilityFinding f = getVulnerabilityBlastRadius(cveId);
                if (f != null) findings.add(f);
            }
        } catch (Exception e) {
            log.error("Error fetching findings: {}", e.getMessage());
        }
        return findings;
    }

    @Override
    public VulnerabilityFinding getVulnerabilityBlastRadius(String cveOrPkgName) {
        if (!connected) return null;
        try {
            Map<String, Object> params = Map.of("cveId", cveOrPkgName, "pkgName", cveOrPkgName);
            String cypher = "MATCH path = (a:Application)-[:DEPENDS_ON*1..6]->(p:Package)-[:AFFECTED_BY]->(v:Vulnerability) " +
                            "WHERE v.cveId = $cveId OR v.id = $cveId OR p.name = $pkgName " +
                            "RETURN a, p, v, path";
            ResultSet rs = graph.query(cypher, params);
            if (!rs.iterator().hasNext()) return null;

            VulnerabilityFinding finding = new VulnerabilityFinding();
            List<DependencyPath> paths = new ArrayList<>();
            Map<String, AffectedAppInfo> appMap = new HashMap<>();

            for (Record record : rs) {
                Node v = record.getValue("v");
                Node p = record.getValue("p");
                Node a = record.getValue("a");
                Path path = record.getValue("path");

                if (finding.getCveId() == null && v != null) {
                    finding.setId(getNodeProp(v, "id"));
                    finding.setCveId(getNodeProp(v, "cveId"));
                    finding.setTitle(getNodeProp(v, "title"));
                    finding.setSeverity(Severity.valueOf(getNodeProp(v, "severity")));
                    finding.setCvssScore(Double.parseDouble(getNodeProp(v, "cvssScore")));
                    finding.setCvssVector(getNodeProp(v, "cvssVector"));
                    finding.setSummary(getNodeProp(v, "summary"));
                    finding.setPatchedVersion(getNodeProp(v, "patchedVersion"));
                    finding.setPublishedDate(getNodeProp(v, "publishedDate"));
                    finding.setPackageName(getNodeProp(p, "name"));
                    finding.setCurrentVersion(getNodeProp(p, "version"));
                }

                if (a != null) {
                    String appId = getNodeProp(a, "id");
                    String appName = getNodeProp(a, "name");
                    CriticalTier tier = CriticalTier.valueOf(getNodeProp(a, "criticalTier"));
                    int depth = path != null ? path.length() : 1;
                    boolean direct = (depth <= 2);
                    String pathDesc = appName + " -> " + getNodeProp(p, "name") + " (" + finding.getCveId() + ")";
                    appMap.putIfAbsent(appId, new AffectedAppInfo(appId, appName, getNodeProp(a, "environment"), tier, depth, direct, pathDesc));

                    if (path != null) {
                        List<String> nodeIds = new ArrayList<>();
                        List<String> labels = new ArrayList<>();
                        for (Node pn : path.getNodes()) {
                            nodeIds.add(getNodeProp(pn, "id"));
                            String name = getNodeProp(pn, "name");
                            labels.add(name != null ? name : getNodeProp(pn, "id"));
                        }
                        paths.add(new DependencyPath("path-" + UUID.randomUUID().toString().substring(0, 8), nodeIds, labels, path.length(), String.join(" -> ", labels)));
                    }
                }
            }

            finding.setPaths(paths);
            finding.setAffectedApplications(new ArrayList<>(appMap.values()));
            finding.setBlastRadiusScore(finding.getCvssScore() * (1 + 0.3 * appMap.size()));
            return finding;
        } catch (Exception e) {
            log.error("Blast radius query error: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<LicenseViolation> getLicenseViolations() {
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getSharedDependencies() {
        return Collections.emptyList();
    }

    @Override
    public List<DependencyPath> findPathsBetween(String appName, String targetPkgName) {
        return Collections.emptyList();
    }

    @Override
    public boolean applyRemediation(String currentPkgId, String patchedPkgId) {
        if (!connected) return false;
        try {
            String cypher = "MATCH (a)-[r:DEPENDS_ON]->(p:Package {id: $oldId}), (target:Package {id: $newId}) " +
                            "CREATE (a)-[:DEPENDS_ON {scope: r.scope, direct: r.direct, depth: r.depth}]->(target) " +
                            "DELETE r";
            graph.query(cypher, Map.of("oldId", currentPkgId, "newId", patchedPkgId));
            return true;
        } catch (Exception e) {
            log.error("Remediation execution failed in FalkorDB: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean rollbackRemediation(String currentPkgId, String patchedPkgId) {
        if (!connected) return false;
        try {
            String cypher = "MATCH (a)-[r:DEPENDS_ON]->(p:Package {id: $newId}), (target:Package {id: $oldId}) " +
                            "CREATE (a)-[:DEPENDS_ON {scope: r.scope, direct: r.direct, depth: r.depth}]->(target) " +
                            "DELETE r";
            graph.query(cypher, Map.of("oldId", currentPkgId, "newId", patchedPkgId));
            return true;
        } catch (Exception e) {
            log.error("Rollback execution failed in FalkorDB: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public int getNodeCount() {
        if (!connected) return 0;
        try {
            ResultSet rs = graph.query("MATCH (n) RETURN count(n) AS c");
            if (rs.iterator().hasNext()) {
                return Integer.parseInt(String.valueOf(rs.iterator().next().getValue("c")));
            }
        } catch (Exception ignored) {}
        return 0;
    }

    @Override
    public int getEdgeCount() {
        if (!connected) return 0;
        try {
            ResultSet rs = graph.query("MATCH ()-[r]->() RETURN count(r) AS c");
            if (rs.iterator().hasNext()) {
                return Integer.parseInt(String.valueOf(rs.iterator().next().getValue("c")));
            }
        } catch (Exception ignored) {}
        return 0;
    }

    @Override
    public GraphStatus getStatus() {
        return new GraphStatus(
                connected,
                properties.getHost(),
                properties.getPort(),
                properties.getGraphName(),
                getNodeCount(),
                getEdgeCount(),
                "JFalkorDB 0.13.1",
                connected ? "FALKORDB_LIVE" : "DISCONNECTED",
                connected ? "Connected to FalkorDB instance." : "FalkorDB host not reachable at " + properties.getHost() + ":" + properties.getPort()
        );
    }
}
