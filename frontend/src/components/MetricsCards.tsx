import React from 'react';
import { Layers, Package, AlertTriangle, GitFork, Scale, Activity } from 'lucide-react';
import { OverviewMetrics } from '../types';

interface MetricsCardsProps {
  metrics: OverviewMetrics | null;
  onOpenFormula: () => void;
}

export const MetricsCards: React.FC<MetricsCardsProps> = ({ metrics, onOpenFormula }) => {
  if (!metrics) {
    return (
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4 animate-pulse">
        {[...Array(6)].map((_, i) => (
          <div key={i} className="h-24 bg-slate-900 rounded-xl border border-slate-800"></div>
        ))}
      </div>
    );
  }

  const cards = [
    {
      label: 'Applications',
      value: metrics.totalApplications,
      sub: 'Tier 1 & Tier 2 Services',
      icon: Layers,
      color: 'text-blue-400',
      bgColor: 'bg-blue-500/10',
      borderColor: 'border-blue-500/20',
    },
    {
      label: 'Packages Tracked',
      value: metrics.totalPackages,
      sub: 'Direct & Transitive',
      icon: Package,
      color: 'text-slate-300',
      bgColor: 'bg-slate-500/10',
      borderColor: 'border-slate-500/20',
    },
    {
      label: 'Active CVEs',
      value: metrics.totalVulnerabilities,
      sub: `${metrics.criticalVulnerabilities} Critical Severity`,
      icon: AlertTriangle,
      color: metrics.criticalVulnerabilities > 0 ? 'text-rose-400' : 'text-emerald-400',
      bgColor: 'bg-rose-500/10',
      borderColor: 'border-rose-500/20',
    },
    {
      label: 'High-Risk Paths',
      value: metrics.highRiskPaths,
      sub: 'Multi-Hop Attack Surface',
      icon: GitFork,
      color: 'text-amber-400',
      bgColor: 'bg-amber-500/10',
      borderColor: 'border-amber-500/20',
    },
    {
      label: 'License Flags',
      value: metrics.licenseViolations,
      sub: 'Copyleft Policy Violations',
      icon: Scale,
      color: metrics.licenseViolations > 0 ? 'text-orange-400' : 'text-emerald-400',
      bgColor: 'bg-orange-500/10',
      borderColor: 'border-orange-500/20',
    },
    {
      label: 'Average Risk (BRS)',
      value: `${metrics.averageRiskScore} / 100`,
      sub: 'Blast Radius Index (Explain)',
      icon: Activity,
      color: 'text-indigo-400',
      bgColor: 'bg-indigo-500/10',
      borderColor: 'border-indigo-500/20',
      onClick: onOpenFormula,
    },
  ];

  return (
    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-3">
      {cards.map((c, i) => {
        const Icon = c.icon;
        return (
          <div
            key={i}
            onClick={c.onClick}
            className={`p-3.5 rounded-xl border ${c.borderColor} ${c.bgColor} bg-slate-900/60 backdrop-blur-sm transition-all hover:border-slate-600 ${
              c.onClick ? 'cursor-pointer hover:bg-slate-800/80 group' : ''
            }`}
          >
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-medium text-slate-400">{c.label}</span>
              <Icon className={`w-4 h-4 ${c.color}`} />
            </div>
            <div className="text-xl font-bold text-white tracking-tight">{c.value}</div>
            <div className="text-[11px] text-slate-400 mt-1 flex items-center justify-between">
              <span>{c.sub}</span>
              {c.onClick && (
                <span className="text-[10px] text-indigo-400 underline underline-offset-2 opacity-80 group-hover:opacity-100">
                  View
                </span>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
};
