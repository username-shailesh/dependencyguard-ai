// ==============================================================================
// Query: Transitive Dependencies
// Finds the full transitive dependency closure for an application up to 6 hops
// ==============================================================================
MATCH path = (a:Application)-[:DEPENDS_ON*1..6]->(p:Package)
WHERE a.id = $appId OR a.name = $appName
RETURN path;
