# Upload Issue Troubleshooting Guide

## Problem: File Upload Stuck at "Uploading..."

This typically happens due to one of these issues:

### Quick Diagnostic Steps

#### 1. Check if Backend is Running
```bash
# Check backend logs
docker-compose logs backend

# Check if backend is responding
curl http://localhost:8080/api/auth/login
```

#### 2. Check Browser Console
Open Browser DevTools (F12) → Console tab
Look for:
- CORS errors (red text about "Access-Control-Allow-Origin")
- Network errors (Failed to fetch, 500 errors)
- The actual error response from the server

#### 3. Check Network Tab
Browser DevTools (F12) → Network tab
- Find the upload request (usually to `/api/datasets/upload`)
- Check the Status (should be 200, might be 500 or CORS error)
- Check Response to see the actual error message

### Common Fixes

#### Fix 1: Backend Not Receiving Requests (CORS Issue)

**Symptoms:** Browser console shows CORS error

**Solution:** Update backend CORS configuration

Edit `backend/src/main/java/com/finrating/config/SecurityConfig.java`:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:80"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

Then rebuild:
```bash
docker-compose restart backend
```

#### Fix 2: Kafka Not Running

**Symptoms:** Upload succeeds but status stays "PENDING"

**Solution:** 
```bash
# Check Kafka status
docker-compose ps kafka

# If not healthy, restart
docker-compose restart zookeeper
sleep 5
docker-compose restart kafka
sleep 10
docker-compose restart backend
```

#### Fix 3: Database Connection Issue

**Symptoms:** Backend logs show database errors

**Solution:**
```bash
# Check PostgreSQL
docker-compose ps postgres

# Restart if needed
docker-compose restart postgres
sleep 5
docker-compose restart backend
```

#### Fix 4: File Size Too Large

**Symptoms:** Upload fails with 413 error

**Solution:** Already configured for 50MB in `application.yml`, but check:
```bash
# Verify backend logs
docker-compose logs backend | grep -i "multipart"
```

### Complete Reset (Nuclear Option)

If nothing else works:

```bash
# Stop everything
docker-compose down -v

# Remove all containers and volumes
docker system prune -a --volumes

# Rebuild and start fresh
docker-compose build --no-cache
docker-compose up -d

# Wait for services to be healthy
docker-compose ps

# Check logs
docker-compose logs -f
```

### Testing Upload Manually

Test the backend directly with curl:

```bash
# First, login to get a token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}' \
  | jq -r '.token')

echo "Token: $TOKEN"

# Then try uploading
curl -X POST http://localhost:8080/api/datasets/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@sample-financial-data.csv" \
  -v
```

### Most Likely Issues in Order

1. **Backend not running** - Check with `docker-compose ps`
2. **CORS error** - Check browser console
3. **Kafka not ready** - Wait 30 seconds after `docker-compose up`, then restart backend
4. **Authentication issue** - Check if token is valid in localStorage

### Step-by-Step Resolution

```bash
# 1. Check all services are running
docker-compose ps

# 2. Check backend logs for errors
docker-compose logs backend --tail=50

# 3. Ensure Kafka is healthy (may take 30-60 seconds)
docker-compose logs kafka | grep -i "started"

# 4. Restart backend after Kafka is ready
docker-compose restart backend

# 5. Wait 10 seconds
sleep 10

# 6. Try upload again in browser
# Open http://localhost:4200 and test
```

### Verification Checklist

- [ ] Docker Desktop is running
- [ ] All containers are up: `docker-compose ps` shows all as "Up"
- [ ] PostgreSQL is healthy
- [ ] Kafka is healthy (check logs for "started")
- [ ] Backend is running on port 8080
- [ ] Frontend is accessible at port 4200
- [ ] Browser console shows no CORS errors
- [ ] Can login successfully
- [ ] Token is present in localStorage

### Get Help

If still stuck, provide:
1. Output of `docker-compose ps`
2. Output of `docker-compose logs backend --tail=100`
3. Browser console errors (screenshot)
4. Network tab showing the failed request (screenshot)
