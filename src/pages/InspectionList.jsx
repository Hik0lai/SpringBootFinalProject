import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link } from "react-router-dom";

export default function InspectionList() {
  const [inspections, setInspections] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchInspections = () => {
    setLoading(true);
    axios.get("http://localhost:8080/api/inspections", {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    }).then(res => setInspections(res.data)).finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchInspections();
  }, []);

  const handleDelete = async (inspectionId, hiveName, date) => {
    const confirmed = window.confirm(
      `Are you sure you want to delete the inspection for "${hiveName}" on ${date}?\n\nThis action cannot be undone.`
    );

    if (!confirmed) {
      return;
    }

    const token = localStorage.getItem("token");
    if (!token) return;

    try {
      await axios.delete(`http://localhost:8080/api/inspections/${inspectionId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchInspections(); // Refresh list after deletion
    } catch (err) {
      console.error("Error deleting inspection:", err);
      alert("Failed to delete inspection. Please try again.");
    }
  };

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
                    <div className="flex gap-2">
                      <Link
                        to={`/edit-inspection/${insp.id}`}
                        className="text-blue-600 hover:text-blue-800 hover:underline text-sm"
                      >
                        Edit
                      </Link>
                      <button
                        onClick={() => handleDelete(insp.id, insp.hiveName, insp.date)}
                        className="text-red-600 hover:text-red-800 hover:underline text-sm"
                      >
                        Delete
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