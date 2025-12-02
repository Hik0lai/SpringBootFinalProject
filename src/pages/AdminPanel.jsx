import React, { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../contexts/AuthContext";

export default function AdminPanel() {
  const { user: currentUser } = useAuth();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = () => {
    setLoading(true);
    axios
      .get("http://localhost:8080/api/users", {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      })
      .then((res) => setUsers(res.data))
      .catch((err) => {
        console.error("Error fetching users:", err);
        setError("Failed to load users. Please try again.");
      })
      .finally(() => setLoading(false));
  };

  const handleRoleChange = async (userId, newRole) => {
    const token = localStorage.getItem("token");
    if (!token) {
      setError("Not authenticated. Please log in again.");
      return;
    }

    // Prevent admin from changing their own role
    if (currentUser && currentUser.id === userId && newRole === "USER") {
      setError("You cannot remove admin role from yourself. Please ask another admin to do it.");
      setTimeout(() => setError(""), 5000);
      return;
    }

    setError("");
    setSuccess("");

    try {
      await axios.put(
        `http://localhost:8080/api/users/${userId}/role`,
        { role: newRole },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      
      // Update the user in the local state
      setUsers(users.map(user => 
        user.id === userId ? { ...user, role: newRole } : user
      ));
      
      setSuccess(`User role updated to ${newRole} successfully!`);
      
      // Clear success message after 3 seconds
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      console.error("Error updating user role:", err);
      const errorMessage = err.response?.data?.error 
        || err.response?.data?.message 
        || "Failed to update user role. Please try again.";
      setError(errorMessage);
      
      // Clear error message after 5 seconds
      setTimeout(() => setError(""), 5000);
    }
  };

  return (
    <div className="bg-content rounded-lg p-6 shadow-lg">
      <h1 className="text-2xl font-bold text-yellow-700 mb-5">Admin Panel - User Management</h1>
      
      {error && (
        <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
          {error}
        </div>
      )}
      
      {success && (
        <div className="mb-4 p-3 bg-green-100 border border-green-400 text-green-700 rounded">
          {success}
        </div>
      )}
      
      {loading ? (
        <div>Loading users...</div>
      ) : (
        <div className="bg-white shadow rounded-lg border border-yellow-100 overflow-x-auto">
          <table className="w-full text-left">
            <thead>
              <tr className="bg-gray-50">
                <th className="p-3">Name</th>
                <th className="p-3">Email</th>
                <th className="p-3">Telephone</th>
                <th className="p-3">Role</th>
                <th className="p-3">Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id} className="border-t hover:bg-yellow-50">
                  <td className="p-3">{u.name}</td>
                  <td className="p-3">{u.email}</td>
                  <td className="p-3">{u.telephone || "-"}</td>
                  <td className="p-3">
                    <span className={`uppercase text-xs px-2 py-1 rounded border ${
                      u.role === "ADMIN" 
                        ? "bg-blue-100 border-blue-400 text-blue-700" 
                        : "bg-yellow-100 border-yellow-400 text-yellow-700"
                    }`}>
                      {u.role}
                    </span>
                  </td>
                  <td className="p-3">
                    <select
                      value={u.role}
                      onChange={(e) => handleRoleChange(u.id, e.target.value)}
                      disabled={currentUser && currentUser.id === u.id && u.role === "ADMIN"}
                      className="px-3 py-1 border rounded bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-yellow-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                      title={currentUser && currentUser.id === u.id && u.role === "ADMIN" ? "You cannot change your own role" : ""}
                    >
                      <option value="USER">USER</option>
                      <option value="ADMIN">ADMIN</option>
                    </select>
                    {currentUser && currentUser.id === u.id && u.role === "ADMIN" && (
                      <span className="ml-2 text-xs text-gray-500">(You)</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          
          {users.length === 0 && !loading && (
            <div className="p-6 text-center text-gray-500">
              No users found.
            </div>
          )}
        </div>
      )}
    </div>
  );
}