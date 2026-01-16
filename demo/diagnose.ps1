#!/usr/bin/env pwsh
# Quick Fix Script for Login Issues

Write-Host "`n=== UVT Newcomer Assistant - Login Fix ===" -ForegroundColor Cyan
Write-Host "Diagnosing login issues...`n" -ForegroundColor Yellow

# Check if backend is running
Write-Host "[1/5] Checking backend..." -ForegroundColor Cyan
$backendRunning = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
if ($backendRunning) {
    Write-Host "  ✅ Backend is running on port 8080" -ForegroundColor Green
} else {
    Write-Host "  ❌ Backend is NOT running!" -ForegroundColor Red
    Write-Host "  👉 Start backend:" -ForegroundColor Yellow
    Write-Host "     cd demo" -ForegroundColor White
    Write-Host "     `$env:SPRING_PROFILES_ACTIVE='h2'" -ForegroundColor White
    Write-Host "     .\mvnw.cmd spring-boot:run" -ForegroundColor White
    exit 1
}

# Check if frontend is running
Write-Host "`n[2/5] Checking frontend..." -ForegroundColor Cyan
$frontendRunning = Get-NetTCPConnection -LocalPort 5173 -State Listen -ErrorAction SilentlyContinue
if ($frontendRunning) {
    Write-Host "  ✅ Frontend is running on port 5173" -ForegroundColor Green
} else {
    Write-Host "  ❌ Frontend is NOT running!" -ForegroundColor Red
    Write-Host "  👉 Start frontend:" -ForegroundColor Yellow
    Write-Host "     cd frontend" -ForegroundColor White
    Write-Host "     npm run dev" -ForegroundColor White
    exit 1
}

# Check .env file
Write-Host "`n[3/5] Checking .env configuration..." -ForegroundColor Cyan
$envContent = Get-Content "frontend\.env" -Raw
if ($envContent -match "VITE_API_BASE_URL=http://localhost:8080$") {
    Write-Host "  ✅ .env file is correct" -ForegroundColor Green
} elseif ($envContent -match "VITE_API_BASE_URL=http://localhost:8080/api") {
    Write-Host "  [X] .env has wrong URL (has /api at end)" -ForegroundColor Red
    Write-Host "  [!] .env file was already fixed, but frontend needs RESTART!" -ForegroundColor Yellow
    Write-Host "`n  [!] VITE ONLY LOADS .env AT STARTUP!" -ForegroundColor Yellow
    Write-Host "  [>] You MUST restart the frontend:" -ForegroundColor Red
    Write-Host "     1. Go to frontend terminal" -ForegroundColor White
    Write-Host "     2. Press Ctrl+C to stop" -ForegroundColor White
    Write-Host "     3. Run: npm run dev" -ForegroundColor White
    exit 1
} else {
    Write-Host "  ⚠️  .env has unexpected value: $envContent" -ForegroundColor Yellow
}

# Test backend login
Write-Host "`n[4/5] Testing backend login API..." -ForegroundColor Cyan
try {
    $body = @{ username = "demo"; password = "demo123" } | ConvertTo-Json
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
        -Method POST -ContentType "application/json" -Body $body -ErrorAction Stop
    Write-Host "  ✅ Backend login works!" -ForegroundColor Green
    Write-Host "     User: $($response.user.username) (Role: $($response.user.role))" -ForegroundColor Gray
} catch {
    Write-Host "  ❌ Backend login failed!" -ForegroundColor Red
    Write-Host "     Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Check Java process
Write-Host "`n[5/5] Checking Java process..." -ForegroundColor Cyan
$javaProcess = Get-Process -Name java -ErrorAction SilentlyContinue | Select-Object -First 1
if ($javaProcess) {
    $memoryMB = [math]::Round($javaProcess.WorkingSet / 1MB, 0)
    Write-Host "  ✅ Java process running (PID: $($javaProcess.Id), Memory: ${memoryMB}MB)" -ForegroundColor Green
} else {
    Write-Host "  ⚠️  No Java process found (backend might be starting...)" -ForegroundColor Yellow
}

# Summary
Write-Host "`n=== Diagnosis Complete ===" -ForegroundColor Cyan
Write-Host "`nIf you still cannot login, the issue is likely:" -ForegroundColor Yellow
Write-Host "  🔄 Frontend needs restart after .env fix" -ForegroundColor Red
Write-Host "`nTo fix:" -ForegroundColor Cyan
Write-Host "  1. Stop frontend (Ctrl+C in frontend terminal)" -ForegroundColor White
Write-Host "  2. Restart: npm run dev" -ForegroundColor White
Write-Host "  3. Clear browser cache / localStorage (F12 > Application > Local Storage > Clear)" -ForegroundColor White
Write-Host "  4. Try login again with: demo / demo123" -ForegroundColor White

Write-Host "`nAvailable test accounts:" -ForegroundColor Cyan
Write-Host "  Username: demo    Password: demo123   (USER)" -ForegroundColor Green
Write-Host "  Username: admin   Password: admin123  (ADMIN)" -ForegroundColor Green  
Write-Host "  Username: user    Password: user123   (USER)" -ForegroundColor Green

Write-Host "`nFor detailed debugging, open browser console (F12) and look for:" -ForegroundColor Cyan
Write-Host "  [>] API Request logs" -ForegroundColor White
Write-Host "  [>] API Response logs" -ForegroundColor White
Write-Host "  [X] Error messages" -ForegroundColor White
