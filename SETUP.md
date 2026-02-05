# Setup Instructions

Complete step-by-step guide to set up and run the Financial Credit Rating Platform on Windows 11.

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [Environment Setup](#environment-setup)
3. [Installation Methods](#installation-methods)
4. [Configuration](#configuration)
5. [Running the Application](#running-the-application)
6. [Troubleshooting](#troubleshooting)
7. [Sample Data](#sample-data)

## Prerequisites

Before starting, ensure you have the following installed on your Windows 11 machine:

### Required Software

Your current setup already includes all required tools:

- ✅ **Java 17** (Microsoft Build 17.0.17.10)
- ✅ **Maven 3.9.12**
- ✅ **Node.js 20.19.6**
- ✅ **npm 10.8.2**
- ✅ **Angular CLI 21.0.3**
- ✅ **Docker 29.1.3**
- ✅ **Docker Compose v2.40.3**
- ✅ **Git 2.49.0**

### 📝 Verification Commands

Run these commands to verify your setup:

```bash
java -version
mvn -version
node -v
npm -v
ng version
docker --version
docker-compose --version
git --version
```

## Environment Setup

### Step 1: Clone the Repository

```bash
# Clone the project
git clone <your-repository-url>
cd financial-rating-platform

# Verify structure
dir
```

### Step 2: Create Environment File

```bash
# Copy the example environment file
copy .env.example .env

# Edit .env if needed (optional - defaults work fine)
notepad .env
```

### Step 3: Verify Docker Desktop

1. Open **Docker Desktop**
2. Ensure Docker Engine is running (green icon in system tray)
3. Settings → Resources → Ensure at least 4GB RAM allocated
4. Settings → General → Ensure "Use WSL 2 based engine" is enabled (if available)

## Installation Methods

Choose one of the following methods to run the application:

---

## Method 1: Docker Compose (Recommended) 🐳

**Easiest method - Everything runs in containers**

### Step 1: Build and Start All Services

```bash
# Navigate to project root
cd financial-rating-platform

# Start all services (first time will download images and build)
docker-compose up -d

# This starts:
# - PostgreSQL database on port 5432
# - Kafka + Zookeeper
# - Backend API on port 8080
# - Frontend on port 4200
```

### Step 2: Monitor Startup

```bash
# View logs (all services)
docker-compose logs -f

# View logs (specific service)
docker-compose logs -f backend
docker-compose logs -f frontend

# Check service status
docker-compose ps
```

### Step 3: Verify Services

```bash
# Check backend health
curl http://localhost:8080/api/auth/login

# Check frontend
# Open browser: http://localhost:4200
```

### Step 4: Stop Services

```bash
# Stop all services (preserves data)
docker-compose stop

# Stop and remove containers (preserves data volumes)
docker-compose down

# Stop and remove everything including data
docker-compose down -v
```

---

## Method 2: Local Development Setup 💻

**For active development work**

### Prerequisites
This method requires PostgreSQL and Kafka running. You can:
- Run them via Docker: `docker-compose up -d postgres kafka zookeeper`
- Or install them locally

### Backend Setup

```bash
# Navigate to backend directory
cd backend

# Install dependencies (first time only)
mvn clean install

# Run the application
mvn spring-boot:run

# Backend will start on http://localhost:8080
```

**Alternative: Run from IDE**
1. Open backend folder in IntelliJ IDEA / Eclipse
2. Import as Maven project
3. Run `FinancialRatingApplication.java`

### Frontend Setup

```bash
# Open new terminal
cd frontend

# Install dependencies (first time only)
npm install

# Start development server
npm start

# Frontend will start on http://localhost:4200
```

**Alternative: Production Build**
```bash
# Build for production
npm run build

# Output will be in dist/ folder
```

---

## Configuration

### Backend Configuration

Edit `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/finrating
    username: postgres
    password: postgres
  
  kafka:
    bootstrap-servers: localhost:9092

jwt:
  secret: your-secret-key-here
  expiration: 86400000  # 24 hours
```

### Frontend Configuration

Edit `frontend/src/app/services/*.service.ts` to change API URL:

```typescript
// Current default
private apiUrl = 'http://localhost:8080/api';

// For production
private apiUrl = 'https://your-domain.com/api';
```

### Environment Variables

Create `.env` file in project root:

```env
# Database
DB_NAME=finrating
DB_USERNAME=postgres
DB_PASSWORD=postgres

# JWT
JWT_SECRET=your-super-secret-jwt-key-change-this

# Kafka
KAFKA_BOOTSTRAP_SERVERS=kafka:29092
```

---

## Running the Application

### Access Points

Once everything is running:

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:4200 | Main application UI |
| **Backend API** | http://localhost:8080 | REST API endpoints |
| **PostgreSQL** | localhost:5432 | Database (credentials in .env) |
| **Kafka** | localhost:9092 | Message broker |

### First Time Usage

1. **Open Browser**: Navigate to http://localhost:4200

2. **Sign Up**: 
   - Click "Get Started" or wait for auto-popup
   - Fill in registration form
   - Email: `test@example.com`
   - Password: `password123`
   - Click "Create Account"

3. **Upload Data**:
   - You'll be redirected to Home page
   - Click "Choose File" in Upload Dataset section
   - Select a CSV or Excel file (see Sample Data below)
   - Click "Upload"
   - Data will be processed asynchronously

4. **View Analytics**:
   - Click "Dashboard" button
   - View credit ratings and risk classifications
   - Use search to filter records
   - Explore pagination

---

## Sample Data

### CSV Format

Create a file named `sample-financial-data.csv`:

```csv
Issuer Name,Industry,Country,Revenue,EBITDA,Total Debt,Interest Expense,Current Assets,Current Liabilities
Acme Corp,Technology,USA,1000000,250000,500000,25000,300000,150000
Global Industries,Manufacturing,Germany,5000000,1200000,3000000,150000,1500000,800000
Tech Innovations,Technology,USA,2000000,600000,800000,40000,700000,400000
Finance Plus,Financial Services,UK,3000000,900000,1500000,75000,1000000,600000
Green Energy Co,Energy,Canada,1500000,400000,1200000,60000,500000,300000
```

### Excel Format

Create an Excel file with the same columns and data as above.

### Download Sample Data

Alternatively, use this PowerShell command to create a sample file:

```powershell
@"
Issuer Name,Industry,Country,Revenue,EBITDA,Total Debt,Interest Expense,Current Assets,Current Liabilities
Acme Corp,Technology,USA,1000000,250000,500000,25000,300000,150000
Global Industries,Manufacturing,Germany,5000000,1200000,3000000,150000,1500000,800000
Tech Innovations,Technology,USA,2000000,600000,800000,40000,700000,400000
Finance Plus,Financial Services,UK,3000000,900000,1500000,75000,1000000,600000
Green Energy Co,Energy,Canada,1500000,400000,1200000,60000,500000,300000
"@ | Out-File -FilePath "sample-data.csv" -Encoding UTF8
```

---

## Troubleshooting

### Issue: Docker Containers Won't Start

**Problem**: Services fail to start

**Solutions**:
```bash
# Check Docker Desktop is running
# View detailed error logs
docker-compose logs

# Rebuild containers
docker-compose build --no-cache
docker-compose up -d

# Check port conflicts
netstat -ano | findstr "8080"
netstat -ano | findstr "4200"
netstat -ano | findstr "5432"
```

### Issue: Backend Can't Connect to Database

**Problem**: Backend logs show database connection errors

**Solutions**:
```bash
# Ensure PostgreSQL is running
docker-compose ps postgres

# Check database logs
docker-compose logs postgres

# Restart just the database
docker-compose restart postgres

# Wait a few seconds, then restart backend
docker-compose restart backend
```

### Issue: Kafka Processing Fails

**Problem**: Uploaded files stay in "PENDING" status

**Solutions**:
```bash
# Check Kafka is running
docker-compose ps kafka

# View Kafka logs
docker-compose logs kafka

# Restart Kafka services
docker-compose restart zookeeper kafka

# Wait 10 seconds, then restart backend
docker-compose restart backend
```

### Issue: Frontend Can't Reach Backend

**Problem**: Login/Register fails with network error

**Solutions**:
1. Verify backend is running: `docker-compose ps backend`
2. Check backend logs: `docker-compose logs backend`
3. Test API directly: `curl http://localhost:8080/api/auth/login`
4. Check browser console for CORS errors
5. Ensure `.env` file has correct settings

### Issue: Port Already in Use

**Problem**: Can't start because port is occupied

**Solutions**:
```bash
# Find process using port 8080
netstat -ano | findstr "8080"

# Kill process (replace PID)
taskkill /PID <PID> /F

# Or change port in docker-compose.yml
# Edit the ports section:
ports:
  - "8081:8080"  # Changed from 8080:8080
```

### Issue: Maven Build Fails

**Problem**: `mvn clean install` fails

**Solutions**:
```bash
# Clear Maven cache
mvn clean
mvn dependency:purge-local-repository

# Rebuild
mvn clean install -U

# Skip tests if needed
mvn clean install -DskipTests
```

### Issue: npm Install Fails

**Problem**: `npm install` shows errors

**Solutions**:
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and package-lock.json
rmdir /s /q node_modules
del package-lock.json

# Reinstall
npm install

# Or use legacy peer deps
npm install --legacy-peer-deps
```

### Getting Help

If you encounter issues not covered here:

1. **Check Logs**: Always check `docker-compose logs` first
2. **GitHub Issues**: Search existing issues or create a new one
3. **Stack Overflow**: Tag questions with `spring-boot`, `angular`, `kafka`
4. **Documentation**: Review Spring Boot and Angular official docs

---

## Development Workflow

### Making Changes

#### Backend Changes
```bash
# Changes to Java code require rebuild
docker-compose build backend
docker-compose up -d backend

# Or run locally:
cd backend
mvn spring-boot:run
```

#### Frontend Changes
```bash
# Angular dev server has hot reload
cd frontend
npm start

# Or rebuild Docker container:
docker-compose build frontend
docker-compose up -d frontend
```

### Debugging

#### Backend Debugging
```bash
# Run with debug enabled
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

# Attach your IDE debugger to port 5005
```

#### Frontend Debugging
```bash
# Use browser DevTools (F12)
# Or use VS Code debugger with Chrome extension
```

---

## Production Deployment

### Environment Variables

Create production `.env`:
```env
DB_NAME=finrating_prod
DB_USERNAME=prod_user
DB_PASSWORD=strong_password_here
JWT_SECRET=very_long_random_string_for_production
```

### Build Production Images

```bash
# Build optimized images
docker-compose -f docker-compose.yml build

# Push to registry (optional)
docker tag finrating-backend your-registry/finrating-backend:latest
docker push your-registry/finrating-backend:latest
```

### Deploy

```bash
# On production server
docker-compose up -d

# Or use Kubernetes/AWS/Azure deployment configs
```

---

## Next Steps

1. ✅ Application is running
2. 📊 Upload sample financial data
3. 🔍 Explore the dashboard
4. 🎨 Customize the frontend design
5. ⚙️ Add new features to backend
6. 📈 Scale with Kubernetes

---

## Support

For questions or issues:
- 📧 Email: your-email@example.com
- 💬 GitHub Issues: [Create Issue](https://github.com/your-username/financial-rating-platform/issues)
- 📚 Documentation: [Wiki](https://github.com/your-username/financial-rating-platform/wiki)

---

**Happy Coding! 🚀**
