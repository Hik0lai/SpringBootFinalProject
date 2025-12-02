import React, { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../contexts/AuthContext";

export default function ProfilePage() {
  const { user } = useAuth();
  const [profile, setProfile] = useState(user);
  const [emailNotifications, setEmailNotifications] = useState(false);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");

  useEffect(() => {
    if (!user) return;
    axios.get("http://localhost:8080/api/users/me", {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    }).then((res) => {
      setProfile(res.data);
      setEmailNotifications(res.data.emailNotificationEnabled || false);
    });
  }, [user]);

  const handleEmailNotificationChange = async (e) => {
    const enabled = e.target.checked;
    setEmailNotifications(enabled);
    setSaving(true);
    setMessage("");

    try {
      const token = localStorage.getItem("token");
      const response = await axios.put(
        "http://localhost:8080/api/users/me/email-notifications",
        { enabled },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setProfile(response.data);
      setMessage("Email notification preference saved!");
      setTimeout(() => setMessage(""), 3000);
    } catch (err) {
      console.error("Error updating email notification preference:", err);
      setEmailNotifications(!enabled); // Revert on error
      setMessage("Failed to save preference. Please try again.");
    } finally {
      setSaving(false);
    }
  };

  return !profile ? (
    <div className="bg-content rounded-lg p-6">Loading...</div>
  ) : (
    <div className="max-w-md mx-auto bg-content rounded-lg shadow-xl p-8 border border-yellow-200">
      <h1 className="text-2xl font-bold text-yellow-700 mb-4">Profile</h1>
      <div className="mb-2">
        <span className="font-semibold">Name:</span>{" "}
        {profile.name}
      </div>
      <div className="mb-2">
        <span className="font-semibold">Email:</span>{" "}
        {profile.email}
      </div>
      <div className="mb-4">
        <span className="font-semibold">Role:</span>{" "}
        <span className="uppercase text-xs px-2 py-1 rounded bg-yellow-100 border border-yellow-400">{profile.role}</span>
      </div>
      {user && user.role === "ADMIN" && (
        <div className="mb-4 border-t pt-4">
          <label className="flex items-center space-x-2 cursor-pointer">
            <input
              type="checkbox"
              checked={emailNotifications}
              onChange={handleEmailNotificationChange}
              disabled={saving}
              className="w-4 h-4 text-yellow-600 border-gray-300 rounded focus:ring-yellow-500"
            />
            <span className="font-semibold">Send email notifications</span>
          </label>
          <p className="text-sm text-gray-600 mt-1 ml-6">
            Receive email notifications when alerts are triggered
          </p>
        </div>
      )}
      {message && (
        <div className={`p-3 rounded text-sm ${
          message.includes("Failed") 
            ? "bg-red-100 text-red-700 border border-red-300" 
            : "bg-green-100 text-green-700 border border-green-300"
        }`}>
          {message}
        </div>
      )}
    </div>
  );
}