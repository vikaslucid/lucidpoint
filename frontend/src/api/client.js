import axios from "axios";

// Reads from a Vite env var at build time (set VITE_API_URL in a .env file).
// Falls back to localhost for local development against the Spring Boot backend.
const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

const apiClient = axios.create({
  baseURL: API_BASE_URL,
});

// Runs before every outgoing request: attaches "Authorization: Bearer <token>"
// if we have one stored, so the backend's JwtAuthenticationFilter can identify the user.
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("lucidpoint_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// If the backend ever returns 401 (expired/invalid token), clear local state and
// bounce back to login instead of leaving the user stuck on a broken dashboard.
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("lucidpoint_token");
      localStorage.removeItem("lucidpoint_user");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default apiClient;
