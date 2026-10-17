// ==============================================================================
// Query: Precise Dependency Path
// Computes exact dependency paths from an application to a target package
// ==============================================================================
MATCH path = (a:Application)-[:DEPENDS_ON*1..6]->(target:Package)
WHERE (a.id = $appId OR a.name = $appName)
  AND (target.id = $targetPackageId OR target.name = $packageName)
RETURN path, length(path) AS depth
ORDER BY depth ASC;
