import React, { useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import axiosClient from "../axiosClient";

export default function LoginPage() {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const existingToken = localStorage.getItem("token");
  if (existingToken) {
    return <Navigate to="/dashboard" replace />;
  }

  const onLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const payload = { username, password };
      // Debug log (development only): confirms payload shape sent to backend.
      if (process.env.NODE_ENV !== "production") {
        // eslint-disable-next-line no-console
        console.info("Login request payload:", {
          username: payload.username,
          password: "*".repeat(payload.password?.length ?? 0),
        });
      }

      const res = await axiosClient.post("/auth/login", payload);

      if (process.env.NODE_ENV !== "production") {
        // eslint-disable-next-line no-console
        console.info("Login response status:", res.status);
      }

      const token = res.data?.data;
      if (!token) {
        throw new Error("Token not found in response");
      }

      localStorage.setItem("token", token);
      navigate("/dashboard", { replace: true });
    } catch (err) {
      const message =
        err?.response?.data?.message ||
        err?.response?.data?.error?.message ||
        err?.message ||
        "Login failed";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card">
      <h2 className="cardTitle">Login</h2>
      {error ? <p className="errorText">{error}</p> : null}

      <form onSubmit={onLogin}>
        <div className="formRow" style={{ gridTemplateColumns: "1fr 1fr" }}>
          <input
            className="input"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <input
            className="input"
            placeholder="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <button className="btn" style={{ gridColumn: "1 / -1" }} type="submit" disabled={loading}>
            {loading ? "Logging in..." : "Login"}
          </button>
        </div>
      </form>
    </div>
  );
}

