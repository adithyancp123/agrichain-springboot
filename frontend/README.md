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
- Login page calls `POST /auth/login` (via `axiosClient`, base URL from `REACT_APP_API_URL`)
- After login, the token is stored in `localStorage`
- An Axios interceptor automatically adds:
  - `Authorization: Bearer <token>`

## API Base URL
Configured in `src/axiosClient.js` using `process.env.REACT_APP_API_URL`.

For deployment (Vercel), set this environment variable in the Vercel project:
- `REACT_APP_API_URL` (example: `https://your-backend-domain.com`)
