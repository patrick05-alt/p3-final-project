# ✅ LOGIN 403 ERROR - FIXED

## Problem Solved
The 403 Forbidden error during login was caused by **conflicting CORS configurations**.

## What Was Fixed

### Changed Files
1. **WebConfig.java** - Removed duplicate CORS configuration
2. **CorsConfig.java** - Disabled conflicting configuration  
3. **SecurityConfig.java** - Kept as single source of CORS truth

### Verification (All Tests Passed ✅)
```
OPTIONS Preflight: 200 ✅
  Allow-Origin: http://localhost:5173
  Allow-Credentials: true

POST Login: 200 ✅
  User: admin | Role: ADMIN
  Token: Successfully generated
```

## How to Start Both Servers

### Backend (Port 8080)
```powershell
cd C:\Users\Lebowski\Documents\projects\p3\demo-project\demo
Start-Job -ScriptBlock {
    Set-Location "C:\Users\Lebowski\Documents\projects\p3\demo-project\demo"
    & ".\mvnw.cmd" "spring-boot:run"
} -Name "Backend"
```

### Frontend (Port 5173)
```powershell
cd C:\Users\Lebowski\Documents\projects\p3\demo-project\demo\frontend
npm run dev
```

## Login Credentials
- **URL**: http://localhost:5173
- **Username**: admin
- **Password**: admin123

## If Login Still Doesn't Work

### 1. Check Both Servers Are Running
```powershell
# Check backend (should see port 8080)
netstat -ano | Select-String "LISTENING" | Select-String ":8080"

# Check frontend (should see port 5173)
netstat -ano | Select-String "LISTENING" | Select-String ":5173"
```

### 2. Test Backend Directly
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body '{"username":"admin","password":"admin123"}' `
  -UseBasicParsing
```
Expected: Status 200

### 3. Check Browser Console
1. Open http://localhost:5173
2. Press F12 to open DevTools
3. Go to Console tab
4. Try to login
5. Look for errors (red messages)

### 4. Check Network Tab
1. Open DevTools (F12)
2. Go to Network tab  
3. Try to login
4. Click on the `login` request
5. Check:
   - **Status Code**: Should be 200 (not 403)
   - **Response Headers**: Should have `Access-Control-Allow-Origin: http://localhost:5173`
   - **Request Headers**: Should have `Origin: http://localhost:5173`

## Common Issues

### Issue: "Failed to fetch" or "Network Error"
**Cause**: Backend not running
**Fix**: Start backend with the command above

### Issue: Still getting 403
**Cause**: Old compiled code in browser cache
**Fix**: 
1. Hard refresh: Ctrl+Shift+R (or Cmd+Shift+R on Mac)
2. Clear browser cache
3. Try in incognito/private window

### Issue: CORS error in console
**Cause**: Backend restarted without the fix
**Fix**: Make sure you saved the Java files and restarted backend

## Technical Details

### Why It Failed Before
You had 3 different CORS configurations:
- **CorsConfig.java**: `allowCredentials(false)` ❌
- **WebConfig.java**: `allowCredentials(true)` but conflicting
- **SecurityConfig.java**: `allowCredentials(true)` but overridden

The browser sends `credentials: 'include'` but one config said no credentials = 403

### Current Configuration (SecurityConfig.java only)
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);  // ✅ Consistent setting
    configuration.setMaxAge(3600L);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

## Database Note
**You do NOT need MySQL running** - the application uses H2 in-memory database (see application.properties)

## Need More Help?
If you're still seeing 403 errors:
1. Share the exact error message from browser console
2. Share the Network tab screenshot showing the failed request
3. Run the test script: `.\test-browser-login.ps1`
