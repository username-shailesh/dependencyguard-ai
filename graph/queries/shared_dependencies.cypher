// ==============================================================================
// Query: Shared Dependencies Analysis
// Finds packages that are depended upon by more than one application
// ==============================================================================
MATCH (a:Application)-[:DEPENDS_ON*1..6]->(p:Package)
WITH p, count(DISTINCT a) AS appCount, collect(DISTINCT a.name) AS appNames
WHERE appCount > 1
RETURN p.id AS packageId,
       p.name AS packageName,
       p.version AS version,
       p.ecosystem AS ecosystem,
       appCount,
       appNames
ORDER BY appCount DESC, p.name ASC;
