import {
  AgentResponse,
  GraphData,
  GraphStatus,
  InvestigationRecord,
  LicenseViolation,
  OverviewMetrics,
  RemediationPlan,
  VulnerabilityFinding,
} from '../types';

const API_BASE = '/api';

export async function fetchOverviewMetrics(): Promise<OverviewMetrics> {
  const res = await fetch(`${API_BASE}/graph/overview`);
  if (!res.ok) throw new Error(`HTTP error ${res.status}`);
  return res.json();
}

export async function fetchGraphData(): Promise<GraphData> {
  const res = await fetch(`${API_BASE}/graph/data`);
  if (!res.ok) throw new Error(`HTTP error ${res.status}`);
  return res.json();
}

export async function fetchGraphStatus(): Promise<GraphStatus> {
  const res = await fetch(`${API_BASE}/graph/status`);
  if (!res.ok) throw new Error(`HTTP error ${res.status}`);
  return res.json();
}

export async function seedGraph(): Promise<{ status: string; message: string; nodes: number; edges: number }> {
  const res = await fetch(`${API_BASE}/graph/seed`, { method: 'POST' });
  if (!res.ok) throw new Error(`HTTP error ${res.status}`);
  return res.json();
}

export async function fetchSharedDependencies(): Promise<any[]> {
  const res = await fetch(`${API_BASE}/graph/shared`);
  if (!res.ok) throw new Error(`HTTP error ${res.status}`);
  return res.json();
}

export async function fetchVulnerabilities(): Promise<VulnerabilityFinding[]> {
  const res = await fetch(`${API_BASE}/vulnerabilities`);
  if (!res.ok) throw new Error(`HTTP error ${res.status}`);
  return res.json();
}

export async function fetchBlastRadius(cveId: string): Promise<VulnerabilityFinding> {
  const res = await fetch(`${API_BASE}/vulnerabilities/${encodeURIComponent(cveId)}/blast-radius`);
  if (!res.ok) throw new Error(`HTTP error ${res.status}`);
  return res.json();
}

export async function fetchFormulaDoc(): Promise<{ formula: string }> {
  const res = await fetch(`${API_BASE}/vulnerabilities/formula`);
  if (!res.ok) throw new Error(`HTTP error ${res.status}`);
  return res.json();
}

export async function investigateAgent(query: string): Promise<AgentResponse> {
  const res = await fetch(`${API_BASE}/agent/investigate`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query }),
  });
  if (!res.ok) throw new Error(`HTTP error ${res.status}`);
  return res.json();
}

export async function fetchInvestigationHistory(): Promise<InvestigationRecord[]> {
  const res = await fetch(`${API_BASE}/agent/history`);
  if (!res.ok) throw new Error(`HTTP error ${res.status}`);
  return res.json();
}

export async function previewRemediation(cveId: string): Promise<RemediationPlan> {
  const res = await fetch(`${API_BASE}/remediation/preview/${encodeURIComponent(cveId)}`);
  if (!res.ok) throw new Error(`HTTP error ${res.status}`);
  return res.json();
}

export async function applyRemediation(cveId: string): Promise<RemediationPlan> {
  const res = await fetch(`${API_BASE}/remediation/apply/${encodeURIComponent(cveId)}`, { method: 'POST' });
  if (!res.ok) throw new Error(`HTTP error ${res.status}`);
  return res.json();
}

export async function rollbackRemediation(cveId: string): Promise<RemediationPlan> {
  const res = await fetch(`${API_BASE}/remediation/rollback/${encodeURIComponent(cveId)}`, { method: 'POST' });
  if (!res.ok) throw new Error(`HTTP error ${res.status}`);
  return res.json();
}

export async function fetchRemediationHistory(): Promise<RemediationPlan[]> {
  const res = await fetch(`${API_BASE}/remediation/history`);
  if (!res.ok) throw new Error(`HTTP error ${res.status}`);
  return res.json();
}

export async function fetchLicenseViolations(app?: string): Promise<LicenseViolation[]> {
  const url = app ? `${API_BASE}/licenses/violations?app=${encodeURIComponent(app)}` : `${API_BASE}/licenses/violations`;
  const res = await fetch(url);
  if (!res.ok) throw new Error(`HTTP error ${res.status}`);
  return res.json();
}

export async function fetchLicensePolicy(): Promise<{ policy: string }> {
  const res = await fetch(`${API_BASE}/licenses/policy`);
  if (!res.ok) throw new Error(`HTTP error ${res.status}`);
  return res.json();
}
