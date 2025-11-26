import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";

export default function HiveDetails() {
  const { hiveId } = useParams();
  const [hive, setHive] = useState(null);
  const [sensors, setSensors] = useState([]);
  const [readings, setReadings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");
    axios.get(`http://localhost:8080/api/hives/${hiveId}`, {
      headers: { Authorization: `Bearer ${token}` }
    }).then(res => {
      setHive(res.data);
      setSensors(res.data.sensors);
      setLoading(false);
    });
    axios.get(`http://localhost:8080/api/sensors/last-readings?hiveId=${hiveId}`, {
      headers: { Authorization: `Bearer ${token}` }
    }).then(res => setReadings(res.data));
  }, [hiveId]);

  return loading || !hive ? (
    <div className="bg-content rounded-lg p-6">Loading...</div>
  ) : (
    <div className="bg-content rounded-lg p-6 shadow-lg">
      <div className="flex items-center mb-4">
        <h1 className="text-2xl font-bold text-yellow-700 flex-1">{hive.name}</h1>
        <Link to={`/edit-hive/${hive.id}`} className="mr-2 text-blue-600 hover:underline">Edit Hive</Link>
      </div>
      <div className="mb-4 text-gray-700">{hive.location}</div>
      <div className="mb-8">
        <h2 className="font-semibold mb-2">Sensors</h2>
        {sensors.length === 0 
          ? <div className="text-gray-500">No sensors assigned.</div>
          : (
            <div>
              {sensors.map(sensor => (
                <div key={sensor.id} className="text-sm mb-1">
                  <span className="text-gray-700 font-mono">{sensor.type}</span>
                  <span className="ml-3 text-gray-400">ID: {sensor.id}</span>
                </div>
              ))}
            </div>
          )
        }
      </div>
      <div>
        <h2 className="font-semibold mb-1">Latest Readings</h2>
        <div className="grid grid-cols-2 max-w-xs gap-4 mb-7 mt-3">
          {readings.map(r => (
            <div key={r.type} className="rounded-lg shadow px-4 py-3 bg-white border border-yellow-100">
              <div className="text-lg font-bold text-yellow-700">{r.value} {r.unit}</div>
              <div className="text-xs text-gray-600">{r.type}</div>
            </div>
          ))}
        </div>
      </div>
      <div className="mt-7">
        <h2 className="font-semibold mb-2">Charts</h2>
        {/* Example static chart placeholder. Replace with real chart component */}
        <div className="w-full max-w-xl bg-white p-6 rounded-lg border border-yellow-100 shadow">
          <div className="h-40 flex items-center justify-center text-gray-300">[Temp / Humidity chart]</div>
        </div>
      </div>
    </div>
  );
}