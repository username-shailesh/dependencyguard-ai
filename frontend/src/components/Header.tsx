import React from 'react';
import { Shield, Database, RefreshCw, Cpu, AlertCircle, CheckCircle2 } from 'lucide-react';
import { GraphStatus, OverviewMetrics } from '../types';

interface HeaderProps {
  metrics: OverviewMetrics | null;
  status: GraphStatus | null;
  onRefresh: () => void;
  onSeed: () => void;
  seeding: boolean;
}

export const Header: React.FC<HeaderProps> = ({
  metrics,
  status,
  onRefresh,
  onSeed,
  seeding,
}) => {
  return (
    <header className="border-b border-slate-800 bg-slate-900/80 backdrop-blur-md sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Brand */}
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-indigo-600 via-indigo-500 to-emerald-400 p-0.5 shadow-lg shadow-indigo-500/20">
            <div className="w-full h-full bg-slate-950 rounded-[10px] flex items-center justify-center">
              <Shield className="w-5 h-5 text-indigo-400" />
            </div>
          </div>
          <div>
            <div className="flex items-center space-x-2">
              <h1 className="text-lg font-bold text-white tracking-tight">DependencyGuard AI</h1>
              <span className="px-2 py-0.5 text-xs font-semibold rounded-full bg-indigo-500/10 text-indigo-400 border border-indigo-500/20">
                FalkorDB Native
              </span>
            </div>
            <p className="text-xs text-slate-400 hidden sm:block">
              Explainable AI Agent for Software Supply-Chain Risk Analysis
            </p>
          </div>
        </div>

        {/* Right Status Controls */}
        <div className="flex items-center space-x-3">
          {/* FalkorDB Connection Status */}
          <div className="flex items-center space-x-2 px-3 py-1.5 rounded-lg bg-slate-800/80 border border-slate-700 text-xs">
            <Database className="w-4 h-4 text-slate-400" />
            <div className="flex items-center space-x-1.5">
              <span className="font-mono text-slate-300">FalkorDB:</span>
              {status?.connected ? (
                <span className="flex items-center text-emerald-400 font-medium">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse mr-1"></span>
                  Live Connected
                </span>
              ) : (
                <span className="flex items-center text-amber-400 font-medium" title={status?.message}>
                  <span className="w-1.5 h-1.5 rounded-full bg-amber-400 mr-1"></span>
                  Emulated Graph Engine
                </span>
              )}
            </div>
          </div>

          {/* AI Grounding Status */}
          <div className="hidden md:flex items-center space-x-1.5 px-3 py-1.5 rounded-lg bg-slate-800/80 border border-slate-700 text-xs text-slate-300">
            <Cpu className="w-4 h-4 text-indigo-400" />
            <span>Agent:</span>
            <span className="text-indigo-400 font-medium">Controlled Graph Grounding</span>
          </div>

          {/* Re-seed Button */}
          <button
            onClick={onSeed}
            disabled={seeding}
            className="flex items-center space-x-1.5 px-3 py-1.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-xs font-semibold text-white shadow-sm transition-all"
            title="Reset and re-seed the graph with realistic supply-chain synthetic dataset"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${seeding ? 'animate-spin' : ''}`} />
            <span>{seeding ? 'Seeding...' : 'Reset Graph'}</span>
          </button>
        </div>
      </div>

      {/* Synthetic Dataset Banner */}
      <div className="bg-indigo-950/40 border-t border-b border-indigo-900/30 px-4 py-1 text-center">
        <p className="text-[11px] text-indigo-300 flex items-center justify-center space-x-1.5">
          <span className="w-1.5 h-1.5 rounded-full bg-indigo-400"></span>
          <span>Environment: Seeded Synthetic Graph Dataset • 5 Applications, Multi-Hop Transitive Chains, Known CVEs & License Policies</span>
        </p>
      </div>
    </header>
  );
};
