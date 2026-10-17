import React, { useMemo, useState, useEffect } from 'react';
import {
  ReactFlow,
  Controls,
  Background,
  MiniMap,
  useNodesState,
  useEdgesState,
  MarkerType,
  Node,
  Edge,
  Handle,
  Position,
} from '@xyflow/react';
import {
  Layers,
  Package as PkgIcon,
  AlertTriangle,
  Scale,
  Users,
  X,
  ExternalLink,
  GitCommit,
  ShieldAlert,
} from 'lucide-react';
import { GraphData, GraphNode, NodeType } from '../types';

interface GraphCanvasProps {
  graphData: GraphData;
  highlightedNodeIds: string[];
  onSelectNode: (node: GraphNode | null) => void;
  selectedNode: GraphNode | null;
}

// Custom Node Component
const CustomGraphNode = ({ data, selected }: { data: any; selected: boolean }) => {
  const isHighlighted = data.isHighlighted;
  const isDimmed = data.hasHighlight && !isHighlighted;

  const nodeColor = () => {
    switch (data.type as NodeType) {
      case 'APPLICATION':
        return 'border-blue-500/80 bg-blue-950/80 text-blue-200 shadow-blue-500/20';
      case 'PACKAGE':
        return data.properties?.isPatchedVersion
          ? 'border-emerald-500/80 bg-emerald-950/80 text-emerald-200 shadow-emerald-500/20'
          : 'border-slate-600 bg-slate-900/90 text-slate-200 shadow-slate-900/40';
      case 'VULNERABILITY':
        return 'border-rose-500 bg-rose-950/90 text-rose-200 shadow-rose-500/30';
      case 'LICENSE':
        return 'border-amber-500/80 bg-amber-950/80 text-amber-200 shadow-amber-500/20';
      case 'TEAM':
        return 'border-purple-500/80 bg-purple-950/80 text-purple-200 shadow-purple-500/20';
    }
  };

  const NodeIcon = () => {
    switch (data.type as NodeType) {
      case 'APPLICATION':
        return <Layers className="w-3.5 h-3.5 text-blue-400" />;
      case 'PACKAGE':
        return <PkgIcon className="w-3.5 h-3.5 text-slate-400" />;
      case 'VULNERABILITY':
        return <AlertTriangle className="w-3.5 h-3.5 text-rose-400 animate-pulse" />;
      case 'LICENSE':
        return <Scale className="w-3.5 h-3.5 text-amber-400" />;
      case 'TEAM':
        return <Users className="w-3.5 h-3.5 text-purple-400" />;
    }
  };

  return (
    <div
      className={`relative px-3 py-2 rounded-xl border text-xs min-w-[150px] max-w-[210px] transition-all duration-300 shadow-lg ${nodeColor()} ${
        selected ? 'ring-2 ring-indigo-400 ring-offset-2 ring-offset-slate-950 scale-105' : ''
      } ${isHighlighted ? 'ring-2 ring-emerald-400 scale-105 opacity-100 z-10' : ''} ${
        isDimmed ? 'opacity-30' : 'opacity-100'
      }`}
    >
      <Handle type="target" position={Position.Top} className="!bg-slate-500 !w-2 !h-2" />

      <div className="flex items-center space-x-1.5 mb-1">
        <NodeIcon />
        <span className="font-semibold truncate tracking-tight text-[11px] text-white">
          {data.title || data.id}
        </span>
      </div>

      <div className="text-[10px] text-slate-400 truncate font-mono">
        {data.subtitle || data.type}
      </div>

      {data.type === 'VULNERABILITY' && (
        <span className="mt-1 inline-block px-1.5 py-0.5 rounded bg-rose-500/20 text-rose-300 text-[9px] font-bold uppercase tracking-wider">
          {data.properties?.severity}
        </span>
      )}

      {data.type === 'APPLICATION' && (
        <span className="mt-1 inline-block px-1.5 py-0.5 rounded bg-blue-500/20 text-blue-300 text-[9px] font-medium">
          {data.properties?.criticalTier}
        </span>
      )}

      <Handle type="source" position={Position.Bottom} className="!bg-slate-500 !w-2 !h-2" />
    </div>
  );
};

const nodeTypes = {
  custom: CustomGraphNode,
};

export const GraphCanvas: React.FC<GraphCanvasProps> = ({
  graphData,
  highlightedNodeIds,
  onSelectNode,
  selectedNode,
}) => {
  const [filterType, setFilterType] = useState<string>('ALL');

  // Compute node coordinates using structured topological tiers
  const layoutedNodes = useMemo(() => {
    const rawNodes = graphData.nodes;
    const tierMap: Record<NodeType, number> = {
      TEAM: 0,
      APPLICATION: 1,
      PACKAGE: 2,
      VULNERABILITY: 3,
      LICENSE: 3,
    };

    // Filter nodes if needed
    const filtered = filterType === 'ALL'
      ? rawNodes
      : rawNodes.filter((n) => n.type === filterType);

    // Group by tier
    const tiers: Record<number, GraphNode[]> = { 0: [], 1: [], 2: [], 3: [] };
    filtered.forEach((n) => {
      const tier = tierMap[n.type] ?? 2;
      tiers[tier].push(n);
    });

    const flowNodes: Node[] = [];
    const hasHighlight = highlightedNodeIds.length > 0;

    Object.keys(tiers).forEach((tierKey) => {
      const tier = Number(tierKey);
      const list = tiers[tier];
      const spacingX = 230;
      const startX = -(list.length * spacingX) / 2 + 100;
      const posY = tier * 180 + 50;

      list.forEach((n, idx) => {
        const isHighlighted = highlightedNodeIds.includes(n.id);
        flowNodes.push({
          id: n.id,
          type: 'custom',
          position: { x: startX + idx * spacingX, y: posY },
          data: {
            ...n,
            isHighlighted,
            hasHighlight,
          },
        });
      });
    });

    return flowNodes;
  }, [graphData.nodes, highlightedNodeIds, filterType]);

  const layoutedEdges = useMemo(() => {
    const activeNodeIds = new Set(layoutedNodes.map((n) => n.id));
    const hasHighlight = highlightedNodeIds.length > 0;

    return graphData.edges
      .filter((e) => activeNodeIds.has(e.source) && activeNodeIds.has(e.target))
      .map((e) => {
        const isHighlighted =
          highlightedNodeIds.includes(e.source) && highlightedNodeIds.includes(e.target);
        const isDimmed = hasHighlight && !isHighlighted;

        return {
          id: e.id,
          source: e.source,
          target: e.target,
          label: e.label,
          animated: isHighlighted || e.label === 'AFFECTED_BY',
          style: {
            stroke: isHighlighted ? '#10b981' : e.label === 'AFFECTED_BY' ? '#f43f5e' : '#475569',
            strokeWidth: isHighlighted ? 2.5 : 1.2,
            opacity: isDimmed ? 0.2 : 1.0,
          },
          labelStyle: {
            fill: isHighlighted ? '#34d399' : '#94a3b8',
            fontSize: 9,
            fontFamily: 'JetBrains Mono',
          },
          markerEnd: {
            type: MarkerType.ArrowClosed,
            color: isHighlighted ? '#10b981' : e.label === 'AFFECTED_BY' ? '#f43f5e' : '#475569',
          },
        } as Edge;
      });
  }, [graphData.edges, layoutedNodes, highlightedNodeIds]);

  const [nodes, setNodes, onNodesChange] = useNodesState(layoutedNodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState(layoutedEdges);

  useEffect(() => {
    setNodes(layoutedNodes);
    setEdges(layoutedEdges);
  }, [layoutedNodes, layoutedEdges]);

  return (
    <div className="relative w-full h-[640px] bg-slate-950 rounded-2xl border border-slate-800 overflow-hidden shadow-2xl">
      {/* Top Controls Toolbar */}
      <div className="absolute top-4 left-4 z-10 flex flex-wrap items-center gap-1.5 bg-slate-900/90 backdrop-blur-md p-1.5 rounded-xl border border-slate-800">
        {['ALL', 'APPLICATION', 'PACKAGE', 'VULNERABILITY', 'LICENSE'].map((type) => (
          <button
            key={type}
            onClick={() => setFilterType(type)}
            className={`px-2.5 py-1 text-[11px] font-semibold rounded-lg transition-all ${
              filterType === type
                ? 'bg-indigo-600 text-white shadow-sm'
                : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800'
            }`}
          >
            {type}
          </button>
        ))}
      </div>

      {/* Path Highlight Reset Banner */}
      {highlightedNodeIds.length > 0 && (
        <div className="absolute top-4 right-4 z-10 flex items-center space-x-2 bg-emerald-950/80 border border-emerald-500/40 text-emerald-300 text-xs px-3 py-1.5 rounded-xl backdrop-blur-md">
          <span className="w-2 h-2 rounded-full bg-emerald-400 animate-ping"></span>
          <span>Highlighting Active Investigation Path ({highlightedNodeIds.length} nodes)</span>
          <button
            onClick={() => onSelectNode(null)}
            className="ml-2 hover:text-white"
            title="Clear path highlight"
          >
            <X className="w-3.5 h-3.5" />
          </button>
        </div>
      )}

      {/* React Flow Core Canvas */}
      <ReactFlow
        nodes={nodes}
        edges={edges}
        nodeTypes={nodeTypes}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        onNodeClick={(_, node) => {
          const found = graphData.nodes.find((n) => n.id === node.id);
          onSelectNode(found || null);
        }}
        fitView
      >
        <Background color="#1e293b" gap={20} size={1} />
        <Controls />
        <MiniMap
          nodeColor={(n) => {
            if (n.data?.type === 'APPLICATION') return '#3b82f6';
            if (n.data?.type === 'VULNERABILITY') return '#f43f5e';
            if (n.data?.type === 'PACKAGE') return '#64748b';
            if (n.data?.type === 'LICENSE') return '#f59e0b';
            return '#a855f7';
          }}
          className="!bg-slate-900 !border-slate-800 !rounded-xl"
        />
      </ReactFlow>

      {/* Node Inspector Drawer */}
      {selectedNode && (
        <div className="absolute top-4 right-4 bottom-4 w-80 bg-slate-900/95 backdrop-blur-xl border border-slate-700/80 rounded-2xl p-4 shadow-2xl z-20 flex flex-col justify-between overflow-y-auto">
          <div>
            <div className="flex items-center justify-between pb-3 border-b border-slate-800">
              <span className="text-xs font-bold uppercase tracking-wider text-indigo-400 flex items-center space-x-1.5">
                <GitCommit className="w-4 h-4" />
                <span>{selectedNode.type} Node Details</span>
              </span>
              <button
                onClick={() => onSelectNode(null)}
                className="p-1 rounded-lg hover:bg-slate-800 text-slate-400 hover:text-slate-200"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="mt-3">
              <h3 className="text-base font-bold text-white tracking-tight">{selectedNode.title}</h3>
              <p className="text-xs text-slate-400 font-mono mt-0.5">{selectedNode.id}</p>
            </div>

            {/* Properties Table */}
            <div className="mt-4 space-y-2">
              <h4 className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">
                Node Properties
              </h4>
              <div className="bg-slate-950/80 rounded-xl p-2.5 border border-slate-800 space-y-1.5 text-xs">
                {Object.entries(selectedNode.properties || {}).map(([key, val]) => (
                  <div key={key} className="flex justify-between items-start text-[11px] py-0.5">
                    <span className="text-slate-400 font-mono">{key}:</span>
                    <span className="text-slate-200 font-semibold text-right max-w-[150px] truncate">
                      {String(val)}
                    </span>
                  </div>
                ))}
              </div>
            </div>

            {/* Connected Relationships in Graph */}
            <div className="mt-4">
              <h4 className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider mb-1.5">
                Connected Edges
              </h4>
              <div className="space-y-1 max-h-40 overflow-y-auto">
                {graphData.edges
                  .filter((e) => e.source === selectedNode.id || e.target === selectedNode.id)
                  .map((e) => (
                    <div
                      key={e.id}
                      className="p-1.5 rounded-lg bg-slate-950 border border-slate-800 text-[10px] font-mono flex items-center justify-between"
                    >
                      <span className="text-slate-400">
                        {e.source === selectedNode.id ? '-> Outgoing' : '<- Incoming'}
                      </span>
                      <span className="text-indigo-400 font-bold">{e.label}</span>
                      <span className="text-slate-300 truncate max-w-[90px]">
                        {e.source === selectedNode.id ? e.target : e.source}
                      </span>
                    </div>
                  ))}
              </div>
            </div>
          </div>

          <div className="pt-3 border-t border-slate-800">
            <div className="text-[10px] text-slate-400 text-center font-mono">
              FalkorDB Node ID: {selectedNode.id}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
