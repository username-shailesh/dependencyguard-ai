# DependencyGuard AI — Hackathon Submission Details

> **FalkorDB Graph AI Agent Hackathon Reference Document**

---

## 1. Project Title & Tagline
- **Project Title:** DependencyGuard AI
- **Tagline:** An Explainable AI Agent for Software Supply-Chain Risk Analysis powered by FalkorDB.

---

## 2. Hackathon Submission Description

### Problem Statement
Modern microservice architectures depend on hundreds of direct and transitive dependencies. When a vulnerability (e.g. Log4Shell, auth bypasses, or Rapid Reset DoS) or license violation (GPL/AGPL copyleft) is disclosed, security teams struggle to identify all indirectly affected applications, understand the true multi-hop blast radius, and safely test remediation without risking production outages.

### Solution & FalkorDB Impact
DependencyGuard AI transforms flat dependency manifests into an active property graph in FalkorDB. Using OpenCypher path traversals and GraphBLAS matrix operations, it instantly computes multi-hop blast radius, calculates a transparent Blast Radius Risk Score (BRS), and powers an AI agent that generates explainable, grounded security findings with complete evidence chains. A built-in remediation simulator re-wires graph relationships to patched versions with instant rollback capability.

---

## 3. Demo Video Plan (2–3 Minutes Script)

- **0:00 – 0:30 (The Problem)**: "Modern microservices depend on hundreds of packages. A vulnerability 3 hops deep in a transitive dependency like `crypto-vault` is invisible in standard flat dependency lists."
- **0:30 – 1:00 (Interactive Graph Canvas)**: Show the React Flow graph with `Payment Service`, `Auth Gateway`, and `Customer Portal`. Filter by node type. Highlight multi-hop dependencies.
- **1:00 – 1:30 (Vulnerability Blast Radius)**: Inspect `CVE-2024-45321` in `auth-core`. Show how it directly impacts 2 Tier-1 applications and transitively exposes `crypto-vault`.
- **1:30 – 2:00 (Explainable AI Agent)**: Ask the agent: *"Which applications are affected if auth-core is vulnerable?"*. Demonstrate the 100% grounded explanation, the multi-hop evidence breadcrumbs, and the executed OpenCypher query.
- **2:00 – 2:30 (Remediation & Instant Rollback)**: Click "Preview Remediation". Inspect the proposed upgrade to `v1.2.4`. Click "Apply Remediation" to rewire the graph edges in real time. Demonstrate instant neutralization of the vulnerability, followed by one-click rollback.
- **2:30 – 3:00 (Why FalkorDB)**: Summarize how FalkorDB's GraphBLAS matrix engine enables instant sub-millisecond multi-hop path reachability and atomic graph edge mutation.

---

## 4. Final Submission Checklist

- [x] Public source-code repository with clean structure.
- [x] README with setup instructions, architecture diagram, and graph data model.
- [x] Real OpenCypher queries implemented in `graph/queries/`.
- [x] AI agent grounded in database results with zero hallucinations.
- [x] Deterministic fallback enabled so app is fully demonstrable without paid API keys.
- [x] All 25 unit and integration test cases run and verified (100% pass rate).
- [x] Reproducible local setup via Docker Compose and PowerShell scripts.
