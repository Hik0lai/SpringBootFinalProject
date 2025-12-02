import React, { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../contexts/AuthContext";

export default function ProfilePage() {
  const { user } = useAuth();
  const [profile, setProfile] = useState(user);
  const [emailNotifications, setEmailNotifications] = useState(false);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");
  const [showChangePassword, setShowChangePassword] = useState(false);
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [changingPassword, setChangingPassword] = useState(false);

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

  const handleChangePassword = async (e) => {
    e.preventDefault();
    setMessage("");

    // Trim all password fields
    const trimmedCurrentPassword = currentPassword.trim();
    const trimmedNewPassword = newPassword.trim();
    const trimmedConfirmPassword = confirmPassword.trim();

    // Validate current password is provided
    if (!trimmedCurrentPassword) {
      setMessage("Current password is required.");
      return;
    }

    // Validate new password is provided
    if (!trimmedNewPassword) {
      setMessage("New password is required.");
      return;
    }

    // Validate passwords match
    if (trimmedNewPassword !== trimmedConfirmPassword) {
      setMessage("New passwords do not match. Please try again.");
      return;
    }

    // Validate password length
    if (trimmedNewPassword.length < 6) {
      setMessage("New password must be at least 6 characters long.");
      return;
    }

    setChangingPassword(true);

    try {
      const token = localStorage.getItem("token");
      await axios.put(
        "http://localhost:8080/api/users/me/password",
        {
          currentPassword: trimmedCurrentPassword,
          newPassword: trimmedNewPassword,
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      setMessage("Password changed successfully!");
      setCurrentPassword("");
      setNewPassword("");
      setConfirmPassword("");
      setShowChangePassword(false);
      setTimeout(() => setMessage(""), 3000);
    } catch (err) {
      console.error("Error changing password:", err);
      console.error("Error response:", err.response);
      console.error("Error response data:", err.response?.data);
      
      let errorMessage = "Failed to change password. Please check your current password and try again.";
      
      if (err.response?.data) {
        const data = err.response.data;
        // Try multiple possible error fields
        errorMessage = data.error || 
                      data.message || 
                      (typeof data === 'string' ? data : errorMessage);
      } else if (err.message) {
        errorMessage = err.message;
      }
      
      setMessage(errorMessage);
    } finally {
      setChangingPassword(false);
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
      <div className="mb-4 border-t pt-4">
        <button
          onClick={() => setShowChangePassword(!showChangePassword)}
          className="text-blue-600 hover:text-blue-800 hover:underline text-sm font-medium"
        >
          {showChangePassword ? "Cancel" : "Change Password"}
        </button>
        {showChangePassword && (
          <form onSubmit={handleChangePassword} className="mt-4 space-y-3">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Current Password
              </label>
              <input
                type="password"
                value={currentPassword}
                onChange={(e) => setCurrentPassword(e.target.value)}
                className="w-full border px-3 py-2 rounded focus:outline-none focus:ring-2 focus:ring-yellow-500"
                required
                disabled={changingPassword}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                New Password
              </label>
              <input
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                className="w-full border px-3 py-2 rounded focus:outline-none focus:ring-2 focus:ring-yellow-500"
                required
                minLength={6}
                disabled={changingPassword}
              />
              <p className="text-xs text-gray-500 mt-1">
                Password must be at least 6 characters long
              </p>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Confirm New Password
              </label>
              <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className="w-full border px-3 py-2 rounded focus:outline-none focus:ring-2 focus:ring-yellow-500"
                required
                disabled={changingPassword}
              />
            </div>
            <button
              type="submit"
              disabled={changingPassword}
              className="w-full bg-yellow-500 text-white py-2 rounded hover:bg-yellow-600 transition disabled:bg-gray-400 disabled:cursor-not-allowed"
            >
              {changingPassword ? "Changing Password..." : "Change Password"}
            </button>
          </form>
        )}
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