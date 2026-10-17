# DependencyGuard AI — AI Agent Workflow & Grounding Engine

## 1. Controlled Agent Lifecycle

To prevent hallucinations, prompt injection, or database corruption, DependencyGuard AI executes a strictly controlled 7-step agent loop:

```
[1. User Natural Language Question]
               │
               ▼
[2. Intent Extraction & Entity Recognition]
     - Maps to approved intents (AFFECTED_APPLICATIONS, PRIORITIZE_RISKS, etc.)
     - Extracts package/CVE identifiers using strict regex filters
               │
               ▼
[3. Parameterized Query Binding]
     - Selects pre-approved Cypher query template
     - Rejects any raw Cypher injection
               │
               ▼
[4. FalkorDB Query Execution]
     - Executes via official JFalkorDB driver (or verified fallback engine)
               │
               ▼
[5. Record Validation & Verification]
     - Ensures nodes, edges, and paths exist in verified graph state
               │
               ▼
[6. Grounded Answer Synthesis]
     - Produces natural language explanation with explicit citation of evidence paths
               │
               ▼
[7. Memory Persistence & Real-Time Graph Re-Validation]
     - Stores investigation snapshot in persistent memory
     - Continuously validates time-sensitive findings against live graph on subsequent sessions
```

---

## 2. Transparent Blast Radius Risk Score (BRS)

Unlike generic vulnerability scores (like raw CVSS), the **Blast Radius Score (BRS)** reflects actual enterprise topological exposure:

$$BRS = \min\left(100.0, \; \text{BaseSeverity} \times (1.0 + 0.35 \times N_{\text{apps}}) \times W_{\text{tier}} \times F_{\text{depth}} \times M_{\text{patch}}\right)$$

### Parameter Definitions:
1. **$\text{BaseSeverity}$**: Scaled baseline from severity category:
   - `CRITICAL`: 25.0
   - `HIGH`: 18.75
   - `MEDIUM`: 12.5
   - `LOW`: 6.25
2. **$N_{\text{apps}}$**: Total count of unique enterprise applications reaching this vulnerable package via direct or multi-hop transitive paths.
3. **$W_{\text{tier}}$**: Criticality multiplier of affected applications:
   - `TIER_1` (Core transaction/auth services): **1.5×**
   - `TIER_2` (Customer portals & inventory APIs): **1.2×**
   - `TIER_3` (Internal analytics workers): **1.0×**
4. **$F_{\text{depth}}$**: Exposure depth factor $\frac{1}{1 + 0.15 \times (\text{minDepth} - 1)}$. Direct dependencies have maximum exploitability ($F=1.0$), while deeper transitive chains have slightly cushioned exposure factors.
5. **$M_{\text{patch}}$**: Remediation availability modifier:
   - **0.95**: Verified patched version exists and is documented in graph.
   - **1.25**: Zero-day with no candidate patch available.

---

## 3. Remediation Planning & Safe Rollback

1. **Simulation Preview**:
   - The user inspects proposed patch candidates before any mutation occurs.
   - The agent computes affected upstream applications, breaking change risk, and verification checklists.
2. **Atomic Graph Edge Rewiring**:
   - Upon explicit approval, incoming `DEPENDS_ON` edges are rewired from the vulnerable package to the verified patched package version.
   - The vulnerability is instantly neutralized across all reaching applications.
3. **Rollback Snapshot**:
   - An immutable snapshot of the original graph edges is preserved in memory.
   - Clicking **"Rollback Remediation"** atomically restores original edges back to the pre-patched package version.
