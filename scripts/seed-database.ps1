# ==============================================================================
# DependencyGuard AI - Seed Graph Database Script
# Windows PowerShell Script
# ==============================================================================

Write-Host "Seeding DependencyGuard AI Graph with synthetic dataset..." -ForegroundColor Cyan

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/graph/seed" -Method Post -TimeoutSec 10
    Write-Host "[✓] Successfully seeded graph!" -ForegroundColor Green
    Write-Host "    Status: $($response.status)" -ForegroundColor Gray
    Write-Host "    Nodes:  $($response.nodes)" -ForegroundColor Gray
    Write-Host "    Edges:  $($response.edges)" -ForegroundColor Gray
    Write-Host "    FalkorDB Live: $($response.connectedToFalkorDb)" -ForegroundColor Gray
} catch {
    Write-Host "[!] Could not contact running backend at http://localhost:8080. Start the backend first using start-backend.ps1." -ForegroundColor Red
}
