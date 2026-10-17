# DependencyGuard AI — System Architecture

**Tagline:** An Explainable AI Agent for Software Supply-Chain Risk Analysis.

---

## 1. Architectural Overview

DependencyGuard AI is an end-to-end, enterprise-grade software supply-chain security platform. It models application-to-package dependencies, multi-hop transitive paths, known vulnerabilities (CVEs), open-source licenses, and engineering ownership teams as a unified, connected property graph powered by **FalkorDB**.

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
|  [Grounding Adapters]                                                                   |
|    ├── DeterministicAgentEngine: 100% offline rule-based intent router (Zero-cost demo) |
|    └── ExternalLlmAdapter: Gemini / OpenAI adapter with strict schema injection         |
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

## 2. Why FalkorDB is Indispensable

Relational databases require recursive Common Table Expressions (`WITH RECURSIVE`) or expensive self-joins to trace multi-hop transitive dependencies. As software supply chains scale to thousands of nested dependencies:
1. **Multi-Hop Traversal Performance**: FalkorDB leverages GraphBLAS (sparse matrix multiplication) to compute reachability paths across arbitrarily deep dependency chains in sub-millisecond latency.
2. **Path Topology as First-Class Citizens**: FalkorDB returns Cypher `Path` objects directly (`MATCH path = (a:Application)-[:DEPENDS_ON*1..6]->(p:Package)`), providing precise breadcrumb chains for every finding.
3. **Dynamic Graph Mutation for Remediation**: Simulating remediation by rewiring dependency edges (`CREATE (a)-[:DEPENDS_ON]->(patched) DELETE oldEdge`) is an atomic, instant pointer update in FalkorDB without table locking.

---

## 3. Controlled Agent Workflow (Zero Hallucination Guarantee)

The AI Investigation Agent does not execute arbitrary Cypher or invent CVE findings:

1. **User Query Ingestion**: The agent parses user natural-language questions (e.g., *"Which applications are affected if auth-core is vulnerable?"*).
2. **Intent & Entity Extraction**: Identifies the investigation class (`AFFECTED_APPLICATIONS`, `PRIORITIZE_RISKS`, `EXPLAIN_PATH`, `REMEDIATION_ORDER`, `LICENSE_RISK`) and extracts entity tokens (e.g., `auth-core`, `CVE-2024-45321`).
3. **Approved Query Selection**: Binds parameters into predefined, injection-safe parameterized Cypher queries.
4. **FalkorDB Execution**: Executes the query against FalkorDB.
5. **Record Validation**: Strictly verifies that returned nodes, edges, and paths exist in the database.
6. **Grounded Synthesis**: Generates an explanation grounded solely in the verified graph records.
7. **Cross-Session Memory Storage**: Stores the investigation, entity IDs, risk score, and evidence paths into persistent memory, continuously re-validating time-sensitive facts against the live graph on subsequent sessions.
