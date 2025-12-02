import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link } from "react-router-dom";

export default function AlertsPage() {
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchAlerts = () => {
    const token = localStorage.getItem("token");
    if (!token) {
      setLoading(false);
      return;
    }

    axios
      .get("http://localhost:8080/api/alerts", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setAlerts(res.data || []))
      .catch(err => console.error("Error fetching alerts:", err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchAlerts();
    
    // Auto-refresh alerts every 1 minute to update trigger status
    const interval = setInterval(() => {
      fetchAlerts();
    }, 60000);

    return () => clearInterval(interval);
  }, []);

  const handleDelete = async (alertId, alertName) => {
    const confirmed = window.confirm(
      `Are you sure you want to delete the alert "${alertName}"?\n\nThis action cannot be undone.`
    );

    if (!confirmed) {
      return;
    }

    const token = localStorage.getItem("token");
    if (!token) return;

    try {
      await axios.delete(`http://localhost:8080/api/alerts/${alertId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchAlerts(); // Refresh list
    } catch (err) {
      console.error("Error deleting alert:", err);
      alert("Failed to delete alert. Please try again.");
    }
  };

  const handleReset = async (alertId) => {
    const token = localStorage.getItem("token");
    if (!token) return;

    try {
      await axios.post(`http://localhost:8080/api/alerts/${alertId}/reset`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchAlerts(); // Refresh list
    } catch (err) {
      console.error("Error resetting alert:", err);
      alert("Failed to reset alert. Please try again.");
    }
  };

  const formatTriggerConditions = (conditionsJson) => {
    if (!conditionsJson) return "No conditions";
    try {
      const conditions = JSON.parse(conditionsJson);
      return conditions.map(c => {
        let param = c.parameter || "";
        // Map parameter names to display names
        if (param.toLowerCase() === "temperature") {
          param = "Int. Temperature";
        } else if (param.toLowerCase() === "externaltemperature" || param.toLowerCase() === "ext. temperature" || param.toLowerCase() === "ext temperature") {
          param = "Ext. Temperature";
        } else if (param.toLowerCase() === "humidity") {
          param = "Humidity";
        } else if (param.toLowerCase() === "co2") {
          param = "CO₂";
        } else if (param.toLowerCase() === "sound" || param.toLowerCase() === "soundlevel") {
          param = "Sound Level";
        } else if (param.toLowerCase() === "weight") {
          param = "Weight";
        }
        const op = c.operator || ">";
        const val = c.value || "";
        return `${param} ${op} ${val}`;
      }).join(" AND ");
    } catch (e) {
      return "Invalid conditions";
    }
  };

  return (
    <div className="bg-content rounded-lg p-6 shadow-lg">
      <div className="flex items-center justify-between mb-5">
        <h1 className="text-2xl font-bold text-yellow-700">Alerts</h1>
        <Link to="/add-alert" className="bg-yellow-500 px-4 py-2 rounded text-white hover:bg-yellow-600">
          + Add Alert
        </Link>
      </div>
      {loading ? (
        <div className="text-center py-8">
          <div className="text-lg text-yellow-700">Loading alerts...</div>
        </div>
      ) : alerts.length === 0 ? (
        <div className="text-center py-8">
          <div className="text-gray-600 mb-4">No alerts configured.</div>
          <Link
            to="/add-alert"
            className="inline-block bg-yellow-500 px-6 py-2 rounded text-white hover:bg-yellow-600">
            Create Your First Alert
          </Link>
        </div>
      ) : (
        <div className="bg-white rounded-lg shadow border border-yellow-100">
          <table className="w-full text-left">
            <thead>
              <tr className="text-yellow-700 bg-yellow-50">
                <th className="p-3 w-24">Status</th>
                <th className="p-3">Alert Name</th>
                <th className="p-3">Hive</th>
                <th className="p-3">Conditions</th>
                <th className="p-3">Created</th>
                <th className="p-3"></th>
              </tr>
            </thead>
            <tbody>
              {alerts.map((alert) => (
                <tr key={alert.id} className="border-t hover:bg-yellow-50">
                  <td className="p-3">
                    <div className="flex items-center gap-2">
                      <div
                        className={`w-4 h-4 rounded-full flex-shrink-0 ${
                          alert.isTriggered ? "bg-red-500" : "bg-green-500"
                        }`}
                        title={alert.isTriggered ? "Alert Triggered" : "Alert Normal"}
                      ></div>
                      {alert.isTriggered && (
                        <button
                          onClick={() => handleReset(alert.id)}
                          className="text-[10px] font-medium bg-orange-500 hover:bg-orange-600 text-white px-2 py-0.5 rounded transition flex-shrink-0 whitespace-nowrap shadow-sm"
                          title="Reset Alert Status"
                        >
                          Reset
                        </button>
                      )}
                    </div>
                  </td>
                  <td className="p-3 font-semibold">{alert.name}</td>
                  <td className="p-3">{alert.hiveName}</td>
                  <td className="p-3 text-sm text-gray-600">
                    {formatTriggerConditions(alert.triggerConditions)}
                  </td>
                  <td className="p-3 text-sm text-gray-500">
                    {new Date(alert.createdAt).toLocaleDateString()}
                  </td>
                  <td className="p-3">
                    <div className="flex gap-2">
                      <Link
                        to={`/edit-alert/${alert.id}`}
                        className="text-blue-600 hover:text-blue-800 hover:underline text-sm"
                      >
                        Edit
                      </Link>
                      <button
                        onClick={() => handleDelete(alert.id, alert.name)}
                        className="text-red-600 hover:text-red-800 hover:underline text-sm"
                      >
                        Remove
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}