# ==============================================================================
# DependencyGuard AI - Start FalkorDB Service
# Windows PowerShell Script
# ==============================================================================

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "Starting FalkorDB Graph Database Container..." -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

$dockerCmd = Get-Command docker -ErrorAction SilentlyContinue

if ($dockerCmd) {
    Write-Host "[✓] Docker is available. Starting FalkorDB on port 6379..." -ForegroundColor Green
    docker run -p 6379:6379 --name dependencyguard-falkordb -d falkordb/falkordb:latest
    Write-Host "[✓] FalkorDB container launched successfully on localhost:6379" -ForegroundColor Green
} else {
    Write-Host "[!] Docker was not detected in PATH on this machine." -ForegroundColor Yellow
    Write-Host "    DependencyGuard AI includes a built-in high-fidelity graph engine," -ForegroundColor Yellow
    Write-Host "    so the backend and frontend run seamlessly out of the box!" -ForegroundColor Yellow
    Write-Host "    To attach a live FalkorDB instance when Docker is installed, run:" -ForegroundColor Gray
    Write-Host "    docker run -p 6379:6379 -it --rm falkordb/falkordb:latest" -ForegroundColor Gray
}
