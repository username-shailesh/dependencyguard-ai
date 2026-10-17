// ==============================================================================
// Query: Direct Dependencies
// Finds all packages directly depended on by a given application
// ==============================================================================
MATCH (a:Application)-[r:DEPENDS_ON {direct: true}]->(p:Package)
WHERE a.id = $appId OR a.name = $appName
RETURN a.name AS application, p.name AS packageName, p.version AS version, p.ecosystem AS ecosystem, r.scope AS scope;
