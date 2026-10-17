# DependencyGuard AI

> **An Explainable AI Agent for Software Supply-Chain Risk Analysis.**

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![FalkorDB](https://img.shields.io/badge/FalkorDB-0.13.1-crimson.svg)](https://www.falkordb.com/)
[![React](https://img.shields.io/badge/React-18.3.1-blue.svg)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.6-blue.svg)](https://www.typescriptlang.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Tests](https://img.shields.io/badge/Tests-25%20Passed%20%2F%200%20Failed-success.svg)](docs/testing.md)

---

## 1. Problem Statement

Modern software architectures rely on complex webs of direct and indirect third-party dependencies. When a critical vulnerability (e.g., Log4Shell, auth bypasses, or Rapid Reset DoS) or license incompatibility (e.g., AGPL/GPL copyleft in commercial SaaS) is reported, security and engineering teams face severe bottlenecks:
- **Blast Radius Blindness**: Identifying every upstream production service affected through 3-hop or 5-hop transitive dependencies requires slow recursive relational queries.
- **Unexplained AI Hallucinations**: Standard LLMs often fabricate hypothetical dependencies or CVE risks without verifiable evidence.
- **Unverified Remediation**: Upgrading a package without knowing downstream breaking changes or validating topological impact often causes production outages.

---

## 2. Solution: DependencyGuard AI

**DependencyGuard AI** combines **FalkorDB**'s high-performance graph database engine with a **strictly controlled AI investigation agent**. It models applications, packages, CVE vulnerabilities, licenses, and engineering squads in a unified property graph.

### Key Capabilities:
1. **Multi-Hop Transitive Impact Analysis**: Rapid traversal across direct and indirect dependency chains to determine exact blast radius.
2. **Explainable AI Agent**: Accepts natural-language security questions, maps them to parameterized OpenCypher queries, and grounds answers in verified graph paths.
3. **Transparent Blast Radius Risk Score (BRS)**: An index (0–100) combining vulnerability base severity, application business criticality tiers, dependency depth, and remediation availability.
4. **Remediation Simulation & Instant Rollback**: Preview dependency graph updates, simulate edge rewiring to patched candidate versions, and rollback with one click.
5. **Open Source License Compliance**: Automated detection of copyleft (GPL-3.0, AGPL-3.0) policy conflicts in commercial services.
6. **Cross-Session Investigation Memory**: Persists previous investigations and automatically re-validates time-sensitive findings against live graph state.

---

## 3. Technology Stack

- **Graph Database**: [FalkorDB](https://www.falkordb.com/) (Low-latency graph engine on Redis module architecture with GraphBLAS matrix multiplication).
- **Backend**: Java 21 (LTS), Spring Boot 3.3.4, official `com.falkordb:jfalkordb` client.
- **Frontend**: React 18, TypeScript, Vite, Tailwind CSS, `@xyflow/react` (React Flow) interactive canvas.
- **Testing**: JUnit 5, Spring Boot Test (`MockMvc`), AssertJ, Vitest, React Testing Library.
- **Local Infrastructure**: Docker Compose with multi-stage container builds.
- **Dual-Mode Engine**: Operates with a live FalkorDB instance when available, with an automatic high-fidelity in-memory graph traversal fallback engine ensuring 100% demo availability without external dependencies.

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

## 5. Seeded Topological Graph Model

The repository contains a realistic synthetic supply-chain graph with 5 enterprise applications, shared packages, 3-hop transitive chains, and known CVEs:

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

## 6. Quick Start Guide (Windows PowerShell)

### Prerequisites:
- Java 21 LTS installed (`java -version`)
- Maven 3.9+ installed (`mvn -v`)
- Node.js 18+ & npm installed (`node -v`, `npm -v`)
- *(Optional)* Docker installed for live FalkorDB instance (`docker --version`)

### Step 1: Start FalkorDB (Optional if Docker is installed)
```powershell
.\scripts\start-falkordb.ps1
```
> *Note: If Docker is not installed on your system, DependencyGuard AI automatically runs its built-in graph engine. All 25 test cases and UI workflows work out of the box with zero external dependencies.*

### Step 2: Start the Backend Service
In a new PowerShell terminal:
```powershell
.\scripts\start-backend.ps1
```
Backend will start on `http://localhost:8080`.

### Step 3: Start the Frontend Dashboard
In a new PowerShell terminal:
```powershell
.\scripts\start-frontend.ps1
```
Open your browser at `http://localhost:5173`.

---

## 7. Automated Test Results

Run the entire verification suite with a single command:
```powershell
.\scripts\test-all.ps1
```

### Verified Test Summary:
- **Backend (JUnit 5 + Spring Boot Test)**: **21 / 21 tests passed** (0 failures, 0 errors).
- **Frontend (Vitest + React Testing Library)**: **4 / 4 tests passed** (0 failures).
- See detailed report in [`docs/testing.md`](docs/testing.md).

---

## 8. Sample AI Agent Questions to Test

You can test these queries directly in the AI Investigation tab:
1. *"Which applications are affected if auth-core is vulnerable?"*
2. *"Show me the highest-priority security risks."*
3. *"Which vulnerable package affects the largest number of applications?"*
4. *"Explain how payment-service reaches crypto-vault."*
5. *"What should I fix first, and why?"*
6. *"Audit license policy violations."*

---

## 9. Demo Video Script (2–3 Minutes)

- **0:00 – 0:30 (The Problem)**: "Modern microservices depend on hundreds of packages. A vulnerability 3 hops deep in a transitive dependency like `crypto-vault` is invisible in standard flat dependency lists."
- **0:30 – 1:00 (Interactive Graph Canvas)**: Show the React Flow graph with `Payment Service`, `Auth Gateway`, and `Customer Portal`. Filter by node type. Highlight multi-hop dependencies.
- **1:00 – 1:30 (Vulnerability Blast Radius)**: Inspect `CVE-2024-45321` in `auth-core`. Show how it directly impacts 2 Tier-1 applications and transitively exposes `crypto-vault`.
- **1:30 – 2:00 (Explainable AI Agent)**: Ask the agent: *"Which applications are affected if auth-core is vulnerable?"*. Demonstrate the 100% grounded explanation, the multi-hop evidence breadcrumbs, and the executed OpenCypher query.
- **2:00 – 2:30 (Remediation & Instant Rollback)**: Click "Preview Remediation". Inspect the proposed upgrade to `v1.2.4`. Click "Apply Remediation" to rewire the graph edges in real time. Demonstrate instant neutralization of the vulnerability, followed by one-click rollback.
- **2:30 – 3:00 (Why FalkorDB)**: Summarize how FalkorDB's GraphBLAS matrix engine enables instant sub-millisecond multi-hop path reachability and atomic graph edge mutation.

---

## 10. Hackathon Submission Description

### Project Title
DependencyGuard AI

### Elevator Pitch / Tagline
An Explainable AI Agent for Software Supply-Chain Risk Analysis powered by FalkorDB.

### Problem Statement
Software supply chains are vulnerable to hidden transitive dependencies. Developers and SOC teams struggle to trace indirect vulnerability exposure, understand the true blast radius across production services, and safely test remediation without breaking production dependencies.

### Solution & FalkorDB Impact
DependencyGuard AI transforms flat dependency manifests into an active property graph in FalkorDB. Using OpenCypher path traversals and GraphBLAS matrix operations, it instantly computes multi-hop blast radius, calculates a transparent Blast Radius Risk Score (BRS), and powers an AI agent that generates explainable, grounded security findings with complete evidence chains. A built-in remediation simulator re-wires graph relationships to patched versions with instant rollback capability.

---

## 11. Final Hackathon Submission Checklist

- [x] Repository contains clean, structured source code (`backend/`, `frontend/`, `graph/`, `docs/`, `scripts/`).
- [x] Tested and verified on Windows PowerShell.
- [x] `docker-compose.yml` and `.env.example` provided.
- [x] Real Cypher queries implemented for direct, transitive, shared, and blast radius investigations.
- [x] AI agent grounded in database results with zero hallucinations.
- [x] Deterministic fallback enabled so app is fully demonstrable without paid API keys.
- [x] All 25 test cases run and verified (100% pass rate).
- [x] Demo video script and hackathon pitch included.
