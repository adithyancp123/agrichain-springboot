# AgriChain

AgriChain is a beginner-friendly full-stack agriculture management app built with a **Spring Boot REST API** and a **React frontend**. It provides CRUD management for **Farmers, Fields, and Crops**, a **secure authenticated UI** using **JWT**, and a **dashboard** with charts.

## Features
- **Farmers**
  - CRUD operations: create, list, update, delete
  - Search/filter support in the frontend (by name and region)
- **Fields**
  - CRUD operations
  - Links to farmers via `farmerId` (no relational DB for domain data)
- **Crops**
  - CRUD operations
  - Links to fields via `fieldId`
- **Dashboard**
  - Total counts (farmers, fields, crops)
  - Charts:
    - Farmers by region
    - Crops by type
- **Auth (JWT)**
  - `POST /auth/register` for registering users
  - `POST /auth/login` for logging in and getting a JWT token
  - Protected API routes (all endpoints except `/auth/**`)
  - React stores the token in `localStorage` and sends it as `Authorization: Bearer <token>`

## Tech Stack
- Backend: **Java 17**, **Spring Boot**, **Spring Security**, **JWT** (`io.jsonwebtoken`), **H2** (in-memory for users), **Swagger** (`springdoc-openapi`), **Lombok**
- Frontend: **React**, **Axios**, **React Router**, **Chart.js** (`react-chartjs-2`)

## Project Structure
- `backend/` - Spring Boot REST API
- `frontend/` - React frontend (create-react-app)

## Setup & Run

### 1) Start Backend
1. Open a terminal at the repo root
2. Run:
   - Windows:
     - `cd backend`
     - `.\mvnw.cmd spring-boot:run`
   - macOS/Linux:
     - `cd backend`
     - `./mvnw spring-boot:run`
3. Backend runs at: `http://localhost:8080`
4. Swagger UI: `http://localhost:8080/swagger-ui.html`

### 2) Start Frontend
1. Run:
   - `cd frontend`
   - `npm install`
   - `npm start`
2. Frontend runs at: `http://localhost:3000`

## API / Auth Notes
- The backend wraps most responses using `ApiResponse<T>` with:
  - `message`, `data`, `status`
- After login, the React app attaches the token automatically using an Axios interceptor:
  - `Authorization: Bearer <token>`

## Screenshots
Add screenshots here (for example):
- Farmers page table + search/filter
- Login/register page
- Dashboard charts (farmers by region, crops by type)
- Swagger UI screenshot

## Future Improvements
- Add persistent storage for domain entities (instead of in-memory lists)
- Role-based authorization (e.g., Admin vs User) on APIs
- Add more dashboard visualizations and date-based filtering
- Improve validation and error UX across all pages
- Add automated tests (backend unit/integration + frontend component tests)
