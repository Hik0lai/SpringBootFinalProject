import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  async function handleRegister(e) {
    e.preventDefault();
    setError("");
    try {
      await register({ email, name, password });
      navigate("/");
    } catch {
      setError("Could not register. Try a different email.");
    }
  }

  return (
    <div className="max-w-sm mx-auto bg-content p-8 mt-16 rounded-lg shadow-xl border border-yellow-200">
      <h1 className="text-2xl font-semibold mb-4 text-yellow-700">Register</h1>
      {error && <div className="mb-3 text-red-500 text-sm">{error}</div>}
      <form onSubmit={handleRegister} className="space-y-4">
        <input className="w-full border px-3 py-2 rounded" placeholder="Name" value={name} onChange={e=>setName(e.target.value)} required/>
        <input className="w-full border px-3 py-2 rounded" placeholder="Email" value={email} onChange={e=>setEmail(e.target.value)} required />
        <input className="w-full border px-3 py-2 rounded" type="password" placeholder="Password" value={password} onChange={e=>setPassword(e.target.value)} required />
        <button type="submit" className="w-full bg-yellow-500 text-white py-2 rounded hover:bg-yellow-600 transition">Register</button>
      </form>
      <div className="text-sm mt-4 text-gray-700">
        Already have an account? <Link to="/login" className="text-yellow-700 underline">Login</Link>
      </div>
    </div>
  );
}
