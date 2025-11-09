import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";

export default function HiveForm() {
  const { hiveId } = useParams();
  const navigate = useNavigate();
  const isEdit = !!hiveId;
  const [form, setForm] = useState({ name: "", location: "", queen: "" });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isEdit) {
      setLoading(true);
      axios.get(`http://localhost:8080/api/hives/${hiveId}`, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      }).then(res => setForm(res.data)).finally(() => setLoading(false));
    }
  }, [hiveId, isEdit]);

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setLoading(true);
    const url = isEdit
      ? `http://localhost:8080/api/hives/${hiveId}`
      : `http://localhost:8080/api/hives`;
    const method = isEdit ? "put" : "post";
    await axios[method](url, form, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    });
    setLoading(false);
    navigate("/");
  }

  return (
    <div className="max-w-lg mx-auto mt-10 bg-content p-8 rounded-lg shadow-xl border border-yellow-200">
      <h1 className="text-xl font-bold mb-6 text-yellow-700">
        {isEdit ? "Edit Hive" : "Add Hive"}
      </h1>
      <form onSubmit={handleSubmit} className="space-y-5">
        <input
          className="w-full border px-3 py-2 rounded"
          placeholder="Hive Name"
          name="name"
          value={form.name}
          onChange={handleChange}
          required />
        <input
          className="w-full border px-3 py-2 rounded"
          placeholder="Location"
          name="location"
          value={form.location}
          onChange={handleChange}
          required />
        <input
          className="w-full border px-3 py-2 rounded"
          placeholder="Queen Name"
          name="queen"
          value={form.queen || ""}
          onChange={handleChange}
        />
        <button
          className="w-full bg-yellow-500 text-white py-2 rounded hover:bg-yellow-600"
          type="submit"
          disabled={loading}>
          {loading ? "Saving..." : (isEdit ? "Update Hive" : "Create Hive")}
        </button>
      </form>
    </div>
  );
}