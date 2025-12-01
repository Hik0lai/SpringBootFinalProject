import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

export default function Dashboard() {
  const [hives, setHives] = useState([]);
  const [sensorData, setSensorData] = useState({});
  const [loading, setLoading] = useState(true);
  const [updatingSensors, setUpdatingSensors] = useState(false);
  const [error, setError] = useState("");
  const { user } = useAuth();

  const fetchHives = () => {
    const token = localStorage.getItem("token");
    if (!token) {
      setError("Not authenticated. Please log in again.");
      setLoading(false);
      return;
    }

    setLoading(true);
    axios
      .get("http://localhost:8080/api/hives", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((res) => {
        // Ensure hives is always an array
        const hivesData = res.data;
        if (Array.isArray(hivesData)) {
          // Clean up any circular references that might have been serialized
          const cleanedHives = hivesData.map(hive => ({
            id: hive.id,
            name: hive.name,
            location: hive.location,
            queen: hive.queen,
            sensors: hive.sensors ? (Array.isArray(hive.sensors) ? hive.sensors.length : 0) : 0
          }));
          setHives(cleanedHives);
        } else {
          console.warn("API returned non-array data:", hivesData);
          setHives([]);
        }
        setError("");
        // Fetch sensor data after hives are loaded
        fetchSensorData();
      })
      .catch((err) => {
        console.error("Error fetching hives:", err);
        let errorMessage = "Failed to load hives. Please try again.";
        
        if (err.code === "ERR_NETWORK" || err.message === "Network Error") {
          errorMessage = "Cannot connect to server. Please make sure Spring Boot is running on http://localhost:8080";
        } else if (err.response?.status === 401 || err.response?.status === 403) {
          errorMessage = "Authentication failed. Please log in again.";
          localStorage.removeItem("token");
        } else if (err.response?.data?.error) {
          errorMessage = err.response.data.error;
        } else if (err.response?.data?.message) {
          errorMessage = err.response.data.message;
        }
        
        setError(errorMessage);
        setHives([]); // Always ensure it's an array
      })
      .finally(() => setLoading(false));
  };

  const fetchSensorData = () => {
    const token = localStorage.getItem("token");
    if (!token) return;

    axios
      .get("http://localhost:8080/api/sensors/realtime-data", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setSensorData(res.data || {});
      })
      .catch((err) => {
        console.error("Error fetching sensor data:", err);
      });
  };

  useEffect(() => {
    fetchHives();
    fetchSensorData(); // Initial fetch
    
    // Set up auto-refresh every 1 minute (60000 ms)
    const interval = setInterval(() => {
      fetchSensorData();
    }, 60000);

    return () => clearInterval(interval); // Cleanup on unmount
  }, []);

  const handleUpdateSensors = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      setError("Not authenticated. Please log in again.");
      return;
    }

    setUpdatingSensors(true);
    setError("");

    try {
      // Try sending empty object instead of null
      const response = await axios.post(
        "http://localhost:8080/api/sensors/update",
        {}, // Send empty object
        {
          headers: { 
            Authorization: `Bearer ${token}`,
          },
        }
      );

      // Update sensor data from response
      if (response.data?.sensorData) {
        setSensorData(response.data.sensorData);
        alert("Sensor data updated successfully!");
      } else {
        // If response doesn't include sensorData, fetch it separately
        fetchSensorData();
      }

      // Show success message
      const updatedCount = response.data?.updatedHives || 0;
      if (updatedCount > 0) {
        setError(""); // Clear any previous errors
      }
    } catch (err) {
      console.error("Error updating sensors:", err);
      
      let errorMessage = "Failed to update sensors. Please try again.";
      
      if (err.response?.status === 401) {
        errorMessage = "Authentication failed. Please log in again.";
        localStorage.removeItem("token");
        window.location.href = "/login";
      } else if (err.response?.data) {
        errorMessage = err.response.data.error 
          || err.response.data.message 
          || err.response.data.errorMessage
          || errorMessage;
      } else if (err.message) {
        errorMessage = err.message;
      }
      
      if (err.code === "ERR_NETWORK" || err.message === "Network Error") {
        errorMessage = "Cannot connect to server. Please make sure the backend is running.";
      }
      
      setError(errorMessage);
    } finally {
      setUpdatingSensors(false);
    }
  };

  const handleDeleteHive = async (hiveId, hiveName) => {
    // Show confirmation prompt
    const confirmed = window.confirm(
      `Are you sure you want to delete the hive "${hiveName}"?\n\nThis action cannot be undone.`
    );

    if (!confirmed) {
      return;
    }

    const token = localStorage.getItem("token");
    if (!token) {
      setError("Not authenticated. Please log in again.");
      return;
    }

    try {
      await axios.delete(`http://localhost:8080/api/hives/${hiveId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      
      // Refresh the hive list
      fetchHives();
    } catch (err) {
      console.error("Error deleting hive:", err);
      const errorMessage = err.response?.data?.error 
        || err.response?.data?.message 
        || "Failed to delete hive. Please try again.";
      alert(`Error: ${errorMessage}`);
    }
  };

  return (
    <div className="bg-content rounded-lg p-6 shadow-lg">
      <h1 className="text-2xl font-bold mb-4 text-yellow-700">Your Hives</h1>
      {error && (
        <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
          {error}
        </div>
      )}
      <div className="mb-4 flex gap-3">
        <Link
          to="/add-hive"
          className="bg-yellow-500 px-4 py-2 rounded text-white hover:bg-yellow-600">
          + Add Hive
        </Link>
        <button
          onClick={handleUpdateSensors}
          disabled={loading || updatingSensors || hives.length === 0}
          className="bg-blue-500 px-4 py-2 rounded text-white hover:bg-blue-600 disabled:bg-gray-400 disabled:cursor-not-allowed">
          {updatingSensors ? "Updating..." : "🔄 Update Sensors"}
        </button>
      </div>
      {loading ? (
        <div className="text-center py-8">
          <div className="text-lg text-yellow-700">Loading hives...</div>
        </div>
      ) : error && !Array.isArray(hives) ? (
        <div className="text-center py-8">
          <div className="text-gray-600">{error}</div>
        </div>
      ) : Array.isArray(hives) && hives.length > 0 ? (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {hives.map((hive) => (
            <div
              key={hive.id}
              className="group relative bg-white rounded-lg shadow-lg hover:shadow-2xl border border-yellow-200 transition-all duration-300 overflow-hidden"
            >
              {/* Delete Button */}
              <button
                onClick={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  handleDeleteHive(hive.id, hive.name);
                }}
                className="absolute top-2 right-2 z-10 bg-orange-500 hover:bg-orange-600 text-white p-2 rounded-full shadow-lg transition-all duration-200 hover:scale-110"
                title="Delete hive"
              >
                <svg 
                  xmlns="http://www.w3.org/2000/svg" 
                  className="h-4 w-4" 
                  fill="none" 
                  viewBox="0 0 24 24" 
                  stroke="currentColor"
                >
                  <path 
                    strokeLinecap="round" 
                    strokeLinejoin="round" 
                    strokeWidth={2} 
                    d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" 
                  />
                </svg>
              </button>

              {/* Beehive Image - Clickable Link */}
              <Link to={`/hives/${hive.id}`} className="block">
                <div className="relative h-48 w-full overflow-hidden bg-gradient-to-br from-yellow-100 to-yellow-200">
                  <img
                    src="/hive/hive.jpg"
                    alt={`Beehive ${hive.name}`}
                    className="w-full h-full object-contain object-center group-hover:scale-105 transition-transform duration-300"
                    onError={(e) => {
                      // Fallback if image doesn't exist
                      e.target.style.display = 'none';
                      e.target.nextSibling.style.display = 'flex';
                    }}
                  />
                  {/* Fallback gradient if image doesn't exist */}
                  <div className="hidden w-full h-full bg-gradient-to-br from-yellow-400 via-yellow-500 to-yellow-600 items-center justify-center">
                    <div className="text-6xl">🏠</div>
                  </div>
                  
                  {/* Overlay with information */}
                  <div className="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent">
                    <div className="absolute bottom-0 left-0 right-0 p-4 text-white">
                      <h3 className="text-xl font-bold mb-1 drop-shadow-lg">{hive.name}</h3>
                      <p className="text-sm text-yellow-200 drop-shadow">{hive.location}</p>
                    </div>
                  </div>
                </div>
              </Link>
              
              {/* Information Section */}
              <div className="p-4 bg-white">
                <div className="flex items-center justify-between mb-3">
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-semibold text-yellow-700 bg-yellow-100 px-2 py-1 rounded">
                      ID: {hive.id}
                    </span>
                  </div>
                </div>
                
                {/* Sensor Data Display */}
                {sensorData[hive.id] && (
                  <div className="grid grid-cols-2 gap-2 text-xs">
                    <div className="bg-blue-50 p-2 rounded border border-blue-200">
                      <div className="font-semibold text-blue-700">🌡️ Int. Temp</div>
                      <div className="text-blue-900">{sensorData[hive.id].temperature}°C</div>
                    </div>
                    <div className="bg-cyan-50 p-2 rounded border border-cyan-200">
                      <div className="font-semibold text-cyan-700">🌡️ Ext. Temp</div>
                      <div className="text-cyan-900">{sensorData[hive.id].externalTemperature}°C</div>
                    </div>
                    <div className="bg-green-50 p-2 rounded border border-green-200">
                      <div className="font-semibold text-green-700">💧 Humidity</div>
                      <div className="text-green-900">{sensorData[hive.id].humidity}%</div>
                    </div>
                    <div className="bg-orange-50 p-2 rounded border border-orange-200">
                      <div className="font-semibold text-orange-700">💨 CO₂</div>
                      <div className="text-orange-900">{sensorData[hive.id].co2} ppm</div>
                    </div>
                    <div className="bg-purple-50 p-2 rounded border border-purple-200">
                      <div className="font-semibold text-purple-700">🔊 Sound Level</div>
                      <div className="text-purple-900">{sensorData[hive.id].soundLevel} dB</div>
                    </div>
                    <div className="bg-yellow-50 p-2 rounded border border-yellow-200">
                      <div className="font-semibold text-yellow-700">⚖️ Weight</div>
                      <div className="text-yellow-900">{sensorData[hive.id].weight} kg</div>
                    </div>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="text-center py-8">
          <div className="text-gray-600 mb-4">No hives found.</div>
          <Link
            to="/add-hive"
            className="inline-block bg-yellow-500 px-6 py-2 rounded text-white hover:bg-yellow-600">
            Create Your First Hive
          </Link>
        </div>
      )}
    </div>
  );
}