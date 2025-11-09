import React, { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../contexts/AuthContext";

export default function ProfilePage() {
  const { user } = useAuth();
  const [profile, setProfile] = useState(user);

  useEffect(() => {
    if (!user) return;
    axios.get("http://localhost:8080/api/users/me", {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    }).then((res) => setProfile(res.data));
  }, [user]);

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
      <div>
        <span className="font-semibold">Role:</span>{" "}
        <span className="uppercase text-xs px-2 py-1 rounded bg-yellow-100 border border-yellow-400">{profile.role}</span>
      </div>
    </div>
  );
}