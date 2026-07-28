import { createContext, useContext, useState } from "react";
import apiClient from "../api/client";

const AuthContext = createContext(null);

/**
 * Wraps the whole app (see main.jsx). Any component can call useAuth() to read
 * the logged-in user or trigger login/logout, instead of prop-drilling that
 * state through every page.
 */
export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem("lucidpoint_user");
    return stored ? JSON.parse(stored) : null;
  });

  async function login(email, password) {
    const { data } = await apiClient.post("/auth/login", { email, password });
    persistSession(data);
  }

  async function register(fullName, email, password, role) {
    const { data } = await apiClient.post("/auth/register", { fullName, email, password, role });
    persistSession(data);
  }

  function persistSession(authResponse) {
    const { token, email, role, fullName } = authResponse;
    localStorage.setItem("lucidpoint_token", token);
    const userInfo = { email, role, fullName };
    localStorage.setItem("lucidpoint_user", JSON.stringify(userInfo));
    setUser(userInfo);
  }

  function logout() {
    localStorage.removeItem("lucidpoint_token");
    localStorage.removeItem("lucidpoint_user");
    setUser(null);
  }

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within an AuthProvider");
  return ctx;
}
