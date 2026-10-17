export type NodeType = 'APPLICATION' | 'PACKAGE' | 'VULNERABILITY' | 'LICENSE' | 'TEAM';
export type Severity = 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW';
export type CriticalTier = 'TIER_1' | 'TIER_2' | 'TIER_3';

export interface GraphNode {
  id: string;
  label: string;
  title: string;
  subtitle: string;
  type: NodeType;
  properties: Record<string, any>;
}

export interface GraphEdge {
  id: string;
  source: string;
  target: string;
  label: string;
  direct: boolean;
  scope: string;
  depth: number;
}

export interface GraphData {
  nodes: GraphNode[];
  edges: GraphEdge[];
}

export interface DependencyPath {
  pathId: string;
  nodeIds: string[];
  nodeLabels: string[];
  depth: number;
  formattedPath: string;
}

export interface AffectedAppInfo {
  appId: string;
  appName: string;
  environment: string;
  criticalTier: CriticalTier;
  depth: number;
  direct: boolean;
  pathString: string;
}

export interface RiskScoreBreakdown {
  baseSeverityScore: number;
  affectedAppsCount: number;
  tierWeightedMultiplier: number;
  exposureDepthFactor: number;
  remediationAvailabilityFactor: number;
  finalScore: number;
  formulaExplanation: string;
}

export interface VulnerabilityFinding {
  id: string;
  cveId: string;
  title: string;
  severity: Severity;
  cvssScore: number;
  cvssVector: string;
  summary: string;
  packageName: string;
  currentVersion: string;
  patchedVersion: string;
  affectedApplications: AffectedAppInfo[];
  paths: DependencyPath[];
  blastRadiusScore: number;
  riskBreakdown?: RiskScoreBreakdown;
  remediationPlan: string;
  publishedDate: string;
}

export interface LicenseViolation {
  id: string;
  appId: string;
  appName: string;
  environment: string;
  packageName: string;
  packageVersion: string;
  spdxId: string;
  riskLevel: string;
  category: string;
  commercialUseAllowed: boolean;
  pathDescription: string;
  policyRule: string;
  recommendation: string;
  depth: number;
}

export interface RemediationPlan {
  id: string;
  findingId?: string;
  packageName: string;
  currentVersion: string;
  targetVersion: string;
  affectedApplications: string[];
  reason: string;
  breakingChangeRisk: string;
  verificationSteps: string[];
  status: 'PROPOSED' | 'APPLIED' | 'ROLLED_BACK';
  timestamp: string;
  rollbackAvailable: boolean;
  graphMutationSummary: string;
}

export interface InvestigationRecord {
  id: string;
  timestamp: string;
  userQuery: string;
  intent: string;
  entityIds: string[];
  findingsCount: number;
  summary: string;
  groundedEvidence: string[];
  riskScore: number;
  activeInGraph: boolean;
}

export interface AgentResponse {
  query: string;
  intent: string;
  answer: string;
  grounded: boolean;
  evidencePaths: DependencyPath[];
  findings: VulnerabilityFinding[];
  suggestedRemediation?: RemediationPlan;
  cypherQueryUsed?: string;
  executionTimeMs: number;
  dataSource: string;
  connectedToFalkorDb: boolean;
}

export interface OverviewMetrics {
  totalApplications: number;
  totalPackages: number;
  totalVulnerabilities: number;
  criticalVulnerabilities: number;
  highRiskPaths: number;
  licenseViolations: number;
  averageRiskScore: number;
  falkorDbConnected: boolean;
  dataSource: string;
}

export interface GraphStatus {
  connected: boolean;
  host: string;
  port: number;
  graphName: string;
  nodeCount: number;
  edgeCount: number;
  clientLibrary: string;
  mode: string;
  message: string;
}
