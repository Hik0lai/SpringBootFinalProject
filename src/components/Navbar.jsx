import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  // Debug: Log when Navbar renders
  console.log("Navbar rendering, user:", user?.email || "no user");

  return (
    <nav className="bg-content shadow-lg mb-6 border-b border-yellow-200" style={{ position: 'relative', zIndex: 1000 }}>
      <div className="container mx-auto flex items-center justify-between py-3 px-4">
        <Link to="/" className="font-bold text-lg text-yellow-600">Beehive Monitor</Link>
        <div className="flex items-center gap-4">
          {user ? (
            <>
              <Link to="/">Dashboard</Link>
              {user.role === "ADMIN" && <Link to="/graphics">Graphics</Link>}
              <Link to="/weather">Weather</Link>
              {user.role === "ADMIN" && <Link to="/inspections">Inspections</Link>}
              {user.role === "ADMIN" && <Link to="/alerts">Alerts</Link>}
              <Link to="/profile">Profile</Link>
              {user.role === "ADMIN" && <Link to="/settings">Settings</Link>}
              {user.role === "ADMIN" && <Link to="/admin">Admin</Link>}
              <div className="flex items-center gap-3 ml-12">
                <span className="text-gray-700 font-medium">
                  Welcome, {user.name || user.email}!
                </span>
                <button
                  className="px-4 py-1 rounded bg-yellow-500 text-white hover:bg-yellow-600 transition"
                  onClick={() => { logout(); navigate("/login"); }}>
                  Logout
                </button>
              </div>
            </>
          ) : (
            <>
              <Link to="/login">Login</Link>
              <Link to="/register">Register</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}