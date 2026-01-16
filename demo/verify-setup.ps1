# UVT Newcomer Assistant - Quick Start Script
# Run this to verify your local setup works end-to-end

Write-Host "`n=== UVT Newcomer Assistant - Setup Verification ===" -ForegroundColor Cyan

# 1. Check Java
Write-Host "`n[1/5] Checking Java..." -ForegroundColor Yellow
$javaVersion = java -version 2>&1 | Select-String "version"
if ($javaVersion) {
    Write-Host "  ✓ Java found: $javaVersion" -ForegroundColor Green
} else {
    Write-Host "  ✗ Java not found. Install Java 21+ and add to PATH." -ForegroundColor Red
    exit 1
}

# 2. Check Node.js
Write-Host "`n[2/5] Checking Node.js..." -ForegroundColor Yellow
try {
    $nodeVersion = & "$Env:ProgramFiles\nodejs\node.exe" -v 2>&1
    Write-Host "  ✓ Node.js found: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "  ✗ Node.js not found. Install from https://nodejs.org" -ForegroundColor Red
    exit 1
}

# 3. Build backend
Write-Host "`n[3/5] Building Spring Boot backend..." -ForegroundColor Yellow
$buildOutput = ./mvnw -q clean package -DskipTests 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "  ✓ Backend built successfully" -ForegroundColor Green
} else {
    Write-Host "  ✗ Backend build failed" -ForegroundColor Red
    Write-Host $buildOutput
    exit 1
}

# 4. Install frontend deps
Write-Host "`n[4/5] Installing React dependencies..." -ForegroundColor Yellow
Push-Location frontend
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass -Force
& "$Env:ProgramFiles\nodejs\node.exe" "$Env:ProgramFiles\nodejs\node_modules\npm\bin\npm-cli.js" install --silent 2>&1 | Out-Null
if ($LASTEXITCODE -eq 0) {
    Write-Host "  ✓ Frontend dependencies installed" -ForegroundColor Green
} else {
    Write-Host "  ✗ npm install failed" -ForegroundColor Red
    Pop-Location
    exit 1
}
Pop-Location

# 5. Test API (start server in background, hit endpoint, stop)
Write-Host "`n[5/5] Testing backend API..." -ForegroundColor Yellow
$job = Start-Job -ScriptBlock {
    cd $using:PWD
    java -jar target/demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=h2 2>&1 | Out-Null
}
Start-Sleep -Seconds 12

try {
    $response = curl.exe -s http://localhost:8080/api/checklist 2>&1
    if ($response) {
        Write-Host "  ✓ Backend API responds correctly" -ForegroundColor Green
    } else {
        Write-Host "  ✗ API did not respond" -ForegroundColor Red
    }
} catch {
    Write-Host "  ✗ Could not reach API" -ForegroundColor Red
} finally {
    Stop-Job $job -Force
    Remove-Job $job -Force
}

Write-Host "`n=== Setup Complete! ===" -ForegroundColor Cyan
Write-Host "`nNext steps:" -ForegroundColor White
Write-Host "  1. Start backend:  " -NoNewline; Write-Host "`$env:SPRING_PROFILES_ACTIVE='h2'; ./mvnw spring-boot:run" -ForegroundColor Yellow
Write-Host "  2. Start frontend: " -NoNewline; Write-Host "cd frontend; npm run dev" -ForegroundColor Yellow
Write-Host "  3. Open browser:   " -NoNewline; Write-Host "http://localhost:5173`n" -ForegroundColor Yellow
