import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";

export default function HiveDetails() {
  const { hiveId } = useParams();
  const [hive, setHive] = useState(null);
  const [sensors, setSensors] = useState([]);
  const [readings, setReadings] = useState([]);
  const [realtimeData, setRealtimeData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");
    
    // Fetch hive details
    axios.get(`http://localhost:8080/api/hives/${hiveId}`, {
      headers: { Authorization: `Bearer ${token}` }
    }).then(res => {
      setHive(res.data);
      setSensors(res.data.sensors || []); // Ensure sensors is always an array
      setLoading(false);
    }).catch(err => {
      console.error("Error fetching hive:", err);
      setLoading(false);
    });
    
    // Fetch last readings
    axios.get(`http://localhost:8080/api/sensors/last-readings?hiveId=${hiveId}`, {
      headers: { Authorization: `Bearer ${token}` }
    }).then(res => setReadings(res.data || [])).catch(err => {
      console.error("Error fetching readings:", err);
      setReadings([]);
    });
    
    // Fetch realtime sensor data for this hive
    axios.get(`http://localhost:8080/api/sensors/realtime-data/hive/${hiveId}`, {
      headers: { Authorization: `Bearer ${token}` }
    }).then(res => {
      setRealtimeData(res.data);
    }).catch(err => {
      console.error("Error fetching realtime data:", err);
      setRealtimeData(null);
    });
  }, [hiveId]);

  return loading || !hive ? (
    <div className="bg-content rounded-lg p-6">Loading...</div>
  ) : (
    <div className="bg-content rounded-lg p-6 shadow-lg">
      <div className="mb-4">
        <h1 className="text-2xl font-bold text-yellow-700">{hive.name}</h1>
      </div>
      <div className="mb-4 text-gray-700">{hive.location}</div>
      <div className="mb-8">
        <h2 className="font-semibold mb-4">Current Sensor Data</h2>
        {realtimeData ? (
          <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
            <div className="rounded-lg shadow px-4 py-3 bg-white border border-yellow-100">
              <div className="text-xs text-gray-500 mb-1">Temperature</div>
              <div className="text-xl font-bold text-yellow-700">{realtimeData.temperature?.toFixed(1) || 'N/A'}°C</div>
            </div>
            <div className="rounded-lg shadow px-4 py-3 bg-white border border-yellow-100">
              <div className="text-xs text-gray-500 mb-1">External Temperature</div>
              <div className="text-xl font-bold text-yellow-700">{realtimeData.externalTemperature?.toFixed(1) || 'N/A'}°C</div>
            </div>
            <div className="rounded-lg shadow px-4 py-3 bg-white border border-yellow-100">
              <div className="text-xs text-gray-500 mb-1">Humidity</div>
              <div className="text-xl font-bold text-yellow-700">{realtimeData.humidity?.toFixed(1) || 'N/A'}%</div>
            </div>
            <div className="rounded-lg shadow px-4 py-3 bg-white border border-yellow-100">
              <div className="text-xs text-gray-500 mb-1">CO₂</div>
              <div className="text-xl font-bold text-yellow-700">{realtimeData.co2?.toFixed(1) || 'N/A'} ppm</div>
            </div>
            <div className="rounded-lg shadow px-4 py-3 bg-white border border-yellow-100">
              <div className="text-xs text-gray-500 mb-1">Sound Level</div>
              <div className="text-xl font-bold text-yellow-700">{realtimeData.soundLevel?.toFixed(1) || 'N/A'} dB</div>
            </div>
            <div className="rounded-lg shadow px-4 py-3 bg-white border border-yellow-100">
              <div className="text-xs text-gray-500 mb-1">Weight</div>
              <div className="text-xl font-bold text-yellow-700">{realtimeData.weight?.toFixed(2) || 'N/A'} kg</div>
            </div>
          </div>
        ) : (
          <div className="text-gray-500">Loading sensor data...</div>
        )}
      </div>
      {readings && readings.length > 0 && (
        <div className="mb-8">
          <h2 className="font-semibold mb-4">Latest Readings</h2>
          <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
            {readings.map(r => (
              <div key={r.type} className="rounded-lg shadow px-4 py-3 bg-white border border-yellow-100">
                <div className="text-xs text-gray-500 mb-1">{r.type}</div>
                <div className="text-xl font-bold text-yellow-700">{r.value} {r.unit}</div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}