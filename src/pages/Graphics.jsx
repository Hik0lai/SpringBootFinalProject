import React, { useState, useEffect } from "react";
import axios from "axios";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import { Line } from "react-chartjs-2";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

export default function Graphics() {
  const [hives, setHives] = useState([]);
  const [selectedHive, setSelectedHive] = useState("");
  const [selectedPeriod, setSelectedPeriod] = useState("1");
  const [customDays, setCustomDays] = useState("");
  const [selectedParams, setSelectedParams] = useState({
    temperature: false,
    externalTemperature: false,
    humidity: false,
    soundLevel: false,
    weight: false,
    co2: false,
  });
  const [chartData, setChartData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      setError("Not authenticated. Please log in again.");
      return;
    }

    axios
      .get("http://localhost:8080/api/hives", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setHives(res.data || []))
      .catch((err) => {
        console.error("Error fetching hives:", err);
        setError("Failed to load hives.");
      });
  }, []);

  const handleParamChange = (param) => {
    setSelectedParams({
      ...selectedParams,
      [param]: !selectedParams[param],
    });
  };

  const handleAdd = async () => {
    if (!selectedHive) {
      setError("Please select a beehive.");
      return;
    }

    const selectedCount = Object.values(selectedParams).filter(Boolean).length;
    if (selectedCount === 0) {
      setError("Please select at least one parameter to display.");
      return;
    }

    let days = parseInt(selectedPeriod);
    if (selectedPeriod === "custom") {
      days = parseInt(customDays);
      if (isNaN(days) || days <= 0) {
        setError("Please enter a valid number of days for custom period.");
        return;
      }
    }

    setLoading(true);
    setError("");

    const token = localStorage.getItem("token");
    if (!token) {
      setError("Not authenticated. Please log in again.");
      setLoading(false);
      return;
    }

    try {
      const response = await axios.get(
        `http://localhost:8080/api/graphics/historical-data?hiveId=${selectedHive}&days=${days}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      const data = response.data || [];
      if (data.length === 0) {
        setError("No data available for the selected period. Please wait for sensor data to be collected. Data is saved every minute by default (configurable in Settings).");
        setChartData(null);
        setLoading(false);
        return;
      }

      // Prepare chart data
      const labels = data.map((item) => {
        const date = new Date(item.timestamp);
        return date.toLocaleString("en-US", {
          month: "short",
          day: "numeric",
          hour: "2-digit",
          minute: "2-digit",
          hour12: false,
        });
      });

      // Create separate chart data for each selected parameter
      const charts = [];

      if (selectedParams.temperature) {
        charts.push({
          title: "Int. Temperature",
          unit: "°C",
          data: {
            labels,
            datasets: [{
              label: "Int. Temperature (°C)",
              data: data.map((item) => item.temperature),
              borderColor: "rgb(59, 130, 246)",
              backgroundColor: "rgba(59, 130, 246, 0.1)",
              tension: 0.4,
            }],
          },
        });
      }

      if (selectedParams.externalTemperature) {
        charts.push({
          title: "Ext. Temperature",
          unit: "°C",
          data: {
            labels,
            datasets: [{
              label: "Ext. Temperature (°C)",
              data: data.map((item) => item.externalTemperature),
              borderColor: "rgb(14, 165, 233)",
              backgroundColor: "rgba(14, 165, 233, 0.1)",
              tension: 0.4,
            }],
          },
        });
      }

      if (selectedParams.humidity) {
        charts.push({
          title: "Humidity",
          unit: "%",
          data: {
            labels,
            datasets: [{
              label: "Humidity (%)",
              data: data.map((item) => item.humidity),
              borderColor: "rgb(34, 197, 94)",
              backgroundColor: "rgba(34, 197, 94, 0.1)",
              tension: 0.4,
            }],
          },
        });
      }

      if (selectedParams.co2) {
        charts.push({
          title: "CO₂",
          unit: "ppm",
          data: {
            labels,
            datasets: [{
              label: "CO₂ (ppm)",
              data: data.map((item) => item.co2),
              borderColor: "rgb(249, 115, 22)",
              backgroundColor: "rgba(249, 115, 22, 0.1)",
              tension: 0.4,
            }],
          },
        });
      }

      if (selectedParams.soundLevel) {
        charts.push({
          title: "Sound Level",
          unit: "dB",
          data: {
            labels,
            datasets: [{
              label: "Sound Level (dB)",
              data: data.map((item) => item.soundLevel),
              borderColor: "rgb(168, 85, 247)",
              backgroundColor: "rgba(168, 85, 247, 0.1)",
              tension: 0.4,
            }],
          },
        });
      }

      if (selectedParams.weight) {
        charts.push({
          title: "Weight",
          unit: "kg",
          data: {
            labels,
            datasets: [{
              label: "Weight (kg)",
              data: data.map((item) => item.weight),
              borderColor: "rgb(234, 179, 8)",
              backgroundColor: "rgba(234, 179, 8, 0.1)",
              tension: 0.4,
            }],
          },
        });
      }

      setChartData(charts);

      setError("");
    } catch (err) {
      console.error("Error fetching historical data:", err);
      setError(
        err.response?.data?.error ||
          "Failed to load historical data. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  const getChartOptions = (title, unit) => ({
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false,
      },
      title: {
        display: true,
        text: title,
        font: {
          size: 16,
          weight: "bold",
        },
      },
    },
    scales: {
      x: {
        title: {
          display: true,
          text: "Time",
          font: {
            size: 12,
            weight: "bold",
          },
        },
      },
      y: {
        beginAtZero: false,
        title: {
          display: true,
          text: unit,
          font: {
            size: 12,
            weight: "bold",
          },
        },
      },
    },
  });

  return (
    <div className="bg-content rounded-lg p-6 shadow-lg">
      <h1 className="text-2xl font-bold text-yellow-700 mb-5">Graphics</h1>

      {error && (
        <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
          {error}
        </div>
      )}

      <div className="bg-white rounded-lg shadow border border-yellow-100 p-6 mb-6">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
          {/* Beehive Selection */}
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Select Beehive
            </label>
            <select
              className="w-full border px-3 py-2 rounded"
              value={selectedHive}
              onChange={(e) => setSelectedHive(e.target.value)}
            >
              <option value="">Choose beehive</option>
              {hives.map((hive) => (
                <option key={hive.id} value={hive.id}>
                  {hive.name} ({hive.location})
                </option>
              ))}
            </select>
          </div>

          {/* Time Period Selection */}
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Time Period
            </label>
            <select
              className="w-full border px-3 py-2 rounded"
              value={selectedPeriod}
              onChange={(e) => setSelectedPeriod(e.target.value)}
            >
              <option value="1">1 day</option>
              <option value="3">3 days</option>
              <option value="5">5 days</option>
              <option value="10">10 days</option>
              <option value="30">30 days</option>
              <option value="custom">Custom value</option>
            </select>
            {selectedPeriod === "custom" && (
              <input
                type="number"
                className="w-full border px-3 py-2 rounded mt-2"
                placeholder="Enter number of days"
                value={customDays}
                onChange={(e) => setCustomDays(e.target.value)}
                min="1"
              />
            )}
          </div>

          {/* Add Button */}
          <div className="flex items-end">
            <button
              onClick={handleAdd}
              disabled={loading}
              className="w-full bg-yellow-500 text-white px-4 py-2 rounded hover:bg-yellow-600 disabled:bg-gray-400"
            >
              {loading ? "Loading..." : "Add"}
            </button>
          </div>
        </div>

        {/* Parameter Checkboxes */}
        <div className="border-t pt-4">
          <label className="block text-sm font-semibold text-gray-700 mb-2">
            Select Parameters
          </label>
          <div className="grid grid-cols-2 md:grid-cols-6 gap-3">
            <label className="flex items-center space-x-2 cursor-pointer">
              <input
                type="checkbox"
                checked={selectedParams.temperature}
                onChange={() => handleParamChange("temperature")}
                className="w-4 h-4 text-blue-600"
              />
              <span className="text-sm text-gray-700">Int. Temperature</span>
            </label>
            <label className="flex items-center space-x-2 cursor-pointer">
              <input
                type="checkbox"
                checked={selectedParams.externalTemperature}
                onChange={() => handleParamChange("externalTemperature")}
                className="w-4 h-4 text-cyan-600"
              />
              <span className="text-sm text-gray-700">Ext. Temperature</span>
            </label>
            <label className="flex items-center space-x-2 cursor-pointer">
              <input
                type="checkbox"
                checked={selectedParams.humidity}
                onChange={() => handleParamChange("humidity")}
                className="w-4 h-4 text-green-600"
              />
              <span className="text-sm text-gray-700">Humidity</span>
            </label>
            <label className="flex items-center space-x-2 cursor-pointer">
              <input
                type="checkbox"
                checked={selectedParams.co2}
                onChange={() => handleParamChange("co2")}
                className="w-4 h-4 text-orange-600"
              />
              <span className="text-sm text-gray-700">CO₂</span>
            </label>
            <label className="flex items-center space-x-2 cursor-pointer">
              <input
                type="checkbox"
                checked={selectedParams.soundLevel}
                onChange={() => handleParamChange("soundLevel")}
                className="w-4 h-4 text-purple-600"
              />
              <span className="text-sm text-gray-700">Sound Level</span>
            </label>
            <label className="flex items-center space-x-2 cursor-pointer">
              <input
                type="checkbox"
                checked={selectedParams.weight}
                onChange={() => handleParamChange("weight")}
                className="w-4 h-4 text-yellow-600"
              />
              <span className="text-sm text-gray-700">Weight</span>
            </label>
          </div>
        </div>
      </div>

      {/* Chart Display */}
      {chartData && chartData.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {chartData.map((chart, index) => (
            <div key={index} className="bg-white rounded-lg shadow border border-yellow-100 p-6">
              <div style={{ height: "350px" }}>
                <Line 
                  data={chart.data} 
                  options={getChartOptions(chart.title, chart.unit)} 
                />
              </div>
            </div>
          ))}
        </div>
      ) : chartData && chartData.length === 0 ? (
        <div className="bg-white rounded-lg shadow border border-yellow-100 p-6">
          <div className="text-center py-8">
            <p className="text-gray-600">No parameters selected. Please select at least one parameter to display.</p>
          </div>
        </div>
      ) : null}
    </div>
  );
}
