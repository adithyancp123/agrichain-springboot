import axios from "axios";

// CRA env vars must start with `REACT_APP_` to be exposed to the browser.
// For deployment, set `REACT_APP_API_URL` (example: https://your-backend.com).
// Trailing slash is removed to avoid double `//` when calling `axiosClient.get("/path")`.
const API_BASE_URL = (process.env.REACT_APP_API_URL || "https://agrichain-springboot.onrender.com").replace(/\/$/, "");

const axiosClient = axios.create({
  baseURL: API_BASE_URL,
});

// Automatically attach JWT token to all requests (if present).
axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers = {
      ...(config.headers ?? {}),
      Authorization: `Bearer ${token}`,
    };
  }
  return config;
});

// Global response interceptor for error handling, descriptive network messages, and auto-logout.
axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // 1. Auto-logout on 401 Unauthorized (unless it is the login request itself)
    if (error.response && error.response.status === 401) {
      const isLoginRequest = error.config?.url?.includes("/auth/login");
      if (!isLoginRequest) {
        localStorage.removeItem("token");
        window.location.href = "/login";
      }
    }
    // 2. Map offline/network/CORS errors to user-friendly messages
    else if (!error.response) {
      error.message = "Network Error: Unable to reach the backend server. The backend might be starting up (cold start) or offline.";
    }
    return Promise.reject(error);
  }
);

export default axiosClient;

