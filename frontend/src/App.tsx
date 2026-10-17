import React, { useState, useEffect } from 'react';
import {
  Network,
  AlertTriangle,
  Bot,
  Scale,
  History,
  Info,
  X,
  ExternalLink,
  ShieldCheck,
} from 'lucide-react';
import { Header } from './components/Header';
import { MetricsCards } from './components/MetricsCards';
import { GraphCanvas } from './components/GraphCanvas';
import { VulnerabilityPanel } from './components/VulnerabilityPanel';
import { AiAgentChat } from './components/AiAgentChat';
import { RemediationModal } from './components/RemediationModal';
import { LicenseAuditPanel } from './components/LicenseAuditPanel';
import { InvestigationHistoryPanel } from './components/InvestigationHistoryPanel';
import {
  fetchOverviewMetrics,
  fetchGraphData,
  fetchGraphStatus,
  fetchVulnerabilities,
  fetchInvestigationHistory,
  fetchLicenseViolations,
  fetchFormulaDoc,
  seedGraph,
  previewRemediation,
} from './services/api';
import {
  GraphData,
  GraphNode,
  GraphStatus,
  InvestigationRecord,
  LicenseViolation,
  OverviewMetrics,
  RemediationPlan,
  VulnerabilityFinding,
} from './types';

export const App: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'graph' | 'vulnerabilities' | 'agent' | 'licenses' | 'history'>('graph');
  const [metrics, setMetrics] = useState<OverviewMetrics | null>(null);
  const [status, setStatus] = useState<GraphStatus | null>(null);
  const [graphData, setGraphData] = useState<GraphData>({ nodes: [], edges: [] });
  const [vulnerabilities, setVulnerabilities] = useState<VulnerabilityFinding[]>([]);
  const [history, setHistory] = useState<InvestigationRecord[]>([]);
  const [violations, setViolations] = useState<LicenseViolation[]>([]);

  // Selection & highlighting
  const [selectedNode, setSelectedNode] = useState<GraphNode | null>(null);
  const [highlightedNodeIds, setHighlightedNodeIds] = useState<string[]>([]);
  const [activeRemediation, setActiveRemediation] = useState<RemediationPlan | null>(null);
  const [agentQuery, setAgentQuery] = useState<string>('');
  const [formulaDoc, setFormulaDoc] = useState<string | null>(null);
  const [showFormulaModal, setShowFormulaModal] = useState<boolean>(false);
  const [seeding, setSeeding] = useState<boolean>(false);

  const loadAllData = async () => {
    try {
      const [m, s, g, v, h, l] = await Promise.all([
        fetchOverviewMetrics(),
        fetchGraphStatus(),
        fetchGraphData(),
        fetchVulnerabilities(),
        fetchInvestigationHistory(),
        fetchLicenseViolations(),
      ]);
      setMetrics(m);
      setStatus(s);
      setGraphData(g);
      setVulnerabilities(v);
      setHistory(h);
      setViolations(l);
    } catch (err) {
      console.error('Error fetching dashboard data:', err);
    }
  };

  useEffect(() => {
    loadAllData();
  }, []);

  const handleSeed = async () => {
    setSeeding(true);
    try {
      await seedGraph();
      await loadAllData();
      setHighlightedNodeIds([]);
      setSelectedNode(null);
    } catch (err) {
      console.error('Failed to seed graph:', err);
    } finally {
      setSeeding(false);
    }
  };

  const handleSelectPath = (nodeIds: string[]) => {
    setHighlightedNodeIds(nodeIds);
    setActiveTab('graph');
  };

  const handlePreviewRemediation = async (cveId: string) => {
    try {
      const plan = await previewRemediation(cveId);
      setActiveRemediation(plan);
    } catch (err) {
      console.error('Failed to preview remediation:', err);
    }
  };

  const handleAskAgent = (query: string) => {
    setAgentQuery(query);
    setActiveTab('agent');
  };

  const handleOpenFormula = async () => {
    try {
      const doc = await fetchFormulaDoc();
      setFormulaDoc(doc.formula);
      setShowFormulaModal(true);
    } catch (err) {
      console.error('Failed to fetch formula doc:', err);
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans">
      <Header
        metrics={metrics}
        status={status}
        onRefresh={loadAllData}
        onSeed={handleSeed}
        seeding={seeding}
      />

      <main className="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 w-full space-y-6">
        {/* Top Metrics Row */}
        <MetricsCards metrics={metrics} onOpenFormula={handleOpenFormula} />

        {/* Navigation Tabs */}
        <div className="flex items-center space-x-1 p-1 rounded-xl bg-slate-900 border border-slate-800 w-fit">
          <button
            onClick={() => setActiveTab('graph')}
            className={`flex items-center space-x-2 px-4 py-2 rounded-lg text-xs font-semibold transition-all ${
              activeTab === 'graph'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800'
            }`}
          >
            <Network className="w-4 h-4" />
            <span>Dependency Graph</span>
          </button>

          <button
            onClick={() => setActiveTab('vulnerabilities')}
            className={`flex items-center space-x-2 px-4 py-2 rounded-lg text-xs font-semibold transition-all ${
              activeTab === 'vulnerabilities'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800'
            }`}
          >
            <AlertTriangle className="w-4 h-4" />
            <span>Vulnerability Blast Radius</span>
            <span className="ml-1 px-1.5 py-0.2 bg-rose-500/20 text-rose-300 rounded text-[10px]">
              {vulnerabilities.length}
            </span>
          </button>

          <button
            onClick={() => setActiveTab('agent')}
            className={`flex items-center space-x-2 px-4 py-2 rounded-lg text-xs font-semibold transition-all ${
              activeTab === 'agent'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800'
            }`}
          >
            <Bot className="w-4 h-4" />
            <span>AI Investigation Agent</span>
          </button>

          <button
            onClick={() => setActiveTab('licenses')}
            className={`flex items-center space-x-2 px-4 py-2 rounded-lg text-xs font-semibold transition-all ${
              activeTab === 'licenses'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800'
            }`}
          >
            <Scale className="w-4 h-4" />
            <span>License Compliance</span>
            <span className="ml-1 px-1.5 py-0.2 bg-amber-500/20 text-amber-300 rounded text-[10px]">
              {violations.length}
            </span>
          </button>

          <button
            onClick={() => setActiveTab('history')}
            className={`flex items-center space-x-2 px-4 py-2 rounded-lg text-xs font-semibold transition-all ${
              activeTab === 'history'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800'
            }`}
          >
            <History className="w-4 h-4" />
            <span>Investigation Memory</span>
          </button>
        </div>

        {/* Tab Content Display */}
        <div className="space-y-4">
          {activeTab === 'graph' && (
            <div className="space-y-2">
              <div className="flex items-center justify-between text-xs text-slate-400 px-1">
                <span>
                  Interactive FalkorDB Graph Canvas • Click any node to inspect properties and relationships
                </span>
                <span>Zoom, Pan & Click Paths to trace multi-hop chains</span>
              </div>
              <GraphCanvas
                graphData={graphData}
                highlightedNodeIds={highlightedNodeIds}
                onSelectNode={setSelectedNode}
                selectedNode={selectedNode}
              />
            </div>
          )}

          {activeTab === 'vulnerabilities' && (
            <VulnerabilityPanel
              findings={vulnerabilities}
              onSelectPath={handleSelectPath}
              onPreviewRemediation={handlePreviewRemediation}
              onAskAgent={handleAskAgent}
            />
          )}

          {activeTab === 'agent' && (
            <AiAgentChat
              onSelectPath={handleSelectPath}
              onPreviewRemediation={handlePreviewRemediation}
              initialQuery={agentQuery}
            />
          )}

          {activeTab === 'licenses' && (
            <LicenseAuditPanel
              violations={violations}
              onSelectPath={handleSelectPath}
            />
          )}

          {activeTab === 'history' && (
            <InvestigationHistoryPanel
              history={history}
              onSelectInvestigation={handleAskAgent}
            />
          )}
        </div>
      </main>

      {/* Remediation Preview & Rollback Modal */}
      {activeRemediation && (
        <RemediationModal
          plan={activeRemediation}
          onClose={() => setActiveRemediation(null)}
          onRemediationApplied={loadAllData}
        />
      )}

      {/* Formula Modal */}
      {showFormulaModal && formulaDoc && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md">
          <div className="bg-slate-900 border border-slate-700 rounded-2xl w-full max-w-xl p-5 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between pb-3 border-b border-slate-800">
              <h3 className="text-sm font-bold text-white flex items-center space-x-2">
                <Info className="w-4 h-4 text-indigo-400" />
                <span>Blast Radius Risk Score (BRS) Index</span>
              </h3>
              <button
                onClick={() => setShowFormulaModal(false)}
                className="p-1 rounded-lg hover:bg-slate-800 text-slate-400 hover:text-white"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
            <div className="text-xs text-slate-300 whitespace-pre-wrap leading-relaxed font-sans max-h-96 overflow-y-auto">
              {formulaDoc}
            </div>
            <div className="pt-2 border-t border-slate-800 flex justify-end">
              <button
                onClick={() => setShowFormulaModal(false)}
                className="px-4 py-1.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-xs font-bold text-white"
              >
                Got it
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default App;
