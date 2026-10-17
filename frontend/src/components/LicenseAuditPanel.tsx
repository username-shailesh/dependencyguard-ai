import React from 'react';
import { Scale, AlertCircle, ExternalLink, ShieldAlert, ArrowRight } from 'lucide-react';
import { LicenseViolation } from '../types';

interface LicenseAuditPanelProps {
  violations: LicenseViolation[];
  onSelectPath: (nodeIds: string[]) => void;
}

export const LicenseAuditPanel: React.FC<LicenseAuditPanelProps> = ({
  violations,
  onSelectPath,
}) => {
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-base font-bold text-white flex items-center space-x-2">
            <Scale className="w-5 h-5 text-amber-400" />
            <span>Open Source License Compliance Audit</span>
          </h2>
          <p className="text-xs text-slate-400">
            Automated detection of copyleft (GPL / AGPL) license constraints in proprietary service dependencies
          </p>
        </div>

        <span className="text-xs font-mono text-amber-400 bg-amber-500/10 border border-amber-500/20 px-2.5 py-1 rounded-lg">
          {violations.length} Compliance Flags
        </span>
      </div>

      {/* Policy Disclaimer Banner */}
      <div className="p-3 rounded-xl bg-amber-950/30 border border-amber-500/30 text-amber-200 text-xs flex items-start space-x-2.5">
        <AlertCircle className="w-4 h-4 text-amber-400 mt-0.5 flex-shrink-0" />
        <div className="leading-relaxed">
          <span className="font-semibold">Legal & Governance Disclaimer:</span> These findings represent internal policy governance flags (SEC-LIC-04) requiring internal software compliance and legal review, not definitive legal determinations.
        </div>
      </div>

      {/* Violations List */}
      <div className="space-y-3">
        {violations.map((v) => (
          <div
            key={v.id}
            className="p-4 rounded-xl bg-slate-900/80 border border-slate-800 hover:border-slate-700 transition-all space-y-3"
          >
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-3">
                <span className="px-2.5 py-1 rounded-md bg-amber-500/20 text-amber-300 font-mono text-xs font-bold border border-amber-500/30">
                  {v.spdxId}
                </span>
                <div>
                  <h4 className="text-sm font-bold text-white">
                    {v.packageName} <span className="text-slate-400 font-mono text-xs">v{v.packageVersion}</span>
                  </h4>
                  <div className="text-xs text-slate-400">
                    Linked by application: <span className="text-indigo-400 font-semibold">{v.appName}</span> ({v.environment})
                  </div>
                </div>
              </div>

              <div className="text-right">
                <span className="text-[10px] uppercase font-bold text-slate-400 block">Risk Category</span>
                <span className="text-xs font-bold text-rose-400 font-mono">{v.category}</span>
              </div>
            </div>

            {/* Dependency Chain */}
            <div className="p-2.5 rounded-lg bg-slate-950 border border-slate-800/80 text-xs font-mono flex items-center justify-between">
              <div className="flex items-center space-x-1.5 text-slate-300 truncate mr-2">
                <span>{v.pathDescription}</span>
                <ArrowRight className="w-3 h-3 text-slate-500 flex-shrink-0" />
                <span className="text-amber-400 font-bold flex-shrink-0">{v.spdxId}</span>
              </div>
            </div>

            {/* Policy Rule & Recommendation */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-xs pt-1">
              <div className="p-2.5 rounded-lg bg-slate-950/60 border border-slate-800 space-y-1">
                <span className="text-[10px] font-bold uppercase text-slate-400 block">Applicable Policy</span>
                <p className="text-slate-300">{v.policyRule}</p>
              </div>

              <div className="p-2.5 rounded-lg bg-slate-950/60 border border-slate-800 space-y-1">
                <span className="text-[10px] font-bold uppercase text-emerald-400 block">Recommended Action</span>
                <p className="text-slate-300">{v.recommendation}</p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
