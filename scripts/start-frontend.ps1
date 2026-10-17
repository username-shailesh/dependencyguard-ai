# ==============================================================================
# DependencyGuard AI - Start React Frontend Dashboard
# Windows PowerShell Script
# ==============================================================================

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "Starting DependencyGuard AI Frontend Dashboard (Vite)..." -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

Set-Location "$PSScriptRoot\..\frontend"

npm run dev
