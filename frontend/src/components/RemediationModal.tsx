import React, { useState } from 'react';
import {
  X,
  ShieldCheck,
  RotateCcw,
  AlertTriangle,
  CheckCircle2,
  GitBranch,
  Layers,
  ArrowRight,
  Sparkles,
} from 'lucide-react';
import { RemediationPlan } from '../types';
import { applyRemediation, rollbackRemediation } from '../services/api';

interface RemediationModalProps {
  plan: RemediationPlan | null;
  onClose: () => void;
  onRemediationApplied: () => void;
}

export const RemediationModal: React.FC<RemediationModalProps> = ({
  plan,
  onClose,
  onRemediationApplied,
}) => {
  const [currentPlan, setCurrentPlan] = useState<RemediationPlan | null>(plan);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!currentPlan) return null;

  const handleApply = async () => {
    setLoading(true);
    setError(null);
    try {
      // Use finding id or default CVE
      const cveId = currentPlan.findingId?.replace('vuln-', '') || 'CVE-2024-45321';
      const updated = await applyRemediation(cveId);
      setCurrentPlan(updated);
      onRemediationApplied();
    } catch (err: any) {
      setError(err.message || 'Failed to apply remediation');
    } finally {
      setLoading(false);
    }
  };

  const handleRollback = async () => {
    setLoading(true);
    setError(null);
    try {
      const cveId = currentPlan.findingId?.replace('vuln-', '') || 'CVE-2024-45321';
      const updated = await rollbackRemediation(cveId);
      setCurrentPlan(updated);
      onRemediationApplied();
    } catch (err: any) {
      setError(err.message || 'Failed to rollback remediation');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md animate-in fade-in">
      <div className="bg-slate-900 border border-slate-700/80 rounded-2xl w-full max-w-xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
        {/* Modal Header */}
        <div className="p-4 border-b border-slate-800 bg-slate-950/60 flex items-center justify-between">
          <div className="flex items-center space-x-2.5">
            <div className="w-8 h-8 rounded-xl bg-indigo-500/20 border border-indigo-500/30 flex items-center justify-center">
              <ShieldCheck className="w-4 h-4 text-indigo-400" />
            </div>
            <div>
              <h3 className="text-sm font-bold text-white tracking-tight">Remediation Planning & Simulation</h3>
              <p className="text-[11px] text-slate-400">Validate graph impact and test rollback before production sign-off</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg hover:bg-slate-800 text-slate-400 hover:text-white"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Modal Body */}
        <div className="p-5 space-y-4 overflow-y-auto">
          {error && (
            <div className="p-3 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-300 text-xs flex items-center space-x-2">
              <AlertTriangle className="w-4 h-4 flex-shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {/* Status Badge */}
          <div className="flex items-center justify-between">
            <span className="text-xs text-slate-400 font-medium">Simulation State:</span>
            <span
              className={`px-2.5 py-0.5 rounded-full text-xs font-bold uppercase tracking-wider ${
                currentPlan.status === 'APPLIED'
                  ? 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30'
                  : currentPlan.status === 'ROLLED_BACK'
                  ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30'
                  : 'bg-indigo-500/20 text-indigo-300 border border-indigo-500/30'
              }`}
            >
              {currentPlan.status}
            </span>
          </div>

          {/* Version Diff Card */}
          <div className="p-3.5 rounded-xl bg-slate-950 border border-slate-800 flex items-center justify-between">
            <div>
              <span className="text-[10px] uppercase font-bold text-slate-500 block">Package</span>
              <span className="text-sm font-bold text-white">{currentPlan.packageName}</span>
            </div>

            <div className="flex items-center space-x-3 font-mono text-xs">
              <span className="px-2 py-1 rounded bg-rose-500/10 text-rose-300 border border-rose-500/20">
                v{currentPlan.currentVersion}
              </span>
              <ArrowRight className="w-4 h-4 text-slate-500" />
              <span className="px-2 py-1 rounded bg-emerald-500/10 text-emerald-300 border border-emerald-500/20 font-bold">
                v{currentPlan.targetVersion}
              </span>
            </div>
          </div>

          {/* Graph Mutation Summary */}
          <div className="p-3 rounded-xl bg-slate-950/70 border border-slate-800/80 text-xs space-y-1">
            <span className="text-[10px] uppercase font-bold text-indigo-400 block flex items-center space-x-1">
              <GitBranch className="w-3.5 h-3.5" />
              <span>Graph Mutation Rationale</span>
            </span>
            <p className="text-slate-300 leading-relaxed">{currentPlan.reason}</p>
            <p className="text-[11px] text-slate-400 font-mono pt-1">{currentPlan.graphMutationSummary}</p>
          </div>

          {/* Affected Services */}
          <div>
            <h4 className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-1.5 flex items-center space-x-1">
              <Layers className="w-3.5 h-3.5 text-blue-400" />
              <span>Affected Upstream Services ({currentPlan.affectedApplications?.length || 0})</span>
            </h4>
            <div className="flex flex-wrap gap-1.5">
              {currentPlan.affectedApplications?.map((app, i) => (
                <span
                  key={i}
                  className="px-2 py-1 rounded-md bg-slate-950 border border-slate-800 text-xs font-mono text-slate-200"
                >
                  {app}
                </span>
              ))}
            </div>
          </div>

          {/* Breaking Change Risk */}
          <div className="p-3 rounded-xl bg-slate-950/70 border border-slate-800/80 text-xs">
            <span className="text-[10px] uppercase font-bold text-slate-400 block mb-0.5">
              API Compatibility & Deprecation Risk
            </span>
            <span className="text-slate-200">{currentPlan.breakingChangeRisk}</span>
          </div>

          {/* Verification Checklist */}
          <div>
            <h4 className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
              Recommended Verification Steps
            </h4>
            <div className="space-y-1.5">
              {currentPlan.verificationSteps?.map((step, idx) => (
                <div
                  key={idx}
                  className="flex items-start space-x-2 text-xs text-slate-300 bg-slate-950/50 p-2 rounded-lg border border-slate-800"
                >
                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400 mt-0.5 flex-shrink-0" />
                  <span>{step}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Modal Footer Controls */}
        <div className="p-4 border-t border-slate-800 bg-slate-950/60 flex items-center justify-between">
          <div className="text-[10px] text-slate-500 font-mono">
            {currentPlan.timestamp ? `Updated: ${currentPlan.timestamp}` : ''}
          </div>

          <div className="flex items-center space-x-2">
            {currentPlan.status === 'APPLIED' ? (
              <button
                onClick={handleRollback}
                disabled={loading}
                className="px-3.5 py-1.5 rounded-lg bg-amber-600 hover:bg-amber-500 disabled:opacity-50 text-xs font-bold text-white flex items-center space-x-1.5 shadow-md transition-all"
              >
                <RotateCcw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
                <span>Rollback Graph Mutation</span>
              </button>
            ) : (
              <button
                onClick={handleApply}
                disabled={loading}
                className="px-4 py-1.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-xs font-bold text-white flex items-center space-x-1.5 shadow-lg shadow-indigo-600/30 transition-all"
              >
                <ShieldCheck className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
                <span>{loading ? 'Applying to Graph...' : 'Apply Remediation (Simulate)'}</span>
              </button>
            )}
            <button
              onClick={onClose}
              className="px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-xs font-semibold text-slate-300"
            >
              Close
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
