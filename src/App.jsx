import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import HiveDetails from './pages/HiveDetails';
import HiveForm from './pages/HiveForm';
import InspectionList from './pages/InspectionList';
import InspectionForm from './pages/InspectionForm';
import AlertsPage from './pages/AlertsPage';
import AlertForm from './pages/AlertForm';
import Graphics from './pages/Graphics';
import Weather from './pages/Weather';
import ProfilePage from './pages/ProfilePage';
import Settings from './pages/Settings';
import AdminPanel from './pages/AdminPanel';
import Navbar from './components/Navbar';

function PrivateRoute({ children, adminOnly }) {
  const { user, loading } = useAuth();
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="text-xl text-yellow-700">Loading...</div>
        </div>
      </div>
    );
  }
  if (!user) return <Navigate to="/login" />;
  if (adminOnly && user.role !== 'ADMIN') return <Navigate to="/" />;
  return children;
}

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen" style={{ background: 'transparent' }}>
          <Navbar />
          <div className="container mx-auto p-4">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route
                path="/"
                element={
                  <PrivateRoute>
                    <Dashboard />
                  </PrivateRoute>
                }
              />
              <Route
                path="/hives/:hiveId"
                element={
                  <PrivateRoute>
                    <HiveDetails />
                  </PrivateRoute>
                }
              />
              <Route
                path="/add-hive"
                element={
                  <PrivateRoute adminOnly>
                    <HiveForm />
                  </PrivateRoute>
                }
              />
              <Route
                path="/edit-hive/:hiveId"
                element={
                  <PrivateRoute adminOnly>
                    <HiveForm />
                  </PrivateRoute>
                }
              />
              <Route
                path="/graphics"
                element={
                  <PrivateRoute adminOnly>
                    <Graphics />
                  </PrivateRoute>
                }
              />
              <Route
                path="/weather"
                element={
                  <PrivateRoute>
                    <Weather />
                  </PrivateRoute>
                }
              />
              <Route
                path="/inspections"
                element={
                  <PrivateRoute adminOnly>
                    <InspectionList />
                  </PrivateRoute>
                }
              />
              <Route
                path="/add-inspection"
                element={
                  <PrivateRoute adminOnly>
                    <InspectionForm />
                  </PrivateRoute>
                }
              />
              <Route
                path="/edit-inspection/:inspectionId"
                element={
                  <PrivateRoute adminOnly>
                    <InspectionForm />
                  </PrivateRoute>
                }
              />
              <Route
                path="/alerts"
                element={
                  <PrivateRoute adminOnly>
                    <AlertsPage />
                  </PrivateRoute>
                }
              />
              <Route
                path="/add-alert"
                element={
                  <PrivateRoute adminOnly>
                    <AlertForm />
                  </PrivateRoute>
                }
              />
              <Route
                path="/edit-alert/:alertId"
                element={
                  <PrivateRoute adminOnly>
                    <AlertForm />
                  </PrivateRoute>
                }
              />
              <Route
                path="/profile"
                element={
                  <PrivateRoute>
                    <ProfilePage />
                  </PrivateRoute>
                }
              />
              <Route
                path="/settings"
                element={
                  <PrivateRoute adminOnly>
                    <Settings />
                  </PrivateRoute>
                }
              />
              <Route
                path="/admin"
                element={
                  <PrivateRoute adminOnly>
                    <AdminPanel />
                  </PrivateRoute>
                }
              />
            </Routes>
          </div>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;