// ==============================================================================
// DependencyGuard AI - FalkorDB Graph Schema Definition
// Graph name: dependencyguard
// Node types: Application, Package, Vulnerability, License, Team
// Relationships: DEPENDS_ON, AFFECTED_BY, HAS_LICENSE, MAINTAINED_BY
// ==============================================================================

// Indices for fast lookups by identifier and key properties
CREATE INDEX FOR (a:Application) ON (a.id);
CREATE INDEX FOR (a:Application) ON (a.name);
CREATE INDEX FOR (p:Package) ON (p.id);
CREATE INDEX FOR (p:Package) ON (p.name);
CREATE INDEX FOR (v:Vulnerability) ON (v.id);
CREATE INDEX FOR (v:Vulnerability) ON (v.cveId);
CREATE INDEX FOR (l:License) ON (l.id);
CREATE INDEX FOR (l:License) ON (l.spdxId);
CREATE INDEX FOR (t:Team) ON (t.id);
