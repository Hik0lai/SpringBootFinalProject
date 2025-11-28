import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";

export default function AlertForm() {
  const { alertId } = useParams();
  const navigate = useNavigate();
  const isEdit = !!alertId;
  const [hives, setHives] = useState([]);
  const [form, setForm] = useState({
    name: "",
    hiveId: "",
    triggers: [{ parameter: "", operator: ">", value: "" }]
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

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
        .get(`http://localhost:8080/api/alerts/${alertId}`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((res) => {
          const alert = res.data;
          let triggers = [];
          if (alert.triggerConditions) {
            try {
              triggers = JSON.parse(alert.triggerConditions);
            } catch (e) {
              triggers = [{ parameter: "", operator: ">", value: "" }];
            }
          }
          if (triggers.length === 0) {
            triggers = [{ parameter: "", operator: ">", value: "" }];
          }
          setForm({
            name: alert.name || "",
            hiveId: alert.hiveId || "",
            triggers: triggers
          });
        })
        .catch(err => {
          setError(err.response?.data?.message || "Failed to load alert");
        })
        .finally(() => setLoading(false));
    }
  }, [alertId, isEdit]);

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError("");
  }

  function handleTriggerChange(index, field, value) {
    const newTriggers = [...form.triggers];
    newTriggers[index] = { ...newTriggers[index], [field]: value };
    setForm({ ...form, triggers: newTriggers });
  }

  function addTrigger() {
    if (form.triggers.length < 4) {
      setForm({
        ...form,
        triggers: [...form.triggers, { parameter: "", operator: ">", value: "" }]
      });
    }
  }

  function removeTrigger(index) {
    if (form.triggers.length > 1) {
      const newTriggers = form.triggers.filter((_, i) => i !== index);
      setForm({ ...form, triggers: newTriggers });
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setLoading(true);
    setError("");

    const token = localStorage.getItem("token");
    if (!token) {
      setError("You must be logged in. Please log in again.");
      setLoading(false);
      return;
    }

    // Validate triggers
    const validTriggers = form.triggers.filter(t => t.parameter && t.value);
    if (validTriggers.length === 0) {
      setError("Please add at least one trigger condition.");
      setLoading(false);
      return;
    }

    const url = isEdit
      ? `http://localhost:8080/api/alerts/${alertId}`
      : "http://localhost:8080/api/alerts";
    const method = isEdit ? "put" : "post";

    const submitData = {
      name: form.name,
      hiveId: form.hiveId,
      triggerConditions: JSON.stringify(validTriggers)
    };

    try {
      await axios[method](url, submitData, {
        headers: { Authorization: `Bearer ${token}` },
        timeout: 10000
      });
      navigate("/alerts");
    } catch (err) {
      let errorMessage = "Failed to save alert. Please try again.";
      
      if (err.response?.data) {
        const data = err.response.data;
        if (data.message) {
          errorMessage = data.message;
        } else if (data.error) {
          errorMessage = data.error;
        } else if (data.errors) {
          if (typeof data.errors === 'object') {
            const errorMessages = Object.values(data.errors).join(", ");
            errorMessage = errorMessages || errorMessage;
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
        {isEdit ? "Edit Alert" : "Add Alert"}
      </h1>
      {error && (
        <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
          {error}
        </div>
      )}
      <form onSubmit={handleSubmit} className="space-y-5">
        <input
          className="w-full border px-3 py-2 rounded"
          placeholder="Alert Name"
          name="name"
          value={form.name}
          onChange={handleChange}
          required
        />
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

        <div className="space-y-3">
          <label className="block font-semibold text-yellow-700">Trigger Conditions</label>
          {form.triggers.map((trigger, index) => (
            <div key={index} className="border p-3 rounded bg-white space-y-2">
              <div className="flex items-center justify-between mb-2">
                <span className="text-sm font-semibold text-gray-700">Condition {index + 1}</span>
                {form.triggers.length > 1 && (
                  <button
                    type="button"
                    onClick={() => removeTrigger(index)}
                    className="text-red-500 hover:text-red-700 text-sm"
                  >
                    Remove
                  </button>
                )}
              </div>
              <div className="grid grid-cols-3 gap-2">
                <select
                  className="border px-2 py-2 rounded text-sm"
                  value={trigger.parameter}
                  onChange={(e) => handleTriggerChange(index, "parameter", e.target.value)}
                  required
                >
                  <option value="">Parameter</option>
                  <option value="temperature">Temperature</option>
                  <option value="humidity">Humidity</option>
                  <option value="co2">CO₂</option>
                  <option value="sound">Sound Level</option>
                  <option value="weight">Weight</option>
                </select>
                <select
                  className="border px-2 py-2 rounded text-sm"
                  value={trigger.operator}
                  onChange={(e) => handleTriggerChange(index, "operator", e.target.value)}
                  required
                >
                  <option value=">">&gt;</option>
                  <option value=">=">&gt;=</option>
                  <option value="<">&lt;</option>
                  <option value="<=">&lt;=</option>
                </select>
                <input
                  type="number"
                  step="0.1"
                  className="border px-2 py-2 rounded text-sm"
                  placeholder="Value"
                  value={trigger.value}
                  onChange={(e) => handleTriggerChange(index, "value", e.target.value)}
                  required
                />
              </div>
            </div>
          ))}
          {form.triggers.length < 4 && (
            <button
              type="button"
              onClick={addTrigger}
              className="text-blue-600 hover:text-blue-800 text-sm font-semibold"
            >
              + Add Another Condition
            </button>
          )}
        </div>

        <button
          className="w-full bg-yellow-500 text-white py-2 rounded hover:bg-yellow-600"
          type="submit"
          disabled={loading}
        >
          {loading ? "Saving..." : (isEdit ? "Update Alert" : "Add Alert")}
        </button>
      </form>
    </div>
  );
}

