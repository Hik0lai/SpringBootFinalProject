import React, { useEffect, useState } from "react";
import axios from "axios";

export default function AlertsPage() {
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/alerts", {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      })
      .then((res) => setAlerts(res.data))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="bg-content rounded-lg p-6 shadow-lg">
      <h1 className="text-2xl font-bold text-yellow-700 mb-4">Alerts</h1>
      {loading ? (
        <div>Loading...</div>
      ) : alerts.length === 0 ? (
        <div>No alerts.</div>
      ) : (
        <div className="space-y-3">
          {alerts.map((alert) => (
            <div
              key={alert.id}
              className={`p-4 rounded-lg shadow border bg-red-50 border-red-400 text-sm`}
            >
              <div className="font-semibold text-red-700">{alert.title}</div>
              <div>{alert.message}</div>
              <div className="text-xs mt-1 text-gray-500">{alert.createdAt}</div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}