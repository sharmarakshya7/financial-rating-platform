# Financial Credit Rating & Analytics Platform

A full-stack enterprise application for financial credit rating analysis and risk assessment, built with modern technologies and microservices architecture.

[Platform Preview]()

##  Overview

This platform enables financial analysts, portfolio managers, and researchers to upload financial data, perform automated credit rating analysis, and visualize risk assessments through an intuitive dashboard. The system processes large datasets asynchronously using Apache Kafka and provides real-time analytics.

##  Key Features

### Core Functionality
- **Data Upload & Processing**: Support for CSV and Excel files with automated validation
- **Credit Rating Engine**: Automated rating classification from AAA to D based on financial metrics
- ** Risk Assessment**: Multi-dimensional analysis categorizing entities into Investment Grade, Speculative, or Distressed
- **Real-time Analytics**: Interactive dashboards with rating distributions and financial ratio visualizations
- **Advanced Filtering**: Search and filter records by industry, country, rating, and financial metrics
- * Export Capabilities**: Generate reports and export analyzed data

### Technical Highlights
- **Asynchronous Processing**: Apache Kafka for scalable, real-time data processing
- **Secure Authentication**: JWT-based authentication with role-based access control
- **Responsive Design**: Modern, mobile-first UI with elegant animations
- **Containerized Deployment**: Full Docker support with Docker Compose orchestration
- **Production-Ready**: Comprehensive error handling, logging, and monitoring

## 🏗 Architecture

```
┌─────────────┐      ┌──────────────┐      ┌─────────────┐
│   Angular   │─────>│  Spring Boot │─────>│ PostgreSQL  │
│  Frontend   │      │   Backend    │      │  Database   │
└─────────────┘      └──────────────┘      └─────────────┘
                            │
                            v
                     ┌─────────────┐
                     │Apache Kafka │
                     │  Message    │
                     │   Queue     │
                     └─────────────┘
```

## Technology Stack

### Backend
- **Java 17** - Modern LTS version
- **Spring Boot 3.2** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Database ORM
- **Apache Kafka** - Asynchronous message processing
- **PostgreSQL** - Relational database
- **JWT** - Secure token-based authentication
- **Apache POI** - Excel file processing
- **OpenCSV** - CSV file processing
- **Lombok** - Boilerplate code reduction
- **Maven** - Dependency management

### Frontend
- **Angular 21** - Modern frontend framework
- **TypeScript** - Type-safe JavaScript
- **RxJS** - Reactive programming
- **Standalone Components** - Modern Angular architecture
- **Custom CSS** - Bespoke, professional design
- **Responsive Design** - Mobile-first approach

### Infrastructure
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **Nginx** - Web server for frontend
- **Git** - Version control

## Rating Algorithm

The platform calculates credit ratings based on key financial metrics:

### Metrics Analyzed
- **Debt to EBITDA Ratio**: Leverage indicator
- **Interest Coverage Ratio**: Ability to service debt
- **Liquidity Coverage Ratio**: Short-term financial health
- **Revenue Stability**: Business consistency

### Rating Scale
- **Investment Grade**: AAA to BBB- (Low to moderate risk)
- **Speculative**: BB+ to B- (Higher risk, higher yield)
- **Distressed**: CCC to D (High default risk)

##  Project Structure

```
financial-rating-platform/
├── backend/
│   ├── src/main/java/com/finrating/
│   │   ├── config/           # Configuration classes
│   │   ├── controller/       # REST API controllers
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── entity/           # JPA entities
│   │   ├── kafka/            # Kafka producers/consumers
│   │   ├── repository/       # Data repositories
│   │   ├── security/         # Security configuration
│   │   └── service/          # Business logic
│   ├── src/main/resources/
│   │   └── application.yml   # Application configuration
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/   # Angular components
│   │   │   ├── guards/       # Route guards
│   │   │   ├── interceptors/ # HTTP interceptors
│   │   │   ├── models/       # TypeScript interfaces
│   │   │   └── services/     # Angular services
│   │   ├── assets/           # Static assets
│   │   └── styles.css        # Global styles
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── angular.json
│   └── package.json
├── docker-compose.yml
├── .env.example
├── README.md
└── SETUP.md
```

## Quick Start

See [SETUP.md](./SETUP.md) for detailed installation and setup instructions.

```bash
# Clone the repository
git clone <your-repo-url>
cd financial-rating-platform

# Copy environment file
cp .env.example .env

# Start all services with Docker Compose
docker-compose up -d

# Access the application
# Frontend: http://localhost:4200
# Backend API: http://localhost:8080
```

##  Usage

### 1. Register/Login
- Navigate to http://localhost:4200
- Create an account or sign in
- Secure JWT authentication

### 2. Upload Financial Data
- Click "Upload Dataset" on the home page
- Select CSV or Excel file with financial data
- System processes data asynchronously via Kafka

### 3. View Analytics
- Navigate to Dashboard
- View credit ratings and risk classifications
- Filter and search records
- Export results

### 4. Data Format

Your CSV/Excel file should include these columns:
- Issuer Name
- Industry
- Country
- Revenue
- EBITDA
- Total Debt
- Interest Expense
- Current Assets
- Current Liabilities

## Screenshots

### Landing Page
Modern, responsive landing page with full-screen hero section and feature showcase.

### Dashboard
Interactive analytics dashboard with search, filtering, and pagination.

### Rating Analysis
Comprehensive credit rating display with color-coded risk indicators.

## Security

- **JWT Authentication**: Secure, stateless authentication
- **Password Encryption**: BCrypt password hashing
- **CORS Configuration**: Controlled cross-origin requests
- **Input Validation**: Comprehensive request validation
- **Role-Based Access**: USER and ADMIN roles

## Testing

```bash
# Backend tests
cd backend
mvn test

# Frontend tests
cd frontend
npm test
```

##  Performance

- **Asynchronous Processing**: Non-blocking file uploads
- **Kafka Integration**: Scalable message processing
- **Database Indexing**: Optimized queries
- **Lazy Loading**: Angular route-level code splitting
- **Docker Multi-stage Builds**: Optimized container images

## 🔧 Development

### Prerequisites
- Java 17
- Maven 3.9+
- Node.js 20+
- npm 10+
- Docker & Docker Compose
- Git

### Local Development

```bash
# Backend
cd backend
mvn spring-boot:run

# Frontend
cd frontend
npm install
npm start

# Database & Kafka (via Docker)
docker-compose up postgres kafka zookeeper
```

## 📝 API Documentation

### Authentication Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Dataset Endpoints
- `POST /api/datasets/upload` - Upload financial data
- `GET /api/datasets` - Get user's datasets
- `DELETE /api/datasets/{id}` - Delete dataset

### Dashboard Endpoints
- `GET /api/dashboard/summary` - Get dashboard summary
- `GET /api/dashboard/records` - Get financial records (paginated)
- `POST /api/dashboard/filter` - Filter records

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.



## Author


Rakshya Sharma

## Acknowledgments

- Spring Boot Team for the excellent framework
- Angular Team for the modern frontend platform
- Apache Software Foundation for Kafka



