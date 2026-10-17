# ==============================================================================
# DependencyGuard AI - Run Full Test Suite (Backend + Frontend)
# Windows PowerShell Script
# ==============================================================================

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "Executing DependencyGuard AI Full Test Suite" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

$backendDir = "$PSScriptRoot\..\backend"
$frontendDir = "$PSScriptRoot\..\frontend"

Write-Host "`n[1/2] Running Backend JUnit 5 & Spring Boot Tests..." -ForegroundColor Yellow
Set-Location $backendDir
mvn test
if ($LASTEXITCODE -ne 0) {
    Write-Host "[X] Backend tests failed!" -ForegroundColor Red
    exit 1
}
Write-Host "[✓] All backend tests PASSED!" -ForegroundColor Green

Write-Host "`n[2/2] Running Frontend Vitest & React Testing Library Tests..." -ForegroundColor Yellow
Set-Location $frontendDir
npm test
if ($LASTEXITCODE -ne 0) {
    Write-Host "[X] Frontend tests failed!" -ForegroundColor Red
    exit 1
}
Write-Host "[✓] All frontend tests PASSED!" -ForegroundColor Green

Write-Host "`n==========================================================" -ForegroundColor Green
Write-Host "🎉 ALL TESTS PASSED SUCCESSFULLY! Ready for Hackathon Submission." -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Green
