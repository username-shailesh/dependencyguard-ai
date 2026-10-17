# DependencyGuard AI — Test Execution & Quality Report

## 1. Test Summary

All test suites execute deterministically without external network or paid API dependencies:

| Component | Framework | Total Tests | Passed | Failed | Status |
|---|---|---|---|---|---|
| Backend Domain & Traversal | JUnit 5 | 4 | 4 | 0 | **PASSED** |
| Vulnerability Blast Radius | JUnit 5 | 3 | 3 | 0 | **PASSED** |
| Remediation & Rollback | JUnit 5 | 2 | 2 | 0 | **PASSED** |
| AI Agent Controlled Workflow | JUnit 5 | 6 | 6 | 0 | **PASSED** |
| REST API End-to-End | Spring Boot MockMvc | 6 | 6 | 0 | **PASSED** |
| **Backend Total** | **Maven / Surefire** | **21** | **21** | **0** | **100% PASSED** |
| **Frontend Total** | **Vitest / Testing Library** | **4** | **4** | **0** | **100% PASSED** |

---

## 2. Running Backend Tests

In Windows PowerShell:

```powershell
cd backend
mvn test
```

### Verified Test Cases:
1. `GraphRepositoryTest.testGraphInitialization`: Validates seeded graph structure (applications, packages, vulnerabilities, licenses, teams).
2. `GraphRepositoryTest.testMultiHopTraversal`: Validates multi-hop path tracing from `payment-service` to `crypto-vault` through `auth-core` and `jwt-verifier`.
3. `GraphRepositoryTest.testSharedDependencies`: Validates detection of packages depended on by multiple applications (`auth-core`, `http-client`).
4. `GraphRepositoryTest.testLicenseViolations`: Validates detection of copyleft licenses (GPL-3.0, AGPL-3.0).
5. `VulnerabilityBlastRadiusTest.testAuthCoreBlastRadius`: Validates exact affected apps count and Blast Radius Risk Score (BRS) calculation for `CVE-2024-45321`.
6. `VulnerabilityBlastRadiusTest.testTransitiveCryptoVaultBlastRadius`: Validates multi-hop transitive vulnerability reachability.
7. `VulnerabilityBlastRadiusTest.testNonexistentVulnerability`: Ensures safe null handling for missing packages/CVEs without throwing exceptions.
8. `RemediationServiceTest.testPreviewRemediation`: Validates remediation plan generation before graph mutation.
9. `RemediationServiceTest.testApplyAndRollbackRemediation`: Validates atomic edge rewiring, active vulnerability neutralization, and subsequent rollback restoration.
10. `AgentOrchestrationServiceTest.testQueryAffectedApplications`: Validates natural-language intent mapping for affected applications.
11. `AgentOrchestrationServiceTest.testQueryPrioritizeRisks`: Validates ranking of active vulnerabilities by risk score.
12. `AgentOrchestrationServiceTest.testQueryMostWidespread`: Validates identification of top blast-radius package.
13. `AgentOrchestrationServiceTest.testQueryExplainPath`: Validates multi-hop dependency path explanation.
14. `AgentOrchestrationServiceTest.testQueryWhatToFixFirst`: Validates prioritized remediation roadmap advice.
15. `AgentOrchestrationServiceTest.testQueryUnknown`: Validates safe handling of unsupported/unknown questions without hallucinating data.
16. `ApiControllerIntegrationTest.testGetOverview`: Validates `GET /api/graph/overview` endpoint and JSON metrics structure.
17. `ApiControllerIntegrationTest.testGetGraphData`: Validates `GET /api/graph/data` returning full nodes and edges.
18. `ApiControllerIntegrationTest.testGetVulnerabilities`: Validates `GET /api/vulnerabilities` returning ranked findings.
19. `ApiControllerIntegrationTest.testGetBlastRadius`: Validates `GET /api/vulnerabilities/{cveId}/blast-radius`.
20. `ApiControllerIntegrationTest.testAgentInvestigate`: Validates `POST /api/agent/investigate`.
21. `ApiControllerIntegrationTest.testGetLicenseViolations`: Validates `GET /api/licenses/violations`.

---

## 3. Running Frontend Tests

In Windows PowerShell:

```powershell
cd frontend
npm test
```

### Verified Test Cases:
1. `Header Rendering`: Verifies branding, status indicator, and environment banners.
2. `MetricsCards Verification`: Verifies overview metric computations and click-to-open formula handler.
3. `VulnerabilityPanel Action`: Verifies vulnerability listing and "Preview Remediation" modal trigger.
4. `LicenseAuditPanel Compliance`: Verifies copyleft flags, policy rule citation, and governance disclaimers.

---

## 4. Running Unified Test Suite

Run the automated script:

```powershell
.\scripts\test-all.ps1
```
