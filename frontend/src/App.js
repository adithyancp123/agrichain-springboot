import React from "react";
import { BrowserRouter, Routes, Route, Link, Navigate } from "react-router-dom";
import FarmersPage from "./pages/FarmersPage";
import FieldsPage from "./pages/FieldsPage";
import CropsPage from "./pages/CropsPage";
import DashboardPage from "./pages/DashboardPage";
import LoginPage from "./pages/LoginPage";
import "./App.css";

function RequireAuth({ children }) {
  const token = localStorage.getItem("token");
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  return children;
}

function App() {
  return (
    <BrowserRouter>
      <div>
        <nav className="navbar">
          <div className="navbarInner">
            <div className="brand">AgriChain</div>
            <div className="navLinks">
              <Link to="/" className="navLink">
                Farmers
              </Link>
              <Link to="/dashboard" className="navLink">
                Dashboard
              </Link>
              <Link to="/fields" className="navLink">
                Fields
              </Link>
              <Link to="/crops" className="navLink">
                Crops
              </Link>
            </div>
          </div>
        </nav>

        <div className="pageContainer">
          <Routes>
            <Route path="/login" element={<LoginPage />} />

            <Route
              path="/"
              element={
                <RequireAuth>
                  <FarmersPage />
                </RequireAuth>
              }
            />
            <Route
              path="/farmers"
              element={
                <RequireAuth>
                  <FarmersPage />
                </RequireAuth>
              }
            />
            <Route
              path="/dashboard"
              element={
                <RequireAuth>
                  <DashboardPage />
                </RequireAuth>
              }
            />
            <Route
              path="/fields"
              element={
                <RequireAuth>
                  <FieldsPage />
                </RequireAuth>
              }
            />
            <Route
              path="/crops"
              element={
                <RequireAuth>
                  <CropsPage />
                </RequireAuth>
              }
            />
          </Routes>
        </div>
      </div>
    </BrowserRouter>
  );
}

export default App;