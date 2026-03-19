# AgriChain AgriApp

AgriChain AgriApp is a Spring Boot REST API for managing a simple agricultural domain with in-memory persistence. It provides CRUD operations for core entities (Farmers, Fields, Crops, Distributors, and Irrigation Schedules), validates inputs, documents the API with Swagger, and secures endpoints using HTTP Basic Authentication.

## Features
- RESTful CRUD endpoints for all entities
- In-memory storage using `ArrayList` with manually generated IDs
- Relationship validation using IDs (no database)
  - `Farmer` has many `Field` (via `farmerId`)
  - `Field` has one `Crop` (via `fieldId`, unique per field)
  - `Field` has one `IrrigationSchedule` (via `fieldId`, unique per field)
  - `Distributor` has many `Farmer` derived through `interestedCropType`
  - Cascade deletes for Farmer -> Fields -> (Crop, IrrigationSchedule)
- Jakarta Bean Validation (`@Valid`) + global exception handling
- Consistent API responses via `ApiResponse<T>` (`message`, `data`, `status`)
- Swagger/OpenAPI UI using `springdoc-openapi`
- Spring Security HTTP Basic Authentication for all endpoints
- SLF4J logging for service create/update/delete operations

## Tech Stack
- Java 17
- Spring Boot (Web MVC, Validation, Security)
- Springdoc OpenAPI (Swagger UI)
- Lombok
- Jakarta Validation
- SLF4J (LoggerFactory)
- Maven (Maven Wrapper)

## API Endpoints
All endpoints require HTTP Basic Authentication.

### Farmers
- `POST /farmers`
- `GET /farmers`
- `GET /farmers/{id}`
- `PUT /farmers/{id}`
- `DELETE /farmers/{id}`

### Fields
- `POST /fields`
- `GET /fields`
- `GET /fields/{id}`
- `PUT /fields/{id}`
- `DELETE /fields/{id}`

### Crops
- `POST /crops`
- `GET /crops`
- `GET /crops/{id}`
- `PUT /crops/{id}`
- `DELETE /crops/{id}`

### Distributors
- `POST /distributors`
- `GET /distributors`
- `GET /distributors/{id}`
- `PUT /distributors/{id}`
- `DELETE /distributors/{id}`

### Irrigation Schedules
- `POST /irrigations`
- `GET /irrigations`
- `GET /irrigations/{id}`
- `PUT /irrigations/{id}`
- `DELETE /irrigations/{id}`

## Authentication (Basic Auth)
In-memory user credentials:
- Username: `admin`
- Password: `admin123`

Tip: Swagger UI is at `/swagger-ui.html`, but API calls from your browser still need Basic Auth.

Example `curl` usage:
```bash
curl -u admin:admin123 -H "Content-Type: application/json" ^
  -d "{\"name\":\"John\",\"region\":\"North\",\"experienceYears\":3}" ^
  http://localhost:8080/farmers
```

## How to Run
From the project root (`agriapp/`):
```bash
.\mvnw.cmd spring-boot:run
```

The application starts on:
- `http://localhost:8080`

Swagger UI:
- `http://localhost:8080/swagger-ui.html`

## Response Format
Most controller responses are wrapped in `ApiResponse<T>`:
- `message`: human readable message
- `data`: returned resource(s)
- `status`: numeric HTTP status code

Example:
```json
{
  "message": "Success",
  "data": { "id": 1, "name": "John", "region": "North", "experienceYears": 3 },
  "status": 200
}
```

## Screenshots
Add screenshots here when you run the app locally, for example:
- Swagger UI landing page (`/swagger-ui.html`)
- Example POST/PUT request with validation errors
- Example authenticated request responses

