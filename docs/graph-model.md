# DependencyGuard AI — Graph Data Model

## 1. Node Schema

The graph models five primary entity types:

### 1.1 `Application`
Represents an internal microservice, serverless function, or digital channel.
- `id` (String, Indexed): Unique application ID (e.g., `"app-payment-service"`).
- `name` (String, Indexed): Human-readable name (e.g., `"payment-service"`).
- `repository` (String): Git repository location (e.g., `"github.com/enterprise/payment-service"`).
- `environment` (String): Deployment environment (`"Production"`, `"Staging"`, `"Development"`).
- `criticalTier` (String): Business criticality (`"TIER_1"` [1.5× multiplier], `"TIER_2"` [1.2× multiplier], `"TIER_3"` [1.0× multiplier]).
- `language` (String): Primary runtime language (e.g., `"Java"`, `"TypeScript"`, `"Go"`).
- `framework` (String): Framework stack (e.g., `"Spring Boot 3.3.4"`).

### 1.2 `Package`
Represents a software library, SDK, or framework module.
- `id` (String, Indexed): Package coordinate (e.g., `"pkg-auth-core-1.2.0"`).
- `name` (String, Indexed): Package name (e.g., `"auth-core"`).
- `version` (String): Semantic version string (e.g., `"1.2.0"`, `"1.2.4"`).
- `ecosystem` (String): Package ecosystem (`"Maven"`, `"npm"`, `"PyPI"`, `"Go"`).
- `directOrTransitive` (String): Direct vs Transitive designation (`"DIRECT"`, `"TRANSITIVE"`).
- `isPatchedVersion` (Boolean): Flag indicating if this version is a verified security patch.

### 1.3 `Vulnerability`
Represents a public Common Vulnerabilities and Exposures (CVE) advisory.
- `id` (String, Indexed): Vulnerability ID (e.g., `"vuln-cve-2024-45321"`).
- `cveId` (String, Indexed): CVE identifier (e.g., `"CVE-2024-45321"`).
- `title` (String): Descriptive advisory title.
- `severity` (String): Severity rating (`"CRITICAL"`, `"HIGH"`, `"MEDIUM"`, `"LOW"`).
- `cvssScore` (Float): Base CVSS v3.1 score (e.g., `9.8`).
- `cvssVector` (String): Official CVSS vector string.
- `summary` (String): Technical description of vulnerability root cause and attack vector.
- `patchedVersion` (String): Verified safe remediation candidate version (e.g., `"1.2.4"`).
- `publishedDate` (String): ISO date string.

### 1.4 `License`
Represents software license terms and compliance governance.
- `id` (String, Indexed): License ID (e.g., `"lic-gpl-3.0"`).
- `spdxId` (String, Indexed): Standard SPDX identifier (e.g., `"MIT"`, `"Apache-2.0"`, `"GPL-3.0"`, `"AGPL-3.0"`).
- `name` (String): Full license title.
- `riskLevel` (String): Compliance risk tier (`"LOW"`, `"HIGH"`, `"CRITICAL"`).
- `category` (String): Category classification (`"PERMISSIVE"`, `"COPYLEFT_STRONG"`, `"NETWORK_COPYLEFT"`).
- `commercialUseAllowed` (Boolean): Flag indicating SaaS/proprietary compatibility.

### 1.5 `Team`
Represents engineering squad or platform ownership.
- `id` (String, Indexed): Team ID (e.g., `"team-payments"`).
- `name` (String): Squad name (e.g., `"Payments Squad"`).
- `leadName` (String): Engineering manager or lead.
- `contactEmail` (String): Alert notification email.
- `slackChannel` (String): Internal incident communication channel.

---

## 2. Relationships

| Relationship | Source Node | Target Node | Properties | Semantics |
|---|---|---|---|---|
| `DEPENDS_ON` | `Application` | `Package` | `scope`, `direct: true`, `depth: 1` | Direct application dependency |
| `DEPENDS_ON` | `Package` | `Package` | `scope`, `direct: false`, `depth: N` | Transitive multi-hop dependency |
| `AFFECTED_BY` | `Package` | `Vulnerability` | `active: true` | Package has known CVE vulnerability |
| `HAS_LICENSE` | `Package` | `License` | `category` | Package is distributed under license |
| `MAINTAINED_BY` | `Application` | `Team` | `role` | Team responsible for service |

---

## 3. Seeded Topological Attack Surfaces

```
[Payment Service (Tier 1)] ─────┬──▶ (direct) auth-core:1.2.0 ──▶ [CVE-2024-45321 (CRITICAL)]
                                │           │
                                │           ▼ (transitive hop 2)
                                │        jwt-verifier:3.4.0
                                │           │
                                │           ▼ (transitive hop 3)
                                │        crypto-vault:0.9.1 ───▶ [CVE-2024-21626 (HIGH)]
                                │
                                └───▶ (direct) logging-facade:2.14.0 ──▶ [CVE-2021-44228 (CRITICAL Log4j)]

[Auth Gateway (Tier 1)] ────────────▶ (direct) auth-core:1.2.0 (Shared vulnerability!)

[Customer Portal (Tier 2)] ─────┬──▶ (direct) http-client:4.2.0 ──▶ [CVE-2023-44487 (Rapid Reset)]
                                └───▶ (direct) data-exporter:1.0.3 ──▶ [GPL-3.0 Copyleft Violation]

[Inventory API (Tier 2)] ───────┬──▶ (direct) http-client:4.2.0 (Shared vulnerability!)
                                └───▶ (direct) pdf-render:0.5.2 ────▶ [AGPL-3.0 Critical Policy Violation]

[Analytics Worker (Tier 3)] ────┬──▶ (direct) logging-facade:2.14.0 (Shared vulnerability!)
                                └───▶ (direct) metrics-agent:1.8.0 ──▶ [CVE-2023-38545 (MEDIUM)]
```
