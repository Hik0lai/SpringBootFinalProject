import React, { useEffect, useState } from "react";
import axios from "axios";

export default function Settings() {
  const [interval, setInterval] = useState(1);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchSettings = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        setError("Not authenticated. Please log in again.");
        return;
      }

      setLoading(true);
      try {
        const response = await axios.get("http://localhost:8080/api/settings", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setInterval(response.data.measurementIntervalMinutes || 1);
        setError("");
      } catch (err) {
        console.error("Error fetching settings:", err);
        setError("Failed to load settings.");
      } finally {
        setLoading(false);
      }
    };

    fetchSettings();
  }, []);

  const handleSave = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      setError("Not authenticated. Please log in again.");
      return;
    }

    setSaving(true);
    setMessage("");
    setError("");

    try {
      await axios.put(
        "http://localhost:8080/api/settings",
        { measurementIntervalMinutes: interval },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setMessage("Change is saved");
      setTimeout(() => setMessage(""), 3000); // Clear message after 3 seconds
    } catch (err) {
      console.error("Error saving settings:", err);
      setError("Failed to save settings. Please try again.");
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="bg-content rounded-lg p-6 shadow-lg">
        <h1 className="text-2xl font-bold text-yellow-700 mb-5">Settings</h1>
        <div className="text-center py-8">
          <div className="text-lg text-yellow-700">Loading settings...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-content rounded-lg p-6 shadow-lg">
      <h1 className="text-2xl font-bold text-yellow-700 mb-5">Settings</h1>

      {error && (
        <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
          {error}
        </div>
      )}

      {message && (
        <div className="mb-4 p-3 bg-green-100 border border-green-400 text-green-700 rounded">
          {message}
        </div>
      )}

      <div className="bg-white rounded-lg shadow border border-yellow-100 p-6">
        <div className="mb-6">
          <label className="block text-sm font-semibold text-gray-700 mb-2">
            Measurement Interval
          </label>
          <p className="text-sm text-gray-600 mb-4">
            Select how often sensor values should be stored in the database
          </p>
          <select
            className="w-full max-w-xs border px-3 py-2 rounded"
            value={interval}
            onChange={(e) => setInterval(parseInt(e.target.value))}
          >
            <option value="1">1 min</option>
            <option value="2">2 min</option>
            <option value="3">3 min</option>
            <option value="5">5 min</option>
            <option value="10">10 min</option>
            <option value="20">20 min</option>
          </select>
        </div>

        <button
          onClick={handleSave}
          disabled={saving}
          className="bg-yellow-500 text-white px-6 py-2 rounded hover:bg-yellow-600 disabled:bg-gray-400"
        >
          {saving ? "Saving..." : "Save"}
        </button>
      </div>
    </div>
  );
}
