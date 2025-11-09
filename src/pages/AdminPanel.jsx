import React, { useEffect, useState } from "react";
import axios from "axios";

export default function AdminPanel() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/users", {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      })
      .then((res) => setUsers(res.data))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="bg-content rounded-lg p-6 shadow-lg">
      <h1 className="text-2xl font-bold text-yellow-700 mb-5">Admin Panel</h1>
      {loading ? (
        <div>Loading...</div>
      ) : (
        <div className="bg-white shadow rounded-lg border border-yellow-100">
          <table className="w-full text-left">
            <thead>
              <tr className="bg-gray-50">
                <th className="p-2">Name</th>
                <th className="p-2">Email</th>
                <th className="p-2">Role</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id} className="border-t hover:bg-yellow-50">
                  <td className="p-2">{u.name}</td>
                  <td className="p-2">{u.email}</td>
                  <td className="p-2">
                    <span className="uppercase text-xs px-2 py-1 rounded bg-yellow-100 border border-yellow-400">
                      {u.role}
                    </span>
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