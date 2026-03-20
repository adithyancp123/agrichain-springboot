import axios from "axios";

const API_BASE_URL = "http://localhost:8080";

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

