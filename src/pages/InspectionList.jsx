import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link } from "react-router-dom";

export default function InspectionList() {
  const [inspections, setInspections] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axios.get("http://localhost:8080/api/inspections", {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    }).then(res => setInspections(res.data)).finally(() => setLoading(false));
  }, []);

  return (
    <div className="bg-content rounded-lg p-6 shadow-lg">
      <div className="flex items-center justify-between mb-5">
        <h1 className="text-2xl font-bold text-yellow-700">Hive Inspections</h1>
        <Link to="/add-inspection" className="bg-yellow-500 px-3 py-1 rounded text-white hover:bg-yellow-600">+ New</Link>
      </div>
      {loading ? (
        <div>Loading...</div>
      ) : inspections.length === 0 ? (
        <div>No inspections recorded yet.</div>
      ) : (
        <div className="bg-white rounded-lg shadow border border-yellow-100">
          <table className="w-full text-left">
            <thead>
              <tr className="text-yellow-700">
                <th className="p-2">Hive</th>
                <th className="p-2">Inspector</th>
                <th className="p-2">Date</th>
                <th className="p-2">Notes</th>
                <th className="p-2"></th>
              </tr>
            </thead>
            <tbody>
              {inspections.map((insp) => (
                <tr key={insp.id} className="border-t">
                  <td className="p-2">{insp.hiveName}</td>
                  <td className="p-2">{insp.inspector || "-"}</td>
                  <td className="p-2">{insp.date}</td>
                  <td className="p-2 max-w-xs truncate">{insp.notes}</td>
                  <td className="p-2">
                    <Link
                      to={`/edit-inspection/${insp.id}`}
                      className="text-blue-600 hover:underline"
                    >
                      Edit
                    </Link>
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