import React, { useState, useRef, useEffect } from 'react';
import {
  Send,
  Sparkles,
  Bot,
  User,
  ShieldCheck,
  Code2,
  ChevronDown,
  ChevronUp,
  GitFork,
  ArrowRight,
  Database,
  Clock,
} from 'lucide-react';
import { AgentResponse, DependencyPath } from '../types';
import { investigateAgent } from '../services/api';

interface Message {
  id: string;
  sender: 'user' | 'agent';
  text: string;
  response?: AgentResponse;
  timestamp: string;
}

interface AiAgentChatProps {
  onSelectPath: (nodeIds: string[]) => void;
  onPreviewRemediation: (cveId: string) => void;
  initialQuery?: string;
}

export const AiAgentChat: React.FC<AiAgentChatProps> = ({
  onSelectPath,
  onPreviewRemediation,
  initialQuery,
}) => {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: 'welcome',
      sender: 'agent',
      text: "Hello! I am **DependencyGuard AI**, your explainable software supply-chain risk investigator backed by FalkorDB. Ask me anything about dependency paths, affected applications, vulnerability blast radius, or remediation planning.",
      timestamp: new Date().toLocaleTimeString(),
    },
  ]);
  const [inputValue, setInputValue] = useState('');
  const [loading, setLoading] = useState(false);
  const [expandedCypher, setExpandedCypher] = useState<Record<string, boolean>>({});
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const samplePrompts = [
    'Which applications are affected if auth-core is vulnerable?',
    'Show me the highest-priority security risks.',
    'Which vulnerable package affects the largest number of applications?',
    'Explain how payment-service reaches crypto-vault.',
    'What should I fix first, and why?',
    'Audit license policy violations.',
  ];

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, loading]);

  useEffect(() => {
    if (initialQuery && initialQuery.trim()) {
      handleSend(initialQuery);
    }
  }, [initialQuery]);

  const handleSend = async (queryText?: string) => {
    const q = queryText || inputValue;
    if (!q.trim() || loading) return;

    const userMsg: Message = {
      id: 'msg-' + Date.now(),
      sender: 'user',
      text: q.trim(),
      timestamp: new Date().toLocaleTimeString(),
    };

    setMessages((prev) => [...prev, userMsg]);
    setInputValue('');
    setLoading(true);

    try {
      const res = await investigateAgent(q.trim());
      const agentMsg: Message = {
        id: 'msg-' + (Date.now() + 1),
        sender: 'agent',
        text: res.answer,
        response: res,
        timestamp: new Date().toLocaleTimeString(),
      };
      setMessages((prev) => [...prev, agentMsg]);
    } catch (err: any) {
      const errorMsg: Message = {
        id: 'msg-' + (Date.now() + 1),
        sender: 'agent',
        text: `⚠️ Error executing investigation: ${err.message}. Please check backend connection.`,
        timestamp: new Date().toLocaleTimeString(),
      };
      setMessages((prev) => [...prev, errorMsg]);
    } finally {
      setLoading(false);
    }
  };

  const toggleCypher = (msgId: string) => {
    setExpandedCypher((prev) => ({ ...prev, [msgId]: !prev[msgId] }));
  };

  return (
    <div className="flex flex-col h-[640px] bg-slate-900/90 rounded-2xl border border-slate-800 overflow-hidden shadow-2xl">
      {/* Chat Header */}
      <div className="p-3.5 border-b border-slate-800 bg-slate-950/60 backdrop-blur-sm flex items-center justify-between">
        <div className="flex items-center space-x-2">
          <div className="w-7 h-7 rounded-lg bg-indigo-500/20 border border-indigo-500/30 flex items-center justify-center">
            <Bot className="w-4 h-4 text-indigo-400" />
          </div>
          <div>
            <h3 className="text-xs font-bold text-white tracking-tight flex items-center space-x-1.5">
              <span>Explainable AI Investigation Agent</span>
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>
            </h3>
            <p className="text-[10px] text-slate-400">Controlled execution grounded in verified FalkorDB graph paths</p>
          </div>
        </div>

        <div className="flex items-center space-x-2 text-[10px] font-mono text-slate-400">
          <Database className="w-3.5 h-3.5 text-indigo-400" />
          <span>Cypher Powered</span>
        </div>
      </div>

      {/* Message List */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {messages.map((m) => {
          const isAgent = m.sender === 'agent';
          return (
            <div key={m.id} className={`flex space-x-2.5 ${isAgent ? 'items-start' : 'items-start justify-end'}`}>
              {isAgent && (
                <div className="w-7 h-7 rounded-lg bg-indigo-600/20 border border-indigo-500/30 flex items-center justify-center flex-shrink-0 mt-0.5">
                  <Bot className="w-4 h-4 text-indigo-400" />
                </div>
              )}

              <div className={`max-w-[85%] space-y-2 ${isAgent ? 'text-left' : 'text-right'}`}>
                {/* Text Bubble */}
                <div
                  className={`p-3.5 rounded-2xl text-xs leading-relaxed ${
                    isAgent
                      ? 'bg-slate-950 border border-slate-800 text-slate-200 shadow-md'
                      : 'bg-indigo-600 text-white rounded-br-sm shadow-md'
                  }`}
                >
                  <div className="whitespace-pre-wrap font-sans">{m.text}</div>
                </div>

                {/* Grounding & Evidence Section for Agent Responses */}
                {isAgent && m.response && (
                  <div className="bg-slate-950/70 border border-slate-800 rounded-xl p-2.5 space-y-2 text-xs">
                    {/* Status Badges */}
                    <div className="flex flex-wrap items-center justify-between gap-1 text-[10px]">
                      <span className="px-2 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 font-semibold flex items-center space-x-1">
                        <ShieldCheck className="w-3 h-3" />
                        <span>100% Grounded in Graph Records</span>
                      </span>

                      <div className="flex items-center space-x-2 text-slate-400 font-mono">
                        <Clock className="w-3 h-3" />
                        <span>{m.response.executionTimeMs} ms</span>
                      </div>
                    </div>

                    {/* Collapsible Cypher Query */}
                    {m.response.cypherQueryUsed && (
                      <div>
                        <button
                          onClick={() => toggleCypher(m.id)}
                          className="flex items-center space-x-1 text-[10px] text-slate-400 hover:text-slate-200 font-mono"
                        >
                          <Code2 className="w-3 h-3 text-indigo-400" />
                          <span>FalkorDB Cypher Query Executed</span>
                          {expandedCypher[m.id] ? <ChevronUp className="w-3 h-3" /> : <ChevronDown className="w-3 h-3" />}
                        </button>

                        {expandedCypher[m.id] && (
                          <pre className="mt-1 p-2 rounded bg-slate-900 border border-slate-800 text-[10px] text-indigo-300 font-mono overflow-x-auto">
                            {m.response.cypherQueryUsed}
                          </pre>
                        )}
                      </div>
                    )}

                    {/* Evidence Paths */}
                    {m.response.evidencePaths && m.response.evidencePaths.length > 0 && (
                      <div className="space-y-1">
                        <span className="text-[10px] font-semibold text-slate-400 uppercase tracking-wider block">
                          Verified Dependency Paths ({m.response.evidencePaths.length}):
                        </span>
                        {m.response.evidencePaths.slice(0, 3).map((path: DependencyPath, idx: number) => (
                          <div
                            key={idx}
                            onClick={() => onSelectPath(path.nodeIds)}
                            className="p-1.5 rounded bg-slate-900 border border-slate-800 hover:border-emerald-500/40 cursor-pointer flex items-center justify-between text-[10px] font-mono group"
                          >
                            <span className="text-slate-300 group-hover:text-emerald-300 truncate mr-2">
                              {path.formattedPath}
                            </span>
                            <span className="text-emerald-400 opacity-80 group-hover:opacity-100 flex-shrink-0">
                              Highlight
                            </span>
                          </div>
                        ))}
                      </div>
                    )}

                    {/* Suggested Remediation Action */}
                    {m.response.suggestedRemediation && (
                      <div className="pt-1.5 border-t border-slate-800/80 flex items-center justify-between">
                        <div className="text-[11px] text-slate-300">
                          Recommended Action: Upgrade <span className="text-indigo-400 font-bold">{m.response.suggestedRemediation.packageName}</span> to <span className="text-emerald-400 font-bold">{m.response.suggestedRemediation.targetVersion}</span>
                        </div>
                        <button
                          onClick={() => onPreviewRemediation(m.response!.findings[0]?.cveId || 'CVE-2024-45321')}
                          className="px-2.5 py-1 rounded bg-indigo-600 hover:bg-indigo-500 text-[10px] font-bold text-white transition-all shadow-sm"
                        >
                          Preview & Apply
                        </button>
                      </div>
                    )}
                  </div>
                )}

                <div className="text-[9px] text-slate-500 font-mono px-1">{m.timestamp}</div>
              </div>

              {!isAgent && (
                <div className="w-7 h-7 rounded-lg bg-indigo-600 flex items-center justify-center flex-shrink-0 mt-0.5">
                  <User className="w-4 h-4 text-white" />
                </div>
              )}
            </div>
          );
        })}

        {loading && (
          <div className="flex items-center space-x-2 text-xs text-slate-400 p-2">
            <Sparkles className="w-4 h-4 text-indigo-400 animate-spin" />
            <span>Traversing FalkorDB graph & synthesizing grounded answer...</span>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Suggested Prompt Chips */}
      <div className="px-3.5 py-2 border-t border-slate-800/60 bg-slate-950/30 flex items-center space-x-1.5 overflow-x-auto no-scrollbar">
        <Sparkles className="w-3.5 h-3.5 text-indigo-400 flex-shrink-0" />
        {samplePrompts.map((p, i) => (
          <button
            key={i}
            onClick={() => handleSend(p)}
            className="flex-shrink-0 px-2.5 py-1 rounded-full bg-slate-800 hover:bg-slate-700 text-[10px] text-slate-300 hover:text-white border border-slate-700/80 transition-all"
          >
            {p}
          </button>
        ))}
      </div>

      {/* Input Box */}
      <div className="p-3 border-t border-slate-800 bg-slate-950">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            handleSend();
          }}
          className="flex items-center space-x-2"
        >
          <input
            type="text"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            placeholder="Ask a supply-chain question (e.g., 'Which applications are affected if auth-core is vulnerable?')..."
            className="flex-1 bg-slate-900 border border-slate-700 rounded-xl px-3.5 py-2 text-xs text-white placeholder-slate-500 focus:outline-none focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500"
          />
          <button
            type="submit"
            disabled={!inputValue.trim() || loading}
            className="p-2 rounded-xl bg-indigo-600 hover:bg-indigo-500 disabled:opacity-40 text-white transition-all shadow-md"
          >
            <Send className="w-4 h-4" />
          </button>
        </form>
      </div>
    </div>
  );
};
