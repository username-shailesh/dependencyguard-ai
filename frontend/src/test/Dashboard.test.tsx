import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { Header } from '../components/Header';
import { MetricsCards } from '../components/MetricsCards';
import { VulnerabilityPanel } from '../components/VulnerabilityPanel';
import { LicenseAuditPanel } from '../components/LicenseAuditPanel';
import { OverviewMetrics, VulnerabilityFinding, LicenseViolation } from '../types';

describe('Frontend Dashboard Components Test Suite', () => {
  const mockMetrics: OverviewMetrics = {
    totalApplications: 5,
    totalPackages: 13,
    totalVulnerabilities: 5,
    criticalVulnerabilities: 2,
    highRiskPaths: 6,
    licenseViolations: 2,
    averageRiskScore: 68.4,
    falkorDbConnected: true,
    dataSource: 'FalkorDB Graph Database',
  };

  const mockFindings: VulnerabilityFinding[] = [
    {
      id: 'vuln-cve-2024-45321',
      cveId: 'CVE-2024-45321',
      title: 'Authentication Token Bypass in auth-core',
      severity: 'CRITICAL',
      cvssScore: 9.8,
      cvssVector: 'CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H',
      summary: 'Improper signature validation allows token forgery.',
      packageName: 'auth-core',
      currentVersion: '1.2.0',
      patchedVersion: '1.2.4',
      affectedApplications: [
        {
          appId: 'app-payment-service',
          appName: 'payment-service',
          environment: 'Production',
          criticalTier: 'TIER_1',
          depth: 1,
          direct: true,
          pathString: 'payment-service -> auth-core:1.2.0',
        },
      ],
      paths: [
        {
          pathId: 'p-1',
          nodeIds: ['app-payment-service', 'pkg-auth-core-1.2.0'],
          nodeLabels: ['payment-service', 'auth-core'],
          depth: 1,
          formattedPath: 'payment-service -> auth-core:1.2.0',
        },
      ],
      blastRadiusScore: 94.1,
      remediationPlan: 'Upgrade to 1.2.4',
      publishedDate: '2024-04-12',
    },
  ];

  const mockViolations: LicenseViolation[] = [
    {
      id: 'lic-1',
      appId: 'app-customer-portal',
      appName: 'customer-portal',
      environment: 'Production',
      packageName: 'data-exporter',
      packageVersion: '1.0.3',
      spdxId: 'GPL-3.0',
      riskLevel: 'HIGH',
      category: 'COPYLEFT_STRONG',
      commercialUseAllowed: false,
      pathDescription: 'customer-portal -> data-exporter -> GPL-3.0',
      policyRule: 'SEC-LIC-04',
      recommendation: 'Replace with MIT alternative',
      depth: 1,
    },
  ];

  it('renders Header with branding, status badge, and synthetic data banner', () => {
    render(
      <Header
        metrics={mockMetrics}
        status={{
          connected: true,
          host: 'localhost',
          port: 6379,
          graphName: 'dependencyguard',
          nodeCount: 33,
          edgeCount: 37,
          clientLibrary: 'JFalkorDB 0.13.1',
          mode: 'FALKORDB_LIVE',
          message: 'Connected',
        }}
        onRefresh={() => {}}
        onSeed={() => {}}
        seeding={false}
      />
    );

    expect(screen.getByText('DependencyGuard AI')).toBeInTheDocument();
    expect(screen.getByText('FalkorDB Native')).toBeInTheDocument();
    expect(screen.getByText('Live Connected')).toBeInTheDocument();
  });

  it('renders MetricsCards with calculated statistics', () => {
    const handleFormula = vi.fn();
    render(<MetricsCards metrics={mockMetrics} onOpenFormula={handleFormula} />);

    expect(screen.getByText('Applications')).toBeInTheDocument();
    expect(screen.getAllByText('5').length).toBeGreaterThan(0);
    expect(screen.getByText('Active CVEs')).toBeInTheDocument();
    expect(screen.getByText('68.4 / 100')).toBeInTheDocument();

    const formulaCard = screen.getByText('Average Risk (BRS)');
    fireEvent.click(formulaCard);
    expect(handleFormula).toHaveBeenCalled();
  });

  it('renders VulnerabilityPanel and supports remediation preview action', () => {
    const handlePreview = vi.fn();
    render(
      <VulnerabilityPanel
        findings={mockFindings}
        onSelectPath={() => {}}
        onPreviewRemediation={handlePreview}
        onAskAgent={() => {}}
      />
    );

    expect(screen.getAllByText('CVE-2024-45321').length).toBeGreaterThan(0);
    expect(screen.getByText('CRITICAL')).toBeInTheDocument();
    expect(screen.getByText(/94.1/)).toBeInTheDocument();

    const previewBtn = screen.getByText('Preview Remediation');
    fireEvent.click(previewBtn);
    expect(handlePreview).toHaveBeenCalledWith('CVE-2024-45321');
  });

  it('renders LicenseAuditPanel with governance warning and violation list', () => {
    render(<LicenseAuditPanel violations={mockViolations} onSelectPath={() => {}} />);

    expect(screen.getByText('Open Source License Compliance Audit')).toBeInTheDocument();
    expect(screen.getAllByText('GPL-3.0').length).toBeGreaterThan(0);
    expect(screen.getAllByText(/SEC-LIC-04/).length).toBeGreaterThan(0);
  });
});
