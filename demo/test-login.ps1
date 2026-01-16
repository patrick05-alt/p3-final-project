# Test login endpoint
Write-Host "Testing login endpoint..." -ForegroundColor Cyan

# Wait for backend to be ready
Start-Sleep -Seconds 2

# Test with demo user
$body = @{
    username = "demo"
    password = "demo123"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $body

    Write-Host "`n✅ Login successful!" -ForegroundColor Green
    Write-Host "Token: $($response.token.Substring(0, 50))..." -ForegroundColor Yellow
    Write-Host "User: $($response.user.username) ($($response.user.role))" -ForegroundColor Yellow
    
    # Test validate endpoint
    Write-Host "`nTesting validate endpoint..." -ForegroundColor Cyan
    $headers = @{
        "Authorization" = "Bearer $($response.token)"
    }
    
    $validateResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/validate" `
        -Method GET `
        -Headers $headers
    
    Write-Host "✅ Validate successful!" -ForegroundColor Green
    Write-Host "Validated user: $($validateResponse.username)" -ForegroundColor Yellow
    
} catch {
    Write-Host "`n❌ Login failed!" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails) {
        Write-Host "Details: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
}
