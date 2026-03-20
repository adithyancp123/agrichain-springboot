# AgriChain Backend

Spring Boot REST API for the AgriChain domain (Farmers, Fields, Crops, Distributors, Irrigation Schedules) using:
- In-memory storage (`ArrayList` + manual IDs) for the domain entities
- JWT authentication for `/auth/**` and protecting all other API routes
- Swagger UI via `springdoc-openapi`
- Consistent `ApiResponse<T>` response wrapper

## Tech Stack
- Java 17
- Spring Boot (Web MVC, Validation, Security)
- Springdoc OpenAPI
- JWT (`io.jsonwebtoken`)
- H2 (runtime, for `User` persistence)
- Lombok
- SLF4J logging

## Authentication (JWT)
Endpoints:
- `POST /auth/register` - create a new user
- `POST /auth/login` - login and get a JWT token

After login, call protected APIs with:
- `Authorization: Bearer <token>`

JWT uses `jwt.secret` and `jwt.expirationMs` from `application.properties`.

## How to Run
From the `backend/` folder:
- Windows:
  - `.\mvnw.cmd spring-boot:run`
- macOS/Linux:
  - `./mvnw spring-boot:run`

Server:
- `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## API Endpoints (CRUD)
- Farmers: `/farmers`
- Fields: `/fields`
- Crops: `/crops`
- Distributors: `/distributors`
- Irrigation Schedules: `/irrigations`

## Response Format
Many controllers return `ApiResponse<T>`:
- `message`
- `data`
- `status`

