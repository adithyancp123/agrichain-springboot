import axios from "axios";

// CRA env vars must start with `REACT_APP_` to be exposed to the browser.
// For deployment, set `REACT_APP_API_URL` (example: https://your-backend.com).
// Trailing slash is removed to avoid double `//` when calling `axiosClient.get("/path")`.
const API_BASE_URL = (process.env.REACT_APP_API_URL ?? "").replace(/\/$/, "");

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

export default axiosClient;

