import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

export default function Dashboard() {
  const [hives, setHives] = useState([]);
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/hives", {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      })
      .then((res) => setHives(res.data))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="bg-content rounded-lg p-6 shadow-lg">
      <h1 className="text-2xl font-bold mb-4 text-yellow-700">Your Hives</h1>
      <div className="mb-4">
        <Link
          to="/add-hive"
          className="bg-yellow-500 px-4 py-2 rounded text-white hover:bg-yellow-600">
          + Add Hive
        </Link>
      </div>
      {loading ? (
        <div>Loading...</div>
      ) : hives.length === 0 ? (
        <div>No hives found.</div>
      ) : (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {hives.map((hive) => (
            <Link
              to={`/hives/${hive.id}`}
              key={hive.id}
              className="bg-white p-4 rounded-lg shadow hover:shadow-xl border border-yellow-100 transition-shadow relative"
            >
              <div className="text-lg font-semibold text-yellow-700">
                {hive.name}
              </div>
              <div className="text-gray-600 mb-2">{hive.location}</div>
              <div className="text-xs text-gray-400">ID: {hive.id}</div>
              <div className="flex gap-3 mt-2 text-sm">
                <span>Queen: {hive.queen ? hive.queen : "?"}</span>
                <span>|</span>
                <span>Sensors: {hive.sensors ? hive.sensors.length : 0}</span>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}