# Backend - Financial Rating Platform

Spring Boot backend with Kafka integration for asynchronous processing.

## Running Locally

```bash
mvn spring-boot:run
```

## Building

```bash
mvn clean package
```

## Docker

```bash
docker build -t finrating-backend .
docker run -p 8080:8080 finrating-backend
```
