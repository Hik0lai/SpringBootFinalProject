import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  async function handleLogin(e) {
    e.preventDefault();
    setError("");
    try {
      await login(email, password);
      navigate("/");
    } catch {
      setError("Invalid credentials");
    }
  }

  return (
    <div className="max-w-sm mx-auto bg-content p-8 mt-16 rounded-lg shadow-xl border border-yellow-200">
      <h1 className="text-2xl font-semibold mb-4 text-yellow-700">Login</h1>
      {error && <div className="mb-3 text-red-500 text-sm">{error}</div>}
      <form onSubmit={handleLogin} className="space-y-4">
        <input
          className="w-full border px-3 py-2 rounded"
          placeholder="Email"
          value={email}
          onChange={e => setEmail(e.target.value)}
          required />
        <input
          className="w-full border px-3 py-2 rounded"
          type="password"
          placeholder="Password"
          value={password}
          onChange={e => setPassword(e.target.value)}
          required />
        <button type="submit" className="w-full bg-yellow-500 text-white py-2 rounded hover:bg-yellow-600 transition">Login</button>
      </form>
      <div className="text-sm mt-4 text-gray-700">
        No account? <Link to="/register" className="text-yellow-700 underline">Register here</Link>
      </div>
    </div>
  );
}