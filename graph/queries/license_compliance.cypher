// ==============================================================================
// Query: License Compliance Policy Audit
// Identifies copyleft or high-risk licenses in application dependency trees
// ==============================================================================
MATCH path = (a:Application)-[:DEPENDS_ON*1..6]->(p:Package)-[:HAS_LICENSE]->(l:License)
WHERE l.riskLevel IN ['HIGH', 'CRITICAL'] OR l.spdxId IN ['GPL-3.0', 'AGPL-3.0']
RETURN a.id AS applicationId,
       a.name AS applicationName,
       p.name AS packageName,
       p.version AS packageVersion,
       l.spdxId AS spdxId,
       l.riskLevel AS riskLevel,
       l.category AS category,
       length(path) AS depth,
       path;
