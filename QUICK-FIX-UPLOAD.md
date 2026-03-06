# Quick Fix for Upload Stuck Issue

## Fastest Solution (90% of cases)

The upload getting stuck is usually because Kafka takes time to start. Here's the quickest fix:

### Step 1: Check Services
```bash
docker-compose ps
```

All services should show "Up" and "healthy" (or just "Up").

### Step 2: Wait for Kafka (Important!)
Kafka takes 30-60 seconds to fully start. Wait until you see:

```bash
docker-compose logs kafka | grep "started"
```

You should see: `[KafkaServer id=1] started`

### Step 3: Restart Backend
After Kafka is ready:

```bash
docker-compose restart backend
```

Wait 10 seconds, then try uploading again.

## If That Doesn't Work

### Check Browser Console (F12)

1. Open your browser
2. Press F12
3. Go to Console tab
4. Look for errors

**If you see CORS error:**
```
Access to XMLHttpRequest at 'http://localhost:8080/api/datasets/upload' 
from origin 'http://localhost:4200' has been blocked by CORS policy
```

**Fix:**
```bash
docker-compose restart backend
```

**If you see "Failed to fetch" or "Network Error":**
Backend isn't running.

**Fix:**
```bash
docker-compose up -d backend
```

### Manual Test

Test if backend is working:

```bash
# Windows PowerShell
curl http://localhost:8080/api/datasets/health

# Should return: OK
```

If you get "connection refused", backend isn't running.

## Complete Restart (If Nothing Else Works)

```bash
# Stop everything
docker-compose down

# Start fresh
docker-compose up -d

# Wait 60 seconds for Kafka to be ready
timeout /t 60

# Check all services are up
docker-compose ps

# Restart backend
docker-compose restart backend

# Try upload again
```

## Testing Without Docker

If Docker is giving you trouble, test locally:

### Backend Only:
```bash
cd backend
mvn spring-boot:run
```

### Frontend Only:
```bash
cd frontend
npm install
npm start
```

Then you'll need PostgreSQL and Kafka running separately or via Docker:
```bash
docker-compose up -d postgres kafka zookeeper
```

## Most Common Issues

### 1. Kafka Not Ready (70% of cases)
**Symptom:** Upload completes but dataset stays "PENDING"
**Fix:** Wait for Kafka to start, restart backend

### 2. CORS Error (20% of cases)
**Symptom:** Browser shows CORS error
**Fix:** Restart backend: `docker-compose restart backend`

### 3. Backend Not Running (10% of cases)
**Symptom:** "Failed to fetch" error
**Fix:** `docker-compose up -d backend`

## Verification Steps

Before trying to upload:

1. ✅ `docker-compose ps` - All services "Up"
2. ✅ `docker-compose logs kafka | grep started` - Kafka ready
3. ✅ Open http://localhost:4200 - Frontend loads
4. ✅ Can login successfully
5. ✅ F12 Console - No errors
6. ✅ Try upload

## Still Stuck?

Run this diagnostic:

```bash
# Save this as check-services.sh
echo "=== Docker Compose Status ==="
docker-compose ps

echo -e "\n=== Backend Logs (last 20 lines) ==="
docker-compose logs backend --tail=20

echo -e "\n=== Kafka Status ==="
docker-compose logs kafka | grep -i "started" | tail -5

echo -e "\n=== PostgreSQL Status ==="
docker-compose logs postgres | grep -i "ready" | tail -5

echo -e "\n=== Testing Backend ==="
curl -s http://localhost:8080/api/datasets/health || echo "Backend not responding"
```

Run it:
```bash
bash check-services.sh
```

Share the output if you need help.

## Emergency: Bypass Kafka

If you need to get it working ASAP without Kafka, edit:

`backend/src/main/java/com/finrating/service/DatasetService.java`

Comment out Kafka line:
```java
// kafkaProducer.sendDatasetProcessingMessage(dataset.getId(), file.getBytes());

// Add immediate processing instead:
// TODO: Process file directly here
```

This will store the dataset but won't process it immediately. Not recommended for production!

## Key Points

1. **Always wait for Kafka** before uploading (30-60 seconds after `docker-compose up`)
2. **Check browser console** (F12) for actual error messages
3. **Restart backend** after Kafka is ready
4. **Check logs** with `docker-compose logs backend`

The upload feature WILL work - it's just about getting the services started in the right order!
