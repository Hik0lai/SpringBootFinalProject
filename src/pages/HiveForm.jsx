import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";

export default function HiveForm() {
  const { hiveId } = useParams();
  const navigate = useNavigate();
  const isEdit = !!hiveId;
  const [form, setForm] = useState({ name: "", location: "", birthMonth: "", birthYear: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (isEdit) {
      setLoading(true);
      axios.get(`http://localhost:8080/api/hives/${hiveId}`, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      })
      .then(res => {
        const data = res.data;
        if (data.birthDate) {
          const [year, month] = data.birthDate.split('-');
          setForm({ 
            name: data.name || "", 
            location: data.location || "", 
            birthMonth: month || "", 
            birthYear: year || "" 
          });
        } else {
          setForm({ 
            name: data.name || "", 
            location: data.location || "", 
            birthMonth: "", 
            birthYear: "" 
          });
        }
      })
      .catch(err => {
        setError(err.response?.data?.message || "Failed to load hive");
      })
      .finally(() => setLoading(false));
    }
  }, [hiveId, isEdit]);

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError(""); // Clear error when user types
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setLoading(true);
    setError("");
    
    const token = localStorage.getItem('token');
    if (!token) {
      setError("You must be logged in to create a hive. Please log in again.");
      setLoading(false);
      return;
    }
    
    const url = isEdit
      ? `http://localhost:8080/api/hives/${hiveId}`
      : `http://localhost:8080/api/hives`;
    const method = isEdit ? "put" : "post";
    
    // Combine month and year into birthDate format (YYYY-MM)
    const submitData = {
      name: form.name,
      location: form.location,
      birthDate: form.birthMonth && form.birthYear ? `${form.birthYear}-${form.birthMonth}` : null
    };
    
    try {
      await axios[method](url, submitData, {
        headers: { Authorization: `Bearer ${token}` },
        timeout: 10000 // 10 second timeout
      });
      navigate("/");
    } catch (err) {
      let errorMessage = "Failed to save hive. Please try again.";
      
      if (err.response?.data) {
        const data = err.response.data;
        if (data.message) {
          errorMessage = data.message;
        } else if (data.error) {
          errorMessage = data.error;
        } else if (data.errors) {
          // Handle validation errors
          if (typeof data.errors === 'object') {
            const errorMessages = Object.values(data.errors).join(", ");
            errorMessage = errorMessages || errorMessage;
          } else if (Array.isArray(data.errors)) {
            errorMessage = data.errors.map(e => e.defaultMessage || e.message || e).join(", ");
          }
        }
      } else if (err.message) {
        errorMessage = err.message;
      }
      
      setError(errorMessage);
      setLoading(false);
    }
  }

  return (
    <div className="max-w-lg mx-auto mt-10 bg-content p-8 rounded-lg shadow-xl border border-yellow-200">
      <h1 className="text-xl font-bold mb-6 text-yellow-700">
        {isEdit ? "Edit Hive" : "Add Hive"}
      </h1>
      {error && (
        <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
          {error}
        </div>
      )}
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
        <div className="flex gap-3">
          <select
            className="flex-1 border px-3 py-2 rounded"
            name="birthMonth"
            value={form.birthMonth}
            onChange={handleChange}
          >
            <option value="">Select Month</option>
            <option value="01">January</option>
            <option value="02">February</option>
            <option value="03">March</option>
            <option value="04">April</option>
            <option value="05">May</option>
            <option value="06">June</option>
            <option value="07">July</option>
            <option value="08">August</option>
            <option value="09">September</option>
            <option value="10">October</option>
            <option value="11">November</option>
            <option value="12">December</option>
          </select>
          <input
            className="flex-1 border px-3 py-2 rounded"
            type="number"
            placeholder="Year (e.g., 2024)"
            name="birthYear"
            min="1900"
            max="2100"
            value={form.birthYear}
            onChange={handleChange}
          />
        </div>
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