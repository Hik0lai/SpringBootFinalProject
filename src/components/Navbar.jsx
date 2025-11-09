import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <nav className="bg-content shadow-lg mb-6 border-b border-yellow-200">
      <div className="container mx-auto flex items-center justify-between py-3 px-4">
        <Link to="/" className="font-bold text-lg text-yellow-600">Beehive Monitor</Link>
        <div className="flex items-center gap-4">
          {user ? (
            <>
              <Link to="/">Dashboard</Link>
              <Link to="/inspections">Inspections</Link>
              <Link to="/alerts">Alerts</Link>
              <Link to="/profile">Profile</Link>
              {user.role === "ADMIN" && <Link to="/admin">Admin</Link>}
              <button
                className="ml-2 px-4 py-1 rounded bg-yellow-500 text-white"
                onClick={() => { logout(); navigate("/login"); }}>
                Logout
              </button>
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