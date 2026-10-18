# DependencyGuard AI

> **An Explainable AI Agent for Software Supply-Chain Risk Analysis powered by FalkorDB.**

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![FalkorDB](https://img.shields.io/badge/FalkorDB-0.13.1-crimson.svg)](https://www.falkordb.com/)
[![React](https://img.shields.io/badge/React-18.3.1-blue.svg)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.6-blue.svg)](https://www.typescriptlang.org/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-3.4-38bdf8.svg)](https://tailwindcss.com/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Tests](https://img.shields.io/badge/Tests-25%20Passed%20%2F%200%20Failed-success.svg)](docs/testing.md)

---

## 1. Overview & Problem Statement

Modern microservice architectures depend on hundreds of direct and third-party transitive packages. When a vulnerability (such as a Log4Shell RCE, JWT authentication bypass, or HTTP/2 Rapid Reset DoS) or license incompatibility (GPL/AGPL copyleft in commercial SaaS) is disclosed, engineering and security teams face severe bottlenecks:

- **Transitive Blast Radius Blindness**: Identifying every upstream production service affected through 3-hop or 5-hop transitive dependencies requires slow recursive relational queries.
- **Unverified AI Hallucinations**: Standard LLMs often fabricate hypothetical dependencies or CVE risks without verifiable evidence.
- **Risky Production Upgrades**: Blindly upgrading a package without understanding downstream breaking changes or validating topological impact often causes production outages.

**DependencyGuard AI** solves this by converting flat dependency manifests into an active property graph powered by **FalkorDB**. It provides multi-hop blast radius discovery, transparent risk scoring, a 100% grounded AI investigation agent, and a safe remediation simulation engine with instant rollback.

---

## 2. Core Capabilities

- **Multi-Hop Dependency Traversal**: Traces direct and indirect dependency chains up to 6+ hops deep to pinpoint the exact blast radius across microservices.
- **Explainable AI Agent**: Accepts natural-language security questions, maps them to parameterized OpenCypher queries, and grounds answers in verifiable FalkorDB graph paths.
- **Transparent Blast Radius Risk Score (BRS)**: An index (0–100) combining vulnerability base severity, application business criticality tiers (`TIER_1` to `TIER_3`), dependency path depth, and patch availability.
- **Remediation Simulation & Instant Rollback**: Previews dependency graph updates, simulates edge rewiring to patched candidate versions, and rolls back with a single click.
- **Open Source License Governance**: Automated detection of copyleft (GPL-3.0, AGPL-3.0) policy conflicts under Enterprise Policy `SEC-LIC-04`.
- **Cross-Session Investigation Memory**: Persists previous inquiries and automatically re-validates time-sensitive findings against live graph state.
- **Dual-Mode Engine**: Operates with a live FalkorDB instance via Redis protocol, with an automatic high-fidelity in-memory graph traversal fallback engine ensuring seamless local execution.

---

## 3. Technology Stack

- **Graph Database**: [FalkorDB](https://www.falkordb.com/) (Low-latency graph engine on Redis module architecture with GraphBLAS matrix multiplication).
- **Backend**: Java 21 (LTS), Spring Boot 3.3.4, official `com.falkordb:jfalkordb` client (`v0.13.1`).
- **Frontend**: React 18, TypeScript, Vite, Tailwind CSS, `@xyflow/react` (React Flow) interactive canvas.
- **Testing**: JUnit 5, Spring Boot Test (`MockMvc`), AssertJ, Vitest, React Testing Library.
- **Infrastructure**: Docker Compose, Multi-stage Dockerfiles, Nginx Alpine reverse proxy.

---

## 4. System Architecture

```
+-----------------------------------------------------------------------------------------+
|                                REACT FRONTEND DASHBOARD                                 |
|  - React 18 + Vite + TypeScript + Tailwind CSS                                          |
|  - @xyflow/react Interactive Canvas (Multi-Hop Path Highlighting)                       |
|  - Conversational AI Agent Panel with Predefined Investigation Prompt Chips            |
|  - Remediation Simulation Center (Preview, Apply Graph Mutation, Instant Rollback)      |
|  - Open Source License Auditor (GPL/AGPL Copyleft Governance)                           |
|  - Cross-Session Investigation Memory Explorer                                          |
+--------------------------------------------+--------------------------------------------+
                                             | REST / JSON APIs (HTTP :8080)
                                             v
+-----------------------------------------------------------------------------------------+
|                             SPRING BOOT 3.3.4 BACKEND                                   |
|                                                                                         |
|  [REST Controllers]                                                                     |
|    ├── GraphController (/api/graph/*)                                                   |
|    ├── VulnerabilityController (/api/vulnerabilities/*)                                 |
|    ├── AgentController (/api/agent/*)                                                   |
|    ├── RemediationController (/api/remediation/*)                                       |
|    └── LicenseController (/api/licenses/*)                                              |
|                                                                                         |
|  [Orchestration & Business Logic Services]                                              |
|    ├── AgentOrchestrationService: Controlled 7-step investigation pipeline              |
|    ├── RiskAnalysisService: Blast Radius Risk Score (BRS) engine                        |
|    ├── RemediationService: Graph edge rewiring simulation & rollback snapshots          |
|    ├── LicenseAnalysisService: SEC-LIC-04 copyleft compliance auditor                   |
|    └── InvestigationMemoryService: Persistent memory with real-time graph re-validation |
|                                                                                         |
|  [Graph Data Access Layer]                                                              |
|    ├── GraphService: Central router with automatic live/fallback detection              |
|    ├── FalkorDbGraphRepository: Native JFalkorDB 0.13.1 client for live FalkorDB        |
|    └── InMemoryGraphEngine: High-fidelity topological traversal engine (Local fallback) |
+--------------------------------------------+--------------------------------------------+
                                             | Redis Protocol / OpenCypher (:6379)
                                             v
+-----------------------------------------------------------------------------------------+
|                                   FALKORDB ENGINE                                       |
|  - Low-latency Graph Database on Redis Module Architecture                              |
|  - Linearized Graph Storage & Sparse Matrix Graph BLAS Processing                       |
|  - OpenCypher Graph Query Execution (GRAPH.QUERY dependencyguard "...")                 |
+-----------------------------------------------------------------------------------------+
```

---

## 5. Seeded Graph Model & Topological Attack Surface

The platform includes a realistic synthetic enterprise supply-chain graph with 5 services, shared packages, 3-hop transitive chains, and known CVEs:

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

---

## 6. Quick Start Guide

### Prerequisites
- **Java 21 LTS** installed (`java -version`)
- **Node.js 18+ & npm** installed (`node -v`, `npm -v`)
- *(Optional)* Docker Desktop installed if running a live FalkorDB instance

---

### Option A: Local Run (Fastest)

#### 1. Start the Backend Service
In your first terminal:
```powershell
# From project root:
.\scripts\start-backend.ps1

# Or run directly with Java:
java -jar backend/target/backend-1.0.0-SNAPSHOT.jar
```
Backend will start on `http://localhost:8080`.

#### 2. Start the Frontend Dashboard
In your second terminal:
```powershell
# From project root:
.\scripts\start-frontend.ps1

# Or run directly:
cd frontend
npm run dev
```
Open **`http://localhost:5173`** in your browser.

---

### Option B: Docker Compose (Single Command)

If Docker Desktop is running:
```bash
docker compose up --build
```
- **Frontend Dashboard:** `http://localhost:5173`
- **Backend API:** `http://localhost:8080`
- **FalkorDB Engine:** `localhost:6379`

---

## 7. Sample AI Agent Queries

You can test these natural language queries directly in the **AI Investigation Agent** tab:

1. *"Which applications are affected if auth-core is vulnerable?"*
2. *"Show me the highest-priority security risks."*
3. *"Which vulnerable package affects the largest number of applications?"*
4. *"Explain how payment-service reaches crypto-vault."*
5. *"What should I fix first, and why?"*
6. *"Audit license policy violations."*

Each response displays the exact OpenCypher query executed, verified evidence breadcrumbs, and a recommended remediation action.

---

## 8. REST API Reference

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/graph/overview` | High-level metrics (apps, packages, CVEs, BRS index) |
| `GET` | `/api/graph/data` | Complete graph topology (nodes and edges) for canvas rendering |
| `GET` | `/api/graph/status` | FalkorDB connection status and client library details |
| `POST` | `/api/graph/seed` | Reset and re-seed the graph with sample dataset |
| `GET` | `/api/vulnerabilities` | Prioritized list of vulnerabilities ranked by Blast Radius Score |
| `GET` | `/api/vulnerabilities/{cveId}/blast-radius` | Blast radius breakdown and multi-hop paths for a specific CVE |
| `POST` | `/api/agent/investigate` | Submit natural-language question to the AI Agent |
| `GET` | `/api/remediation/preview/{cveId}` | Preview graph mutation impact and breaking changes |
| `POST` | `/api/remediation/apply/{cveId}` | Atomically rewire graph edges to patched package version |
| `POST` | `/api/remediation/rollback/{cveId}` | Restore original graph edges from snapshot |
| `GET` | `/api/licenses/violations` | Copyleft and high-risk license compliance audit |
| `GET` | `/api/agent/history` | Cross-session investigation history with live graph re-validation |

---

## 9. Automated Testing & Verification

The project includes unit and integration tests across both backend and frontend layers:

```powershell
# Run the complete test suite:
powershell.exe -ExecutionPolicy Bypass -File ".\scripts\test-all.ps1"
```

### Verified Test Summary
- **Backend (JUnit 5 + Spring Boot Test)**: **21 / 21 tests passed** (0 failures, 0 errors).
- **Frontend (Vitest + React Testing Library)**: **4 / 4 tests passed** (0 failures).
- **Total**: **25 / 25 automated tests passing (100%)**.

Refer to [`docs/testing.md`](docs/testing.md) for the detailed test execution log.

---

## 10. Project Directory Structure

```
├── backend/                       # Spring Boot 3.3.4 (Java 21) backend service
│   ├── src/main/java/             # Controllers, Services, Repositories, Models
│   ├── src/main/resources/        # application.yml, seed.cypher
│   └── src/test/java/             # 21 JUnit 5 & MockMvc test cases
├── frontend/                      # React 18 + TypeScript + Vite frontend
│   ├── src/components/            # Graph canvas, panels, modals, chat
│   ├── src/services/              # Typed REST client
│   └── src/test/                  # 4 Vitest / React Testing Library tests
├── graph/                         # Graph definitions
│   ├── schema.cypher              # FalkorDB index definitions
│   ├── seed.cypher                # Synthetic supply-chain graph dataset
│   └── queries/                   # Reusable OpenCypher query library
├── docs/                          # Technical documentation
│   ├── architecture.md            # Architecture and FalkorDB role
│   ├── graph-model.md             # Entity relationships and schemas
│   ├── agent-workflow.md          # 7-step agent loop & BRS formula
│   ├── testing.md                 # Test execution report
│   └── hackathon-submission.md    # Hackathon video script & pitch details
├── scripts/                       # PowerShell automation scripts
├── docker-compose.yml             # Container orchestration
└── LICENSE                        # Apache 2.0 License
```

---

## 11. Documentation Links

- [System Architecture](docs/architecture.md)
- [Graph Data Model](docs/graph-model.md)
- [Agent Workflow & Risk Scoring](docs/agent-workflow.md)
- [Test Report](docs/testing.md)
- [Hackathon Submission Details](docs/hackathon-submission.md)

---

## 12. License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
