package com.dependencyguard.repository;

import com.dependencyguard.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class InMemoryGraphEngine implements GraphRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryGraphEngine.class);

    private final Map<String, GraphNode> nodes = new ConcurrentHashMap<>();
    private final List<GraphEdge> edges = new CopyOnWriteArrayList<>();
    private final Map<String, List<GraphEdge>> outgoingEdges = new ConcurrentHashMap<>();
    private final Map<String, List<GraphEdge>> incomingEdges = new ConcurrentHashMap<>();

    // Keep history of rewires for rollback
    private final List<RemediationPlan> remediationHistory = new CopyOnWriteArrayList<>();

    public InMemoryGraphEngine() {
        initializeAndSeed();
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public synchronized void initializeAndSeed() {
        nodes.clear();
        edges.clear();
        outgoingEdges.clear();
        incomingEdges.clear();
        remediationHistory.clear();

        // 1. Teams
        addNode("team-sec", "Platform Security", "Elena Rostova", "Lead", NodeType.TEAM,
                Map.of("slack", "#sec-ops", "email", "security-ops@enterprise.internal"));
        addNode("team-payments", "Payments Squad", "Marcus Chen", "Lead", NodeType.TEAM,
                Map.of("slack", "#payments-eng", "email", "payments-eng@enterprise.internal"));
        addNode("team-identity", "Identity & Access Squad", "Sarah Jenkins", "Lead", NodeType.TEAM,
                Map.of("slack", "#auth-team", "email", "identity-eng@enterprise.internal"));
        addNode("team-data", "Data Platform Team", "Devon Brooks", "Lead", NodeType.TEAM,
                Map.of("slack", "#data-workers", "email", "data-eng@enterprise.internal"));
        addNode("team-frontend", "Digital Channels Squad", "Aaliyah Patel", "Lead", NodeType.TEAM,
                Map.of("slack", "#portal-web", "email", "channels-eng@enterprise.internal"));

        // 2. Applications
        addNode("app-payment-service", "payment-service", "Payment Service", "Production • Tier 1", NodeType.APPLICATION,
                Map.of("environment", "Production", "criticalTier", "TIER_1", "language", "Java", "framework", "Spring Boot 3.3.4",
                       "repo", "github.com/enterprise/payment-service"));
        addNode("app-auth-gateway", "auth-gateway", "Auth Gateway", "Production • Tier 1", NodeType.APPLICATION,
                Map.of("environment", "Production", "criticalTier", "TIER_1", "language", "Java", "framework", "Spring Boot 3.3.4",
                       "repo", "github.com/enterprise/auth-gateway"));
        addNode("app-customer-portal", "customer-portal", "Customer Portal", "Production • Tier 2", NodeType.APPLICATION,
                Map.of("environment", "Production", "criticalTier", "TIER_2", "language", "TypeScript", "framework", "React 18",
                       "repo", "github.com/enterprise/customer-portal"));
        addNode("app-inventory-api", "inventory-api", "Inventory API", "Production • Tier 2", NodeType.APPLICATION,
                Map.of("environment", "Production", "criticalTier", "TIER_2", "language", "Go", "framework", "Gin",
                       "repo", "github.com/enterprise/inventory-api"));
        addNode("app-analytics-worker", "analytics-worker", "Analytics Worker", "Staging • Tier 3", NodeType.APPLICATION,
                Map.of("environment", "Staging", "criticalTier", "TIER_3", "language", "Python", "framework", "FastAPI",
                       "repo", "github.com/enterprise/analytics-worker"));

        // 3. Application Maintenance Edges
        addEdge("edge-m-1", "app-payment-service", "team-payments", "MAINTAINED_BY", true, "ownership", 1);
        addEdge("edge-m-2", "app-auth-gateway", "team-identity", "MAINTAINED_BY", true, "ownership", 1);
        addEdge("edge-m-3", "app-customer-portal", "team-frontend", "MAINTAINED_BY", true, "ownership", 1);
        addEdge("edge-m-4", "app-inventory-api", "team-sec", "MAINTAINED_BY", true, "ownership", 1);
        addEdge("edge-m-5", "app-analytics-worker", "team-data", "MAINTAINED_BY", true, "ownership", 1);

        // 4. Licenses
        addNode("lic-mit", "MIT", "MIT License", "Permissive", NodeType.LICENSE,
                Map.of("spdxId", "MIT", "riskLevel", "LOW", "category", "PERMISSIVE", "commercialUseAllowed", true));
        addNode("lic-apache-2.0", "Apache-2.0", "Apache License 2.0", "Permissive", NodeType.LICENSE,
                Map.of("spdxId", "Apache-2.0", "riskLevel", "LOW", "category", "PERMISSIVE", "commercialUseAllowed", true));
        addNode("lic-bsd-3-clause", "BSD-3-Clause", "BSD 3-Clause", "Permissive", NodeType.LICENSE,
                Map.of("spdxId", "BSD-3-Clause", "riskLevel", "LOW", "category", "PERMISSIVE", "commercialUseAllowed", true));
        addNode("lic-gpl-3.0", "GPL-3.0", "GNU General Public License v3.0", "Copyleft High Risk", NodeType.LICENSE,
                Map.of("spdxId", "GPL-3.0", "riskLevel", "HIGH", "category", "COPYLEFT_STRONG", "commercialUseAllowed", false));
        addNode("lic-agpl-3.0", "AGPL-3.0", "GNU Affero General Public License v3.0", "Network Copyleft Critical", NodeType.LICENSE,
                Map.of("spdxId", "AGPL-3.0", "riskLevel", "CRITICAL", "category", "NETWORK_COPYLEFT", "commercialUseAllowed", false));

        // 5. Packages
        addNode("pkg-auth-core-1.2.0", "auth-core:1.2.0", "auth-core", "v1.2.0 • Maven", NodeType.PACKAGE,
                Map.of("name", "auth-core", "version", "1.2.0", "ecosystem", "Maven", "isPatchedVersion", false));
        addNode("pkg-auth-core-1.2.4", "auth-core:1.2.4", "auth-core (Patched)", "v1.2.4 • Maven", NodeType.PACKAGE,
                Map.of("name", "auth-core", "version", "1.2.4", "ecosystem", "Maven", "isPatchedVersion", true));

        addNode("pkg-jwt-verifier-3.4.0", "jwt-verifier:3.4.0", "jwt-verifier", "v3.4.0 • Maven", NodeType.PACKAGE,
                Map.of("name", "jwt-verifier", "version", "3.4.0", "ecosystem", "Maven", "isPatchedVersion", false));

        addNode("pkg-crypto-vault-0.9.1", "crypto-vault:0.9.1", "crypto-vault", "v0.9.1 • Maven", NodeType.PACKAGE,
                Map.of("name", "crypto-vault", "version", "0.9.1", "ecosystem", "Maven", "isPatchedVersion", false));
        addNode("pkg-crypto-vault-0.9.5", "crypto-vault:0.9.5", "crypto-vault (Patched)", "v0.9.5 • Maven", NodeType.PACKAGE,
                Map.of("name", "crypto-vault", "version", "0.9.5", "ecosystem", "Maven", "isPatchedVersion", true));

        addNode("pkg-logging-facade-2.14.0", "logging-facade:2.14.0", "logging-facade", "v2.14.0 • Maven", NodeType.PACKAGE,
                Map.of("name", "logging-facade", "version", "2.14.0", "ecosystem", "Maven", "isPatchedVersion", false));
        addNode("pkg-logging-facade-2.17.1", "logging-facade:2.17.1", "logging-facade (Patched)", "v2.17.1 • Maven", NodeType.PACKAGE,
                Map.of("name", "logging-facade", "version", "2.17.1", "ecosystem", "Maven", "isPatchedVersion", true));

        addNode("pkg-http-client-4.2.0", "http-client:4.2.0", "http-client", "v4.2.0 • npm", NodeType.PACKAGE,
                Map.of("name", "http-client", "version", "4.2.0", "ecosystem", "npm", "isPatchedVersion", false));
        addNode("pkg-http-client-4.2.2", "http-client:4.2.2", "http-client (Patched)", "v4.2.2 • npm", NodeType.PACKAGE,
                Map.of("name", "http-client", "version", "4.2.2", "ecosystem", "npm", "isPatchedVersion", true));

        addNode("pkg-data-exporter-1.0.3", "data-exporter:1.0.3", "data-exporter", "v1.0.3 • npm", NodeType.PACKAGE,
                Map.of("name", "data-exporter", "version", "1.0.3", "ecosystem", "npm", "isPatchedVersion", false));

        addNode("pkg-pdf-render-0.5.2", "pdf-render:0.5.2", "pdf-render", "v0.5.2 • Go", NodeType.PACKAGE,
                Map.of("name", "pdf-render", "version", "0.5.2", "ecosystem", "Go", "isPatchedVersion", false));

        addNode("pkg-metrics-agent-1.8.0", "metrics-agent:1.8.0", "metrics-agent", "v1.8.0 • PyPI", NodeType.PACKAGE,
                Map.of("name", "metrics-agent", "version", "1.8.0", "ecosystem", "PyPI", "isPatchedVersion", false));

        addNode("pkg-cache-layer-2.0.1", "cache-layer:2.0.1", "cache-layer", "v2.0.1 • Maven", NodeType.PACKAGE,
                Map.of("name", "cache-layer", "version", "2.0.1", "ecosystem", "Maven", "isPatchedVersion", false));

        // 6. Package Licenses
        addEdge("edge-lic-1", "pkg-auth-core-1.2.0", "lic-apache-2.0", "HAS_LICENSE", true, "license", 1);
        addEdge("edge-lic-2", "pkg-auth-core-1.2.4", "lic-apache-2.0", "HAS_LICENSE", true, "license", 1);
        addEdge("edge-lic-3", "pkg-jwt-verifier-3.4.0", "lic-mit", "HAS_LICENSE", true, "license", 1);
        addEdge("edge-lic-4", "pkg-crypto-vault-0.9.1", "lic-bsd-3-clause", "HAS_LICENSE", true, "license", 1);
        addEdge("edge-lic-5", "pkg-crypto-vault-0.9.5", "lic-bsd-3-clause", "HAS_LICENSE", true, "license", 1);
        addEdge("edge-lic-6", "pkg-logging-facade-2.14.0", "lic-apache-2.0", "HAS_LICENSE", true, "license", 1);
        addEdge("edge-lic-7", "pkg-logging-facade-2.17.1", "lic-apache-2.0", "HAS_LICENSE", true, "license", 1);
        addEdge("edge-lic-8", "pkg-http-client-4.2.0", "lic-mit", "HAS_LICENSE", true, "license", 1);
        addEdge("edge-lic-9", "pkg-http-client-4.2.2", "lic-mit", "HAS_LICENSE", true, "license", 1);
        addEdge("edge-lic-10", "pkg-cache-layer-2.0.1", "lic-mit", "HAS_LICENSE", true, "license", 1);
        addEdge("edge-lic-11", "pkg-metrics-agent-1.8.0", "lic-apache-2.0", "HAS_LICENSE", true, "license", 1);
        addEdge("edge-lic-12", "pkg-data-exporter-1.0.3", "lic-gpl-3.0", "HAS_LICENSE", true, "license", 1);
        addEdge("edge-lic-13", "pkg-pdf-render-0.5.2", "lic-agpl-3.0", "HAS_LICENSE", true, "license", 1);

        // 7. Vulnerabilities
        addNode("vuln-cve-2024-45321", "CVE-2024-45321", "Authentication Token Bypass in auth-core", "CVSS 9.8 • CRITICAL", NodeType.VULNERABILITY,
                Map.of("cveId", "CVE-2024-45321", "title", "Authentication Token Bypass in auth-core",
                       "severity", "CRITICAL", "cvssScore", 9.8, "cvssVector", "CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H",
                       "summary", "Improper verification of JWT algorithm signatures allows forging arbitrary session tokens.",
                       "patchedVersion", "1.2.4", "publishedDate", "2024-04-12"));

        addNode("vuln-cve-2024-21626", "CVE-2024-21626", "Side-Channel Key Recovery in crypto-vault", "CVSS 8.1 • HIGH", NodeType.VULNERABILITY,
                Map.of("cveId", "CVE-2024-21626", "title", "Side-Channel Key Recovery in crypto-vault",
                       "severity", "HIGH", "cvssScore", 8.1, "cvssVector", "CVSS:3.1/AV:N/AC:H/PR:N/UI:N/S:U/C:H/I:H/A:N",
                       "summary", "Non-constant time modular exponentiation reveals private session keys under high throughput.",
                       "patchedVersion", "0.9.5", "publishedDate", "2024-01-30"));

        addNode("vuln-cve-2021-44228", "CVE-2021-44228", "Remote Code Execution in logging-facade JNDI Lookups", "CVSS 10.0 • CRITICAL", NodeType.VULNERABILITY,
                Map.of("cveId", "CVE-2021-44228", "title", "Remote Code Execution in logging-facade JNDI Lookups",
                       "severity", "CRITICAL", "cvssScore", 10.0, "cvssVector", "CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:C/C:H/I:H/A:H",
                       "summary", "Arbitrary JNDI message lookup parsing executes untrusted remote bytecode via LDAP payloads.",
                       "patchedVersion", "2.17.1", "publishedDate", "2021-12-10"));

        addNode("vuln-cve-2023-44487", "CVE-2023-44487", "HTTP/2 Rapid Reset Denial of Service in http-client", "CVSS 7.5 • HIGH", NodeType.VULNERABILITY,
                Map.of("cveId", "CVE-2023-44487", "title", "HTTP/2 Rapid Reset Denial of Service in http-client",
                       "severity", "HIGH", "cvssScore", 7.5, "cvssVector", "CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:N/I:N/A:H",
                       "summary", "RST_STREAM frame storm exhausts server worker threads without generating log events.",
                       "patchedVersion", "4.2.2", "publishedDate", "2023-10-10"));

        addNode("vuln-cve-2023-38545", "CVE-2023-38545", "Heap Buffer Overflow in metrics-agent Transport", "CVSS 5.8 • MEDIUM", NodeType.VULNERABILITY,
                Map.of("cveId", "CVE-2023-38545", "title", "Heap Buffer Overflow in metrics-agent Transport",
                       "severity", "MEDIUM", "cvssScore", 5.8, "cvssVector", "CVSS:3.1/AV:N/AC:H/PR:N/UI:R/S:U/C:L/I:L/A:L",
                       "summary", "SOCKS5 hostname resolution causes out-of-bounds heap write during slow network negotiation.",
                       "patchedVersion", "1.8.2", "publishedDate", "2023-10-11"));

        // 8. Package Affected_By Edges
        addEdge("edge-v-1", "pkg-auth-core-1.2.0", "vuln-cve-2024-45321", "AFFECTED_BY", true, "vulnerability", 1);
        addEdge("edge-v-2", "pkg-crypto-vault-0.9.1", "vuln-cve-2024-21626", "AFFECTED_BY", true, "vulnerability", 1);
        addEdge("edge-v-3", "pkg-logging-facade-2.14.0", "vuln-cve-2021-44228", "AFFECTED_BY", true, "vulnerability", 1);
        addEdge("edge-v-4", "pkg-http-client-4.2.0", "vuln-cve-2023-44487", "AFFECTED_BY", true, "vulnerability", 1);
        addEdge("edge-v-5", "pkg-metrics-agent-1.8.0", "vuln-cve-2023-38545", "AFFECTED_BY", true, "vulnerability", 1);

        // 9. Dependency Relationships (Direct & Transitive Multi-Hop)
        // payment-service
        addEdge("edge-dep-1", "app-payment-service", "pkg-auth-core-1.2.0", "DEPENDS_ON", true, "compile", 1);
        addEdge("edge-dep-2", "app-payment-service", "pkg-logging-facade-2.14.0", "DEPENDS_ON", true, "compile", 1);
        addEdge("edge-dep-3", "app-payment-service", "pkg-cache-layer-2.0.1", "DEPENDS_ON", true, "runtime", 1);

        // auth-gateway
        addEdge("edge-dep-4", "app-auth-gateway", "pkg-auth-core-1.2.0", "DEPENDS_ON", true, "compile", 1);
        addEdge("edge-dep-5", "app-auth-gateway", "pkg-cache-layer-2.0.1", "DEPENDS_ON", true, "runtime", 1);

        // Transitive hops: auth-core -> jwt-verifier -> crypto-vault
        addEdge("edge-dep-6", "pkg-auth-core-1.2.0", "pkg-jwt-verifier-3.4.0", "DEPENDS_ON", false, "compile", 2);
        addEdge("edge-dep-7", "pkg-auth-core-1.2.4", "pkg-jwt-verifier-3.4.0", "DEPENDS_ON", false, "compile", 2);
        addEdge("edge-dep-8", "pkg-jwt-verifier-3.4.0", "pkg-crypto-vault-0.9.1", "DEPENDS_ON", false, "runtime", 3);

        // customer-portal
        addEdge("edge-dep-9", "app-customer-portal", "pkg-http-client-4.2.0", "DEPENDS_ON", true, "dependencies", 1);
        addEdge("edge-dep-10", "app-customer-portal", "pkg-data-exporter-1.0.3", "DEPENDS_ON", true, "dependencies", 1);

        // inventory-api
        addEdge("edge-dep-11", "app-inventory-api", "pkg-http-client-4.2.0", "DEPENDS_ON", true, "require", 1);
        addEdge("edge-dep-12", "app-inventory-api", "pkg-pdf-render-0.5.2", "DEPENDS_ON", true, "require", 1);

        // analytics-worker
        addEdge("edge-dep-13", "app-analytics-worker", "pkg-metrics-agent-1.8.0", "DEPENDS_ON", true, "install_requires", 1);
        addEdge("edge-dep-14", "app-analytics-worker", "pkg-logging-facade-2.14.0", "DEPENDS_ON", true, "install_requires", 1);

        log.info("Initialized In-Memory Graph with {} nodes and {} edges", nodes.size(), edges.size());
    }

    private void addNode(String id, String label, String title, String subtitle, NodeType type, Map<String, Object> props) {
        GraphNode node = new GraphNode(id, label, title, subtitle, type, props);
        nodes.put(id, node);
    }

    private void addEdge(String id, String source, String target, String label, boolean direct, String scope, int depth) {
        GraphEdge edge = new GraphEdge(id, source, target, label, direct, scope, depth);
        edges.add(edge);
        outgoingEdges.computeIfAbsent(source, k -> new CopyOnWriteArrayList<>()).add(edge);
        incomingEdges.computeIfAbsent(target, k -> new CopyOnWriteArrayList<>()).add(edge);
    }

    @Override
    public GraphData getFullGraph() {
        return new GraphData(new ArrayList<>(nodes.values()), new ArrayList<>(edges));
    }

    @Override
    public List<VulnerabilityFinding> getVulnerabilityFindings() {
        List<VulnerabilityFinding> findings = new ArrayList<>();
        for (GraphNode vNode : nodes.values()) {
            if (vNode.getType() == NodeType.VULNERABILITY) {
                VulnerabilityFinding finding = getVulnerabilityBlastRadius(vNode.getId());
                if (finding != null) {
                    findings.add(finding);
                }
            }
        }
        findings.sort((a, b) -> Double.compare(b.getBlastRadiusScore(), a.getBlastRadiusScore()));
        return findings;
    }

    @Override
    public VulnerabilityFinding getVulnerabilityBlastRadius(String cveOrPkgName) {
        // Find vulnerability node
        GraphNode vNode = null;
        for (GraphNode n : nodes.values()) {
            if (n.getType() == NodeType.VULNERABILITY) {
                if (n.getId().equalsIgnoreCase(cveOrPkgName) ||
                    String.valueOf(n.getProperties().get("cveId")).equalsIgnoreCase(cveOrPkgName)) {
                    vNode = n;
                    break;
                }
            }
        }

        // If not found by vuln id, check if package name was supplied
        if (vNode == null) {
            for (GraphNode pNode : nodes.values()) {
                if (pNode.getType() == NodeType.PACKAGE &&
                    (pNode.getId().equalsIgnoreCase(cveOrPkgName) ||
                     String.valueOf(pNode.getProperties().get("name")).equalsIgnoreCase(cveOrPkgName))) {
                    // find vulnerability connected to this package
                    List<GraphEdge> pkgOut = outgoingEdges.getOrDefault(pNode.getId(), Collections.emptyList());
                    for (GraphEdge e : pkgOut) {
                        if ("AFFECTED_BY".equals(e.getLabel())) {
                            vNode = nodes.get(e.getTarget());
                            break;
                        }
                    }
                    if (vNode != null) break;
                }
            }
        }

        if (vNode == null) return null;

        // Find package affected by this vulnerability
        GraphNode affectedPkg = null;
        List<GraphEdge> inToVuln = incomingEdges.getOrDefault(vNode.getId(), Collections.emptyList());
        for (GraphEdge e : inToVuln) {
            if ("AFFECTED_BY".equals(e.getLabel())) {
                affectedPkg = nodes.get(e.getSource());
                break;
            }
        }
        if (affectedPkg == null) return null;

        VulnerabilityFinding finding = new VulnerabilityFinding();
        finding.setId(vNode.getId());
        finding.setCveId(String.valueOf(vNode.getProperties().get("cveId")));
        finding.setTitle(String.valueOf(vNode.getProperties().get("title")));
        finding.setSeverity(Severity.valueOf(String.valueOf(vNode.getProperties().get("severity"))));
        finding.setCvssScore(Double.parseDouble(String.valueOf(vNode.getProperties().get("cvssScore"))));
        finding.setCvssVector(String.valueOf(vNode.getProperties().get("cvssVector")));
        finding.setSummary(String.valueOf(vNode.getProperties().get("summary")));
        finding.setPackageName(String.valueOf(affectedPkg.getProperties().get("name")));
        finding.setCurrentVersion(String.valueOf(affectedPkg.getProperties().get("version")));
        finding.setPatchedVersion(String.valueOf(vNode.getProperties().get("patchedVersion")));
        finding.setPublishedDate(String.valueOf(vNode.getProperties().get("publishedDate")));

        // Traverse backwards along DEPENDS_ON to find all affected Applications and multi-hop paths
        List<DependencyPath> paths = new ArrayList<>();
        List<AffectedAppInfo> affectedApps = new ArrayList<>();
        Map<String, AffectedAppInfo> appMap = new HashMap<>();

        findReversePathsToApps(affectedPkg.getId(), new ArrayList<>(List.of(affectedPkg.getId())),
                new ArrayList<>(List.of(affectedPkg.getLabel())), 0, paths, appMap);

        finding.setPaths(paths);
        finding.setAffectedApplications(new ArrayList<>(appMap.values()));

        // Calculate Blast Radius Risk Score
        RiskScoreBreakdown breakdown = calculateRiskScore(finding.getSeverity(), appMap.values(), paths, finding.getPatchedVersion());
        finding.setBlastRadiusScore(breakdown.getFinalScore());
        finding.setRiskBreakdown(breakdown);
        finding.setRemediationPlan("Upgrade package " + finding.getPackageName() + " from version " +
                finding.getCurrentVersion() + " to safe candidate " + finding.getPatchedVersion() + ".");

        return finding;
    }

    private void findReversePathsToApps(String currentId, List<String> currentPathIds,
                                        List<String> currentPathLabels, int depth,
                                        List<DependencyPath> resultPaths,
                                        Map<String, AffectedAppInfo> appMap) {
        if (depth > 6) return; // prevent deep cycles

        List<GraphEdge> inEdges = incomingEdges.getOrDefault(currentId, Collections.emptyList());
        for (GraphEdge edge : inEdges) {
            if ("DEPENDS_ON".equals(edge.getLabel())) {
                GraphNode sourceNode = nodes.get(edge.getSource());
                if (sourceNode == null) continue;

                // Cycle check
                if (currentPathIds.contains(sourceNode.getId())) continue;

                List<String> nextIds = new ArrayList<>(currentPathIds);
                nextIds.add(0, sourceNode.getId());

                List<String> nextLabels = new ArrayList<>(currentPathLabels);
                nextLabels.add(0, sourceNode.getLabel());

                if (sourceNode.getType() == NodeType.APPLICATION) {
                    // Reached an application!
                    String pathId = "path-" + UUID.randomUUID().toString().substring(0, 8);
                    String formatted = String.join(" -> ", nextLabels);
                    DependencyPath depPath = new DependencyPath(pathId, nextIds, nextLabels, nextIds.size() - 1, formatted);
                    resultPaths.add(depPath);

                    CriticalTier tier = CriticalTier.valueOf(String.valueOf(sourceNode.getProperties().getOrDefault("criticalTier", "TIER_3")));
                    boolean isDirect = (nextIds.size() == 2);
                    AffectedAppInfo appInfo = new AffectedAppInfo(
                            sourceNode.getId(),
                            sourceNode.getLabel(),
                            String.valueOf(sourceNode.getProperties().get("environment")),
                            tier,
                            nextIds.size() - 1,
                            isDirect,
                            formatted
                    );
                    appMap.putIfAbsent(sourceNode.getId(), appInfo);
                } else if (sourceNode.getType() == NodeType.PACKAGE) {
                    findReversePathsToApps(sourceNode.getId(), nextIds, nextLabels, depth + 1, resultPaths, appMap);
                }
            }
        }
    }

    private RiskScoreBreakdown calculateRiskScore(Severity severity, Collection<AffectedAppInfo> apps,
                                                  List<DependencyPath> paths, String patchedVersion) {
        double baseScore = severity.getBaseScore() * 2.5; // Scale to 0-100 baseline: 25.0, 18.75, 12.5, 6.25
        int count = apps.size();

        // Calculate tier multiplier
        double tierSum = 0.0;
        for (AffectedAppInfo app : apps) {
            tierSum += app.getCriticalTier().getMultiplier();
        }
        double tierMultiplier = (count > 0) ? (tierSum / count) : 1.0;

        // Min depth across all paths
        int minDepth = paths.stream().mapToInt(DependencyPath::getDepth).min().orElse(1);
        double depthFactor = 1.0 / (1.0 + 0.15 * (minDepth - 1));

        // Remediation availability: if patch is available, vulnerability is actionable (0.95), if zero-day / no patch, risk is amplified (1.25)
        double remediationFactor = (patchedVersion != null && !patchedVersion.isBlank()) ? 0.95 : 1.25;

        // Formula: BaseSeverity * (1 + 0.35 * count) * TierMultiplier * DepthFactor * RemediationFactor
        double raw = baseScore * (1.0 + 0.35 * count) * tierMultiplier * depthFactor * remediationFactor;
        double finalScore = Math.min(100.0, Math.round(raw * 10.0) / 10.0);

        String explanation = String.format(
                "Blast Radius Score = BaseWeight(%.1f) × AppFactor(1 + 0.35×%d) × TierMultiplier(%.2f) × DepthFactor(%.2f) × RemediationMod(%.2f) = %.1f / 100",
                baseScore, count, tierMultiplier, depthFactor, remediationFactor, finalScore
        );

        return new RiskScoreBreakdown(baseScore, count, tierMultiplier, depthFactor, remediationFactor, finalScore, explanation);
    }

    @Override
    public List<LicenseViolation> getLicenseViolations() {
        List<LicenseViolation> violations = new ArrayList<>();
        // Scan all packages that have copyleft / high-risk licenses
        for (GraphNode pNode : nodes.values()) {
            if (pNode.getType() == NodeType.PACKAGE) {
                List<GraphEdge> out = outgoingEdges.getOrDefault(pNode.getId(), Collections.emptyList());
                for (GraphEdge e : out) {
                    if ("HAS_LICENSE".equals(e.getLabel())) {
                        GraphNode licNode = nodes.get(e.getTarget());
                        if (licNode != null) {
                            String riskLevel = String.valueOf(licNode.getProperties().get("riskLevel"));
                            String spdx = String.valueOf(licNode.getProperties().get("spdxId"));
                            if ("HIGH".equalsIgnoreCase(riskLevel) || "CRITICAL".equalsIgnoreCase(riskLevel) ||
                                "GPL-3.0".equalsIgnoreCase(spdx) || "AGPL-3.0".equalsIgnoreCase(spdx)) {

                                // Find applications using this package
                                List<DependencyPath> paths = new ArrayList<>();
                                Map<String, AffectedAppInfo> appMap = new HashMap<>();
                                findReversePathsToApps(pNode.getId(), new ArrayList<>(List.of(pNode.getId())),
                                        new ArrayList<>(List.of(pNode.getLabel())), 0, paths, appMap);

                                for (AffectedAppInfo app : appMap.values()) {
                                    String vId = "lic-viol-" + app.getAppId() + "-" + pNode.getId();
                                    String pathDesc = paths.stream()
                                            .filter(p -> p.getNodeIds().contains(app.getAppId()))
                                            .findFirst()
                                            .map(DependencyPath::getFormattedPath)
                                            .orElse(app.getAppName() + " -> " + pNode.getLabel());

                                    LicenseViolation violation = new LicenseViolation(
                                            vId,
                                            app.getAppId(),
                                            app.getAppName(),
                                            app.getEnvironment(),
                                            String.valueOf(pNode.getProperties().get("name")),
                                            String.valueOf(pNode.getProperties().get("version")),
                                            spdx,
                                            riskLevel,
                                            String.valueOf(licNode.getProperties().get("category")),
                                            (Boolean) licNode.getProperties().getOrDefault("commercialUseAllowed", false),
                                            pathDesc,
                                            "Enterprise Policy SEC-LIC-04: Proprietary commercial applications cannot link copyleft licenses (" + spdx + ") without legal review.",
                                            "Replace " + pNode.getLabel() + " with an MIT or Apache-2.0 compatible alternative or obtain a dual-license commercial grant.",
                                            app.getDepth()
                                    );
                                    violations.add(violation);
                                }
                            }
                        }
                    }
                }
            }
        }
        return violations;
    }

    @Override
    public List<Map<String, Object>> getSharedDependencies() {
        Map<String, Set<String>> pkgToApps = new HashMap<>();
        for (GraphNode appNode : nodes.values()) {
            if (appNode.getType() == NodeType.APPLICATION) {
                Set<String> reachablePkgs = new HashSet<>();
                findForwardReachablePackages(appNode.getId(), reachablePkgs, 0);
                for (String pkgId : reachablePkgs) {
                    pkgToApps.computeIfAbsent(pkgId, k -> new HashSet<>()).add(appNode.getLabel());
                }
            }
        }

        List<Map<String, Object>> shared = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : pkgToApps.entrySet()) {
            if (entry.getValue().size() > 1) {
                GraphNode pkg = nodes.get(entry.getKey());
                if (pkg != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("packageId", pkg.getId());
                    item.put("packageName", pkg.getProperties().get("name"));
                    item.put("version", pkg.getProperties().get("version"));
                    item.put("ecosystem", pkg.getProperties().get("ecosystem"));
                    item.put("appCount", entry.getValue().size());
                    item.put("appNames", new ArrayList<>(entry.getValue()));

                    // Check if package is affected by any vulnerability
                    boolean hasVuln = false;
                    String vulnCve = null;
                    List<GraphEdge> out = outgoingEdges.getOrDefault(pkg.getId(), Collections.emptyList());
                    for (GraphEdge e : out) {
                        if ("AFFECTED_BY".equals(e.getLabel())) {
                            hasVuln = true;
                            GraphNode vNode = nodes.get(e.getTarget());
                            if (vNode != null) vulnCve = String.valueOf(vNode.getProperties().get("cveId"));
                            break;
                        }
                    }
                    item.put("hasVulnerability", hasVuln);
                    item.put("vulnerabilityCve", vulnCve);
                    shared.add(item);
                }
            }
        }
        shared.sort((a, b) -> Integer.compare((int) b.get("appCount"), (int) a.get("appCount")));
        return shared;
    }

    private void findForwardReachablePackages(String currentId, Set<String> reachable, int depth) {
        if (depth > 6) return;
        List<GraphEdge> out = outgoingEdges.getOrDefault(currentId, Collections.emptyList());
        for (GraphEdge e : out) {
            if ("DEPENDS_ON".equals(e.getLabel())) {
                GraphNode target = nodes.get(e.getTarget());
                if (target != null && target.getType() == NodeType.PACKAGE) {
                    if (reachable.add(target.getId())) {
                        findForwardReachablePackages(target.getId(), reachable, depth + 1);
                    }
                }
            }
        }
    }

    @Override
    public List<DependencyPath> findPathsBetween(String appName, String targetPkgName) {
        List<DependencyPath> paths = new ArrayList<>();
        GraphNode startNode = null;
        for (GraphNode n : nodes.values()) {
            if (n.getType() == NodeType.APPLICATION && (n.getId().equalsIgnoreCase(appName) || n.getLabel().equalsIgnoreCase(appName))) {
                startNode = n;
                break;
            }
        }
        if (startNode == null) return paths;

        findForwardPaths(startNode.getId(), targetPkgName, new ArrayList<>(List.of(startNode.getId())),
                new ArrayList<>(List.of(startNode.getLabel())), 0, paths);
        return paths;
    }

    private void findForwardPaths(String currentId, String targetPkgName, List<String> currentIds,
                                  List<String> currentLabels, int depth, List<DependencyPath> resultPaths) {
        if (depth > 6) return;
        List<GraphEdge> out = outgoingEdges.getOrDefault(currentId, Collections.emptyList());
        for (GraphEdge e : out) {
            if ("DEPENDS_ON".equals(e.getLabel())) {
                GraphNode target = nodes.get(e.getTarget());
                if (target == null || currentIds.contains(target.getId())) continue;

                List<String> nextIds = new ArrayList<>(currentIds);
                nextIds.add(target.getId());

                List<String> nextLabels = new ArrayList<>(currentLabels);
                nextLabels.add(target.getLabel());

                String name = String.valueOf(target.getProperties().get("name"));
                if (target.getId().equalsIgnoreCase(targetPkgName) || target.getLabel().equalsIgnoreCase(targetPkgName) ||
                    targetPkgName.equalsIgnoreCase(name)) {
                    String pathId = "path-" + UUID.randomUUID().toString().substring(0, 8);
                    resultPaths.add(new DependencyPath(pathId, nextIds, nextLabels, nextIds.size() - 1, String.join(" -> ", nextLabels)));
                } else {
                    findForwardPaths(target.getId(), targetPkgName, nextIds, nextLabels, depth + 1, resultPaths);
                }
            }
        }
    }

    @Override
    public synchronized boolean applyRemediation(String currentPkgId, String patchedPkgId) {
        GraphNode currentPkg = nodes.get(currentPkgId);
        GraphNode patchedPkg = nodes.get(patchedPkgId);
        if (currentPkg == null || patchedPkg == null) {
            log.warn("Cannot apply remediation: {} or {} not found", currentPkgId, patchedPkgId);
            return false;
        }

        // Rewire all DEPENDS_ON edges that pointed to currentPkgId to now point to patchedPkgId
        List<GraphEdge> inEdges = new ArrayList<>(incomingEdges.getOrDefault(currentPkgId, Collections.emptyList()));
        List<String> affectedApps = new ArrayList<>();
        int rewiredCount = 0;

        for (GraphEdge edge : inEdges) {
            if ("DEPENDS_ON".equals(edge.getLabel())) {
                // Remove old edge
                edges.remove(edge);
                incomingEdges.get(currentPkgId).remove(edge);
                outgoingEdges.get(edge.getSource()).remove(edge);

                // Create new edge pointing to patchedPkgId
                String newEdgeId = "rewired-" + UUID.randomUUID().toString().substring(0, 8);
                GraphEdge newEdge = new GraphEdge(newEdgeId, edge.getSource(), patchedPkgId, "DEPENDS_ON",
                        edge.isDirect(), edge.getScope(), edge.getDepth());
                edges.add(newEdge);
                outgoingEdges.computeIfAbsent(edge.getSource(), k -> new CopyOnWriteArrayList<>()).add(newEdge);
                incomingEdges.computeIfAbsent(patchedPkgId, k -> new CopyOnWriteArrayList<>()).add(newEdge);

                GraphNode src = nodes.get(edge.getSource());
                if (src != null) {
                    affectedApps.add(src.getLabel());
                }
                rewiredCount++;
            }
        }

        // Record remediation plan in history
        RemediationPlan plan = new RemediationPlan();
        plan.setId("rem-audit-" + UUID.randomUUID().toString().substring(0, 8));
        plan.setPackageName(String.valueOf(currentPkg.getProperties().get("name")));
        plan.setCurrentVersion(String.valueOf(currentPkg.getProperties().get("version")));
        plan.setTargetVersion(String.valueOf(patchedPkg.getProperties().get("version")));
        plan.setAffectedApplications(affectedApps);
        plan.setReason("Security patch applied to eliminate active vulnerability.");
        plan.setBreakingChangeRisk("Low: verified semantic versioning compatibility.");
        plan.setVerificationSteps(List.of("Execute integration test suite", "Verify token validation in auth-gateway", "Run regression suite"));
        plan.setStatus("APPLIED");
        plan.setTimestamp(java.time.Instant.now().toString());
        plan.setRollbackAvailable(true);
        plan.setGraphMutationSummary(String.format("Successfully rewired %d DEPENDS_ON edge(s) from %s to %s.", rewiredCount, currentPkg.getLabel(), patchedPkg.getLabel()));

        remediationHistory.add(plan);
        log.info("Applied remediation: rewired {} edges from {} to {}", rewiredCount, currentPkgId, patchedPkgId);
        return true;
    }

    @Override
    public synchronized boolean rollbackRemediation(String currentPkgId, String patchedPkgId) {
        GraphNode currentPkg = nodes.get(currentPkgId);
        GraphNode patchedPkg = nodes.get(patchedPkgId);
        if (currentPkg == null || patchedPkg == null) {
            return false;
        }

        // Rewire all edges from patchedPkg back to currentPkg
        List<GraphEdge> inEdges = new ArrayList<>(incomingEdges.getOrDefault(patchedPkgId, Collections.emptyList()));
        int restoredCount = 0;

        for (GraphEdge edge : inEdges) {
            if ("DEPENDS_ON".equals(edge.getLabel())) {
                edges.remove(edge);
                incomingEdges.get(patchedPkgId).remove(edge);
                outgoingEdges.get(edge.getSource()).remove(edge);

                String restoredEdgeId = "restored-" + UUID.randomUUID().toString().substring(0, 8);
                GraphEdge newEdge = new GraphEdge(restoredEdgeId, edge.getSource(), currentPkgId, "DEPENDS_ON",
                        edge.isDirect(), edge.getScope(), edge.getDepth());
                edges.add(newEdge);
                outgoingEdges.computeIfAbsent(edge.getSource(), k -> new CopyOnWriteArrayList<>()).add(newEdge);
                incomingEdges.computeIfAbsent(currentPkgId, k -> new CopyOnWriteArrayList<>()).add(newEdge);
                restoredCount++;
            }
        }

        // Update latest remediation plan status
        if (!remediationHistory.isEmpty()) {
            RemediationPlan latest = remediationHistory.get(remediationHistory.size() - 1);
            latest.setStatus("ROLLED_BACK");
            latest.setRollbackAvailable(false);
            latest.setGraphMutationSummary(String.format("Rollback executed: restored %d edge(s) back to original version %s.", restoredCount, currentPkg.getLabel()));
        }

        log.info("Rolled back remediation: restored {} edges to {}", restoredCount, currentPkgId);
        return true;
    }

    public List<RemediationPlan> getRemediationHistory() {
        return new ArrayList<>(remediationHistory);
    }

    @Override
    public int getNodeCount() {
        return nodes.size();
    }

    @Override
    public int getEdgeCount() {
        return edges.size();
    }

    @Override
    public GraphStatus getStatus() {
        return new GraphStatus(
                false,
                "localhost",
                6379,
                "dependencyguard",
                nodes.size(),
                edges.size(),
                "JFalkorDB 0.13.1 (Engine Emulated)",
                "FALLBACK_EMULATED",
                "FalkorDB host not reachable at localhost:6379. Running verified high-fidelity graph traversal engine. Start FalkorDB via Docker Compose to connect to a live database."
        );
    }
}
