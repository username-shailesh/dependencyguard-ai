// ==============================================================================
// DependencyGuard AI - Synthetic Seed Graph Data
// Realistic software supply-chain graph with multi-hop transitive dependencies,
// shared dependencies, known CVE vulnerabilities, and license policy risks.
// ==============================================================================

// ------------------------------------------------------------------------------
// 1. Teams
// ------------------------------------------------------------------------------
CREATE (teamSec:Team {
    id: "team-sec",
    name: "Platform Security",
    leadName: "Elena Rostova",
    contactEmail: "security-ops@enterprise.internal",
    slackChannel: "#sec-ops"
});

CREATE (teamPayments:Team {
    id: "team-payments",
    name: "Payments Squad",
    leadName: "Marcus Chen",
    contactEmail: "payments-eng@enterprise.internal",
    slackChannel: "#payments-eng"
});

CREATE (teamIdentity:Team {
    id: "team-identity",
    name: "Identity & Access Squad",
    leadName: "Sarah Jenkins",
    contactEmail: "identity-eng@enterprise.internal",
    slackChannel: "#auth-team"
});

CREATE (teamData:Team {
    id: "team-data",
    name: "Data Platform Team",
    leadName: "Devon Brooks",
    contactEmail: "data-eng@enterprise.internal",
    slackChannel: "#data-workers"
});

CREATE (teamFrontend:Team {
    id: "team-frontend",
    name: "Digital Channels Squad",
    leadName: "Aaliyah Patel",
    contactEmail: "channels-eng@enterprise.internal",
    slackChannel: "#portal-web"
});

// ------------------------------------------------------------------------------
// 2. Applications
// ------------------------------------------------------------------------------
CREATE (appPayment:Application {
    id: "app-payment-service",
    name: "payment-service",
    repository: "github.com/enterprise/payment-service",
    environment: "Production",
    criticalTier: "TIER_1",
    language: "Java",
    framework: "Spring Boot 3.3.4"
});

CREATE (appAuth:Application {
    id: "app-auth-gateway",
    name: "auth-gateway",
    repository: "github.com/enterprise/auth-gateway",
    environment: "Production",
    criticalTier: "TIER_1",
    language: "Java",
    framework: "Spring Boot 3.3.4"
});

CREATE (appPortal:Application {
    id: "app-customer-portal",
    name: "customer-portal",
    repository: "github.com/enterprise/customer-portal",
    environment: "Production",
    criticalTier: "TIER_2",
    language: "TypeScript",
    framework: "React 18"
});

CREATE (appAnalytics:Application {
    id: "app-analytics-worker",
    name: "analytics-worker",
    repository: "github.com/enterprise/analytics-worker",
    environment: "Staging",
    criticalTier: "TIER_3",
    language: "Python",
    framework: "FastAPI"
});

CREATE (appInventory:Application {
    id: "app-inventory-api",
    name: "inventory-api",
    repository: "github.com/enterprise/inventory-api",
    environment: "Production",
    criticalTier: "TIER_2",
    language: "Go",
    framework: "Gin"
});

// ------------------------------------------------------------------------------
// 3. Application Maintenance Relationships
// ------------------------------------------------------------------------------
CREATE (appPayment)-[:MAINTAINED_BY]->(teamPayments);
CREATE (appAuth)-[:MAINTAINED_BY]->(teamIdentity);
CREATE (appPortal)-[:MAINTAINED_BY]->(teamFrontend);
CREATE (appAnalytics)-[:MAINTAINED_BY]->(teamData);
CREATE (appInventory)-[:MAINTAINED_BY]->(teamSec);

// ------------------------------------------------------------------------------
// 4. Licenses
// ------------------------------------------------------------------------------
CREATE (licMit:License {
    id: "lic-mit",
    spdxId: "MIT",
    name: "MIT License",
    riskLevel: "LOW",
    category: "PERMISSIVE",
    commercialUseAllowed: true
});

CREATE (licApache:License {
    id: "lic-apache-2.0",
    spdxId: "Apache-2.0",
    name: "Apache License 2.0",
    riskLevel: "LOW",
    category: "PERMISSIVE",
    commercialUseAllowed: true
});

CREATE (licBsd3:License {
    id: "lic-bsd-3-clause",
    spdxId: "BSD-3-Clause",
    name: "BSD 3-Clause License",
    riskLevel: "LOW",
    category: "PERMISSIVE",
    commercialUseAllowed: true
});

CREATE (licGpl3:License {
    id: "lic-gpl-3.0",
    spdxId: "GPL-3.0",
    name: "GNU General Public License v3.0",
    riskLevel: "HIGH",
    category: "COPYLEFT_STRONG",
    commercialUseAllowed: false
});

CREATE (licAgpl3:License {
    id: "lic-agpl-3.0",
    spdxId: "AGPL-3.0",
    name: "GNU Affero General Public License v3.0",
    riskLevel: "CRITICAL",
    category: "NETWORK_COPYLEFT",
    commercialUseAllowed: false
});

// ------------------------------------------------------------------------------
// 5. Packages (Vulnerable and Safe Versions)
// ------------------------------------------------------------------------------
CREATE (pkgAuthCoreOld:Package {
    id: "pkg-auth-core-1.2.0",
    name: "auth-core",
    version: "1.2.0",
    ecosystem: "Maven",
    directOrTransitive: "DIRECT",
    isPatchedVersion: false
});

CREATE (pkgAuthCorePatched:Package {
    id: "pkg-auth-core-1.2.4",
    name: "auth-core",
    version: "1.2.4",
    ecosystem: "Maven",
    directOrTransitive: "DIRECT",
    isPatchedVersion: true
});

CREATE (pkgJwtVerifier:Package {
    id: "pkg-jwt-verifier-3.4.0",
    name: "jwt-verifier",
    version: "3.4.0",
    ecosystem: "Maven",
    directOrTransitive: "TRANSITIVE",
    isPatchedVersion: false
});

CREATE (pkgCryptoVaultOld:Package {
    id: "pkg-crypto-vault-0.9.1",
    name: "crypto-vault",
    version: "0.9.1",
    ecosystem: "Maven",
    directOrTransitive: "TRANSITIVE",
    isPatchedVersion: false
});

CREATE (pkgCryptoVaultPatched:Package {
    id: "pkg-crypto-vault-0.9.5",
    name: "crypto-vault",
    version: "0.9.5",
    ecosystem: "Maven",
    directOrTransitive: "TRANSITIVE",
    isPatchedVersion: true
});

CREATE (pkgLoggingFacadeOld:Package {
    id: "pkg-logging-facade-2.14.0",
    name: "logging-facade",
    version: "2.14.0",
    ecosystem: "Maven",
    directOrTransitive: "DIRECT",
    isPatchedVersion: false
});

CREATE (pkgLoggingFacadePatched:Package {
    id: "pkg-logging-facade-2.17.1",
    name: "logging-facade",
    version: "2.17.1",
    ecosystem: "Maven",
    directOrTransitive: "DIRECT",
    isPatchedVersion: true
});

CREATE (pkgHttpClientOld:Package {
    id: "pkg-http-client-4.2.0",
    name: "http-client",
    version: "4.2.0",
    ecosystem: "npm",
    directOrTransitive: "DIRECT",
    isPatchedVersion: false
});

CREATE (pkgHttpClientPatched:Package {
    id: "pkg-http-client-4.2.2",
    name: "http-client",
    version: "4.2.2",
    ecosystem: "npm",
    directOrTransitive: "DIRECT",
    isPatchedVersion: true
});

CREATE (pkgDataExporter:Package {
    id: "pkg-data-exporter-1.0.3",
    name: "data-exporter",
    version: "1.0.3",
    ecosystem: "npm",
    directOrTransitive: "DIRECT",
    isPatchedVersion: false
});

CREATE (pkgPdfRender:Package {
    id: "pkg-pdf-render-0.5.2",
    name: "pdf-render",
    version: "0.5.2",
    ecosystem: "Go",
    directOrTransitive: "DIRECT",
    isPatchedVersion: false
});

CREATE (pkgMetricsAgent:Package {
    id: "pkg-metrics-agent-1.8.0",
    name: "metrics-agent",
    version: "1.8.0",
    ecosystem: "PyPI",
    directOrTransitive: "DIRECT",
    isPatchedVersion: false
});

CREATE (pkgCacheLayer:Package {
    id: "pkg-cache-layer-2.0.1",
    name: "cache-layer",
    version: "2.0.1",
    ecosystem: "Maven",
    directOrTransitive: "DIRECT",
    isPatchedVersion: false
});

// ------------------------------------------------------------------------------
// 6. Package Licenses
// ------------------------------------------------------------------------------
CREATE (pkgAuthCoreOld)-[:HAS_LICENSE]->(licApache);
CREATE (pkgAuthCorePatched)-[:HAS_LICENSE]->(licApache);
CREATE (pkgJwtVerifier)-[:HAS_LICENSE]->(licMit);
CREATE (pkgCryptoVaultOld)-[:HAS_LICENSE]->(licBsd3);
CREATE (pkgCryptoVaultPatched)-[:HAS_LICENSE]->(licBsd3);
CREATE (pkgLoggingFacadeOld)-[:HAS_LICENSE]->(licApache);
CREATE (pkgLoggingFacadePatched)-[:HAS_LICENSE]->(licApache);
CREATE (pkgHttpClientOld)-[:HAS_LICENSE]->(licMit);
CREATE (pkgHttpClientPatched)-[:HAS_LICENSE]->(licMit);
CREATE (pkgCacheLayer)-[:HAS_LICENSE]->(licMit);
CREATE (pkgMetricsAgent)-[:HAS_LICENSE]->(licApache);
CREATE (pkgDataExporter)-[:HAS_LICENSE]->(licGpl3);
CREATE (pkgPdfRender)-[:HAS_LICENSE]->(licAgpl3);

// ------------------------------------------------------------------------------
// 7. Known Vulnerabilities (CVEs)
// ------------------------------------------------------------------------------
CREATE (vulnAuthBypass:Vulnerability {
    id: "vuln-cve-2024-45321",
    cveId: "CVE-2024-45321",
    title: "Authentication Token Bypass in auth-core",
    severity: "CRITICAL",
    cvssScore: 9.8,
    cvssVector: "CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H",
    summary: "Improper verification of JWT algorithm signatures allows forging arbitrary session tokens.",
    patchedVersion: "1.2.4",
    publishedDate: "2024-04-12"
});

CREATE (vulnCryptoLeak:Vulnerability {
    id: "vuln-cve-2024-21626",
    cveId: "CVE-2024-21626",
    title: "Side-Channel Key Recovery in crypto-vault",
    severity: "HIGH",
    cvssScore: 8.1,
    cvssVector: "CVSS:3.1/AV:N/AC:H/PR:N/UI:N/S:U/C:H/I:H/A:N",
    summary: "Non-constant time modular exponentiation reveals private session keys under high throughput.",
    patchedVersion: "0.9.5",
    publishedDate: "2024-01-30"
});

CREATE (vulnLog4Shell:Vulnerability {
    id: "vuln-cve-2021-44228",
    cveId: "CVE-2021-44228",
    title: "Remote Code Execution in logging-facade JNDI Lookups",
    severity: "CRITICAL",
    cvssScore: 10.0,
    cvssVector: "CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:C/C:H/I:H/A:H",
    summary: "Arbitrary JNDI message lookup parsing executes untrusted remote bytecode via LDAP payloads.",
    patchedVersion: "2.17.1",
    publishedDate: "2021-12-10"
});

CREATE (vulnRapidReset:Vulnerability {
    id: "vuln-cve-2023-44487",
    cveId: "CVE-2023-44487",
    title: "HTTP/2 Rapid Reset Denial of Service in http-client",
    severity: "HIGH",
    cvssScore: 7.5,
    cvssVector: "CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:N/I:N/A:H",
    summary: "RST_STREAM frame storm exhausts server worker threads without generating log events.",
    patchedVersion: "4.2.2",
    publishedDate: "2023-10-10"
});

CREATE (vulnSocksBuffer:Vulnerability {
    id: "vuln-cve-2023-38545",
    cveId: "CVE-2023-38545",
    title: "Heap Buffer Overflow in metrics-agent Transport",
    severity: "MEDIUM",
    cvssScore: 5.8,
    cvssVector: "CVSS:3.1/AV:N/AC:H/PR:N/UI:R/S:U/C:L/I:L/A:L",
    summary: "SOCKS5 hostname resolution causes out-of-bounds heap write during slow network negotiation.",
    patchedVersion: "1.8.2",
    publishedDate: "2023-10-11"
});

// ------------------------------------------------------------------------------
// 8. Package Affected_By Relationships
// ------------------------------------------------------------------------------
CREATE (pkgAuthCoreOld)-[:AFFECTED_BY]->(vulnAuthBypass);
CREATE (pkgCryptoVaultOld)-[:AFFECTED_BY]->(vulnCryptoLeak);
CREATE (pkgLoggingFacadeOld)-[:AFFECTED_BY]->(vulnLog4Shell);
CREATE (pkgHttpClientOld)-[:AFFECTED_BY]->(vulnRapidReset);
CREATE (pkgMetricsAgent)-[:AFFECTED_BY]->(vulnSocksBuffer);

// ------------------------------------------------------------------------------
// 9. Dependency Relationships (Direct & Transitive Multi-Hop)
// ------------------------------------------------------------------------------

// Direct dependencies from payment-service
CREATE (appPayment)-[:DEPENDS_ON {scope: "compile", direct: true, depth: 1}]->(pkgAuthCoreOld);
CREATE (appPayment)-[:DEPENDS_ON {scope: "compile", direct: true, depth: 1}]->(pkgLoggingFacadeOld);
CREATE (appPayment)-[:DEPENDS_ON {scope: "runtime", direct: true, depth: 1}]->(pkgCacheLayer);

// Direct dependencies from auth-gateway
CREATE (appAuth)-[:DEPENDS_ON {scope: "compile", direct: true, depth: 1}]->(pkgAuthCoreOld);
CREATE (appAuth)-[:DEPENDS_ON {scope: "runtime", direct: true, depth: 1}]->(pkgCacheLayer);

// Transitive chain from auth-core -> jwt-verifier -> crypto-vault
CREATE (pkgAuthCoreOld)-[:DEPENDS_ON {scope: "compile", direct: false, depth: 2}]->(pkgJwtVerifier);
CREATE (pkgAuthCorePatched)-[:DEPENDS_ON {scope: "compile", direct: false, depth: 2}]->(pkgJwtVerifier);
CREATE (pkgJwtVerifier)-[:DEPENDS_ON {scope: "runtime", direct: false, depth: 3}]->(pkgCryptoVaultOld);

// Direct dependencies from customer-portal
CREATE (appPortal)-[:DEPENDS_ON {scope: "dependencies", direct: true, depth: 1}]->(pkgHttpClientOld);
CREATE (appPortal)-[:DEPENDS_ON {scope: "dependencies", direct: true, depth: 1}]->(pkgDataExporter);

// Direct dependencies from inventory-api
CREATE (appInventory)-[:DEPENDS_ON {scope: "require", direct: true, depth: 1}]->(pkgHttpClientOld);
CREATE (appInventory)-[:DEPENDS_ON {scope: "require", direct: true, depth: 1}]->(pkgPdfRender);

// Direct dependencies from analytics-worker
CREATE (appAnalytics)-[:DEPENDS_ON {scope: "install_requires", direct: true, depth: 1}]->(pkgMetricsAgent);
CREATE (appAnalytics)-[:DEPENDS_ON {scope: "install_requires", direct: true, depth: 1}]->(pkgLoggingFacadeOld);
