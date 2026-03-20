# AgriChain Frontend

React UI for the AgriChain project.

## Setup
1. Go to `frontend/`
2. Install dependencies:
   - `npm install`

## Run
- `npm start`
- Open: `http://localhost:3000`

## JWT Authentication
- Login page calls `POST http://localhost:8080/auth/login`
- After login, the token is stored in `localStorage`
- An Axios interceptor automatically adds:
  - `Authorization: Bearer <token>`

## API Base URL
Configured in `src/axiosClient.js` (defaults to `http://localhost:8080`).
