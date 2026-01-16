# Test browser-like login request with proper CORS headers
Write-Host "`n=== Testing Login Request ===" -ForegroundColor Cyan

# Step 1: OPTIONS preflight request
Write-Host "`n1. Testing CORS Preflight (OPTIONS)..." -ForegroundColor Yellow
try {
    $preflightResponse = Invoke-WebRequest `
        -Uri "http://localhost:8080/api/auth/login" `
        -Method OPTIONS `
        -Headers @{
            "Origin" = "http://localhost:5173"
            "Access-Control-Request-Method" = "POST"
            "Access-Control-Request-Headers" = "content-type,authorization"
        } `
        -UseBasicParsing
    
    Write-Host "   ✅ Preflight Status: $($preflightResponse.StatusCode)" -ForegroundColor Green
    Write-Host "   CORS Headers:" -ForegroundColor Gray
    $preflightResponse.Headers.GetEnumerator() | Where-Object {$_.Key -like "Access-Control-*"} | ForEach-Object {
        Write-Host "      $($_.Key): $($_.Value)" -ForegroundColor Gray
    }
} catch {
    Write-Host "   ❌ Preflight Failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "   Status Code: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
}

# Step 2: Actual POST login request
Write-Host "`n2. Testing Login POST..." -ForegroundColor Yellow
try {
    $loginResponse = Invoke-WebRequest `
        -Uri "http://localhost:8080/api/auth/login" `
        -Method POST `
        -Headers @{
            "Content-Type" = "application/json"
            "Origin" = "http://localhost:5173"
        } `
        -Body '{"username":"admin","password":"admin123"}' `
        -UseBasicParsing
    
    Write-Host "   ✅ Login Status: $($loginResponse.StatusCode)" -ForegroundColor Green
    
    # Parse and display response
    $responseData = $loginResponse.Content | ConvertFrom-Json
    Write-Host "   User: $($responseData.user.username)" -ForegroundColor Green
    Write-Host "   Role: $($responseData.user.role)" -ForegroundColor Green
    Write-Host "   Token: $($responseData.token.Substring(0,20))..." -ForegroundColor Green
    
    Write-Host "`n   CORS Response Headers:" -ForegroundColor Gray
    $loginResponse.Headers.GetEnumerator() | Where-Object {$_.Key -like "Access-Control-*"} | ForEach-Object {
        Write-Host "      $($_.Key): $($_.Value)" -ForegroundColor Gray
    }
    
} catch {
    Write-Host "   ❌ Login Failed!" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "   Status Code: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
        try {
            $errorBody = $_.ErrorDetails.Message | ConvertFrom-Json
            Write-Host "   Response: $($errorBody.message)" -ForegroundColor Red
        } catch {
            Write-Host "   Raw Error: $($_.ErrorDetails.Message)" -ForegroundColor Red
        }
    }
}

Write-Host "`n=== Test Complete ===" -ForegroundColor Cyan
Write-Host "`nIf both tests passed, try logging in at: http://localhost:5173`n" -ForegroundColor White
