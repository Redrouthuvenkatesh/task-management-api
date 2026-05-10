# Task Manager API

A production-quality RESTful Task Management API built with **Java 17** and **Spring Boot 3**, featuring clean layered architecture, JWT authentication, H2 in-memory storage, Swagger/OpenAPI documentation, and comprehensive unit tests.

---

## Project Overview

This API enables full lifecycle management of tasks — create, read, update, delete, and mark complete — with role-based access control via JWT Bearer tokens.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Build | Maven |
| Web | Spring Web (MVC) |
| Validation | Spring Validation (Jakarta) |
| Security | Spring Security + JWT (JJWT 0.11) |
| Database | H2 In-Memory + Spring Data JPA + Hibernate |
| Documentation | Springdoc OpenAPI 2.3 / Swagger UI |
| Logging | SLF4J + Logback |
| Testing | JUnit 5 + Mockito + AssertJ |
| Utilities | Lombok |

---

## Features

-  Full CRUD for Tasks
-  Task status lifecycle: `PENDING` → `IN_PROGRESS` → `COMPLETED`
-  JWT Bearer token authentication
-  Public read endpoints (no auth required)
-  Bean Validation on all request DTOs
-  Global exception handling with consistent JSON error responses
-  H2 in-memory database with console access
-  Auto-generated timestamps (`createdAt`, `updatedAt`)
-  Sample data seeded on startup
-  Swagger UI with JWT authorization support
-  SLF4J logging throughout service and controller layers
-  15+ unit tests covering service layer

---

## API Endpoints

### Authentication

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/auth/login` | None | Obtain a JWT token |

### Tasks

| Method | Endpoint | Auth | HTTP Status | Description |
|--------|----------|------|-------------|-------------|
| GET | `/tasks` | None | 200 | Get all tasks |
| GET | `/tasks/{id}` | None | 200 / 404 | Get task by ID |
| POST | `/tasks` | Bearer JWT | 201 / 400 / 401 | Create a new task |
| PUT | `/tasks/{id}` | Bearer JWT | 200 / 400 / 401 / 404 | Update a task |
| DELETE | `/tasks/{id}` | Bearer JWT | 204 / 401 / 404 | Delete a task |
| PATCH | `/tasks/{id}/complete` | Bearer JWT | 200 / 401 / 404 | Mark task as completed |

---

## Validation Rules

| Field | Rule |
|-------|------|
| `title` | Must not be blank |
| `description` | Maximum 500 characters |
| `dueDate` | Must not be in the past (today is valid) |

---

## Authentication Details

| Credential | Value |
|------------|-------|
| Username | `admin` |
| Password | `admin123` |
| Token type | `Bearer` |
| Token expiration | 24 hours |

**Flow:**
1. `POST /auth/login` → receive `token`
2. Add header `Authorization: Bearer <token>` to all protected requests

---

## How to Run

### Prerequisites
- Java 17+
- Maven 3.8+

### Start the application
```bash
mvn spring-boot:run
```

The server starts on **port 8080** by default.

---

## Maven Commands

```bash
# Compile
mvn compile

# Run all tests
mvn test

# Package (skip tests)
mvn package -DskipTests

# Run packaged JAR
java -jar target/task-manager-api-1.0.0.jar

# Run with Spring Boot plugin
mvn spring-boot:run

# Clean build
mvn clean install
```

---

## Useful URLs

| Resource | URL |
|----------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api-docs |
| H2 Console | http://localhost:8080/h2-console |

**H2 Console settings:**
- JDBC URL: `jdbc:h2:mem:taskdb`
- Username: `sa`
- Password: `password`

---

## Sample cURL Commands

### 1. Login and get JWT token
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 2. Get all tasks (public)
```bash
curl http://localhost:8080/tasks
```

### 3. Get task by ID (public)
```bash
curl http://localhost:8080/tasks/1
```

### 4. Create a task (requires token)
```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -d '{
    "title": "Implement feature X",
    "description": "Build and test the new feature",
    "dueDate": "2026-12-31"
  }'
```

### 5. Update a task (requires token)
```bash
curl -X PUT http://localhost:8080/tasks/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -d '{
    "title": "Updated title",
    "description": "Updated description",
    "dueDate": "2026-12-31",
    "status": "IN_PROGRESS"
  }'
```

### 6. Mark task as complete (requires token)
```bash
curl -X PATCH http://localhost:8080/tasks/1/complete \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

### 7. Delete a task (requires token)
```bash
curl -X DELETE http://localhost:8080/tasks/1 \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

---

## Sample JSON Requests & Responses

### POST /auth/login

**Request:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "username": "admin"
  },
  "timestamp": "2026-05-10T12:00:00"
}
```

### POST /tasks

**Request:**
```json
{
  "title": "Deploy to production",
  "description": "Deploy the application to AWS ECS.",
  "dueDate": "2026-12-31"
}
```

**Response (201 CREATED):**
```json
{
  "success": true,
  "message": "Task created successfully",
  "data": {
    "id": 6,
    "title": "Deploy to production",
    "description": "Deploy the application to AWS ECS.",
    "dueDate": "2026-12-31",
    "status": "PENDING",
    "createdAt": "2026-05-10T12:01:00",
    "updatedAt": "2026-05-10T12:01:00"
  },
  "timestamp": "2026-05-10T12:01:00"
}
```

### Validation Error (400 BAD REQUEST):
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "Request body contains invalid fields",
  "path": "/tasks",
  "errors": [
    "Title must not be blank",
    "Due date must not be in the past"
  ],
  "timestamp": "2026-05-10T12:01:00"
}
```

### Not Found (404):
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Task not found with id: 99",
  "path": "/tasks/99",
  "timestamp": "2026-05-10T12:01:00"
}
```

---

## Testing Instructions

```bash
# Run all unit tests
mvn test

# Run tests with coverage report (requires JaCoCo plugin addition)
mvn test jacoco:report

# Run a specific test class
mvn test -Dtest=TaskServiceTest

# Run a specific test method
mvn test -Dtest="TaskServiceTest#shouldCreateTaskSuccessfully"
```

---

## Package Structure

```
com.taskmanager
├── TaskManagerApplication.java      ← Entry point
├── controller
│   ├── TaskController.java          ← REST endpoints
│   └── AuthController.java          ← Login / token issuance
├── service
│   └── TaskService.java             ← Service interface
├── service/impl
│   └── TaskServiceImpl.java         ← Business logic
├── repository
│   └── TaskRepository.java          ← Spring Data JPA repository
├── entity
│   └── Task.java                    ← JPA entity
├── dto
│   ├── TaskRequest.java             ← Create/update input
│   ├── TaskResponse.java            ← API output
│   ├── AuthRequest.java             ← Login credentials
│   ├── AuthResponse.java            ← JWT token response
│   └── ApiResponse.java             ← Generic response envelope
├── exception
│   ├── TaskNotFoundException.java   ← 404 trigger
│   ├── ApiError.java                ← Error response DTO
│   └── GlobalExceptionHandler.java  ← @ControllerAdvice handler
├── config
│   ├── SecurityConfig.java          ← Spring Security + JWT setup
│   ├── JwtAuthenticationFilter.java ← Request filter
│   ├── SwaggerConfig.java           ← Springdoc OpenAPI config
│   └── DataInitializer.java         ← Sample data seeder
├── mapper
│   └── TaskMapper.java              ← Entity ↔ DTO conversion
├── enums
│   └── TaskStatus.java              ← PENDING / IN_PROGRESS / COMPLETED
└── util
    └── JwtUtil.java                 ← Token generation & validation
```

---

## Assumptions Made

1. **In-memory storage** — H2 is used as specified; data is lost on restart. Swap to PostgreSQL by updating `application.yml` and adding the `postgresql` driver dependency.
2. **Single admin user** — Credentials are hardcoded in `SecurityConfig`. In production, users would be stored in the database with role management.
3. **No refresh tokens** — JWT tokens expire after 24 hours; re-authentication is required.
4. **No pagination** — `GET /tasks` returns all tasks. For large datasets, add `Pageable` support.
5. **No soft delete** — `DELETE /tasks/{id}` performs a hard delete.

---

## Future Improvements

- [ ] PostgreSQL / production-ready database
- [ ] Integration tests with `@SpringBootTest`
- [ ] JaCoCo code coverage reporting
- [ ] Rate limiting
- [ ] Actuator health/metrics endpoints
- [ ] Docker + Docker Compose configuration
