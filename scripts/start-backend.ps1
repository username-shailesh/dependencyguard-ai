# ==============================================================================
# DependencyGuard AI - Start Spring Boot Backend
# Windows PowerShell Script
# ==============================================================================

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "Starting DependencyGuard AI Backend (Spring Boot 3.3.4)..." -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

Set-Location "$PSScriptRoot\..\backend"

if (Test-Path "target\backend-1.0.0-SNAPSHOT.jar") {
    Write-Host "[✓] Executing packaged JAR..." -ForegroundColor Green
    java -jar target\backend-1.0.0-SNAPSHOT.jar
} else {
    Write-Host "[*] Packaging and starting Spring Boot..." -ForegroundColor Yellow
    mvn spring-boot:run
}
