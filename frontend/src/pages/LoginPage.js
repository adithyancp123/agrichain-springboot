import React, { useState } from "react";
import axios from "axios";
import { Navigate, useNavigate } from "react-router-dom";

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
      const res = await axios.post("http://localhost:8080/auth/login", {
        username,
        password,
      });

      const token = res.data?.data;
      if (!token) {
        throw new Error("Token not found in response");
      }

      localStorage.setItem("token", token);
      navigate("/dashboard", { replace: true });
    } catch (err) {
      console.error("Login failed:", err);
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

