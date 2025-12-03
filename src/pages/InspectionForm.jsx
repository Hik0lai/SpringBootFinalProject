import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../contexts/AuthContext";

export default function InspectionForm() {
  const { inspectionId } = useParams();
  const { user } = useAuth();
  const [hives, setHives] = useState([]);
  const [form, setForm] = useState({
    hiveId: "",
    inspector: "",
    date: "",
    notes: "",
  });
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const isEdit = !!inspectionId;

  useEffect(() => {
    const token = localStorage.getItem("token");
    
    // Fetch hives
    axios
      .get("http://localhost:8080/api/hives", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setHives(res.data))
      .catch(err => console.error("Error fetching hives:", err));

    if (isEdit) {
      setLoading(true);
      axios
        .get(`http://localhost:8080/api/inspections/${inspectionId}`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((res) => {
          setForm({
            hiveId: res.data.hiveId,
            inspector: res.data.inspector,
            date: res.data.date,
            notes: res.data.notes || "",
          });
        })
        .catch(err => console.error("Error fetching inspection:", err))
        .finally(() => setLoading(false));
    } else {
      // Set inspector to current user's name for new inspections
      if (user) {
        setForm(prev => ({
          ...prev,
          inspector: user.name || user.email,
        }));
      }
    }
  }, [inspectionId, isEdit, user]);

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setLoading(true);
    const url = isEdit
      ? `http://localhost:8080/api/inspections/${inspectionId}`
      : "http://localhost:8080/api/inspections";
    const method = isEdit ? "put" : "post";
    await axios[method](url, form, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    setLoading(false);
    navigate("/inspections");
  }

  return (
    <div className="max-w-lg mx-auto bg-content rounded-lg shadow-xl p-8 mt-5 border border-yellow-200">
      <h1 className="text-xl font-bold mb-6 text-yellow-700">
        {isEdit ? "Edit Inspection" : "Add Inspection"}
      </h1>
      <form onSubmit={handleSubmit} className="space-y-4">
        <select
          name="hiveId"
          className="w-full border px-3 py-2 rounded"
          value={form.hiveId}
          onChange={handleChange}
          required
          disabled={isEdit}
        >
          <option value="">Select Hive</option>
          {hives.map((h) => (
            <option key={h.id} value={h.id}>
              {h.name} ({h.location})
            </option>
          ))}
        </select>
        <input
          className="w-full border px-3 py-2 rounded"
          type="date"
          name="date"
          value={form.date}
          onChange={handleChange}
          required
        />
        <textarea
          className="w-full border px-3 py-2 rounded"
          name="notes"
          placeholder="Notes"
          value={form.notes}
          onChange={handleChange}
          rows={3}
        />
        <div className="flex gap-3">
          <button
            className="flex-1 bg-gray-500 text-white py-2 rounded hover:bg-gray-600"
            type="button"
            onClick={() => navigate("/inspections")}
            disabled={loading}
          >
            Cancel
          </button>
          <button
            className="flex-1 bg-yellow-500 text-white py-2 rounded hover:bg-yellow-600"
            type="submit"
            disabled={loading}
          >
            {loading ? "Saving..." : isEdit ? "Update" : "Save"}
          </button>
        </div>
      </form>
    </div>
  );
}