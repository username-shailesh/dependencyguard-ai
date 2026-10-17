import React from 'react';
import { History, ShieldCheck, CheckCircle2, AlertCircle, Clock, ExternalLink } from 'lucide-react';
import { InvestigationRecord } from '../types';

interface InvestigationHistoryPanelProps {
  history: InvestigationRecord[];
  onSelectInvestigation: (query: string) => void;
}

export const InvestigationHistoryPanel: React.FC<InvestigationHistoryPanelProps> = ({
  history,
  onSelectInvestigation,
}) => {
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-base font-bold text-white flex items-center space-x-2">
            <History className="w-5 h-5 text-indigo-400" />
            <span>Investigation Memory & Session History</span>
          </h2>
          <p className="text-xs text-slate-400">
            Persistent cross-session memory with real-time graph state validation
          </p>
        </div>

        <span className="text-xs font-mono text-slate-400 bg-slate-900 border border-slate-800 px-2.5 py-1 rounded-lg">
          {history.length} Saved Inquiries
        </span>
      </div>

      <div className="space-y-3">
        {history.map((rec) => (
          <div
            key={rec.id}
            onClick={() => onSelectInvestigation(rec.userQuery)}
            className="p-4 rounded-xl bg-slate-900/80 border border-slate-800 hover:border-indigo-500/50 cursor-pointer transition-all space-y-2.5 group"
          >
            <div className="flex items-center justify-between">
              <span className="text-xs font-bold text-white group-hover:text-indigo-300 transition-colors">
                "{rec.userQuery}"
              </span>

              {/* Dynamic Graph State Validation Badge */}
              <div className="flex items-center space-x-2">
                {rec.activeInGraph ? (
                  <span className="px-2 py-0.5 rounded-full bg-rose-500/10 text-rose-300 border border-rose-500/30 text-[10px] font-bold flex items-center space-x-1">
                    <AlertCircle className="w-3 h-3 text-rose-400" />
                    <span>Active in Current Graph</span>
                  </span>
                ) : (
                  <span className="px-2 py-0.5 rounded-full bg-emerald-500/10 text-emerald-300 border border-emerald-500/30 text-[10px] font-bold flex items-center space-x-1">
                    <CheckCircle2 className="w-3 h-3 text-emerald-400" />
                    <span>Mitigated on Graph</span>
                  </span>
                )}
              </div>
            </div>

            <p className="text-xs text-slate-300 leading-relaxed font-sans">{rec.summary}</p>

            <div className="flex flex-wrap items-center justify-between gap-2 pt-2 border-t border-slate-800 text-[11px] font-mono text-slate-400">
              <div className="flex items-center space-x-3">
                <span className="flex items-center space-x-1">
                  <Clock className="w-3 h-3" />
                  <span>{new Date(rec.timestamp).toLocaleTimeString()}</span>
                </span>
                <span>Intent: <strong className="text-slate-300">{rec.intent}</strong></span>
                {rec.riskScore > 0 && (
                  <span>Risk Score: <strong className="text-indigo-400">{rec.riskScore.toFixed(1)}</strong></span>
                )}
              </div>

              <span className="text-[10px] text-indigo-400 group-hover:underline">
                Re-run Investigation &rarr;
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
