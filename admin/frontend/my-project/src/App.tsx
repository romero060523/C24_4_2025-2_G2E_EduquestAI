import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { useEffect } from "react";
import AdminLayout from "./layout/AdminLayout";
import Dashboard from "./pages/Dashboard";
import Users from "./pages/Users";
import Cursos from "./pages/Cursos";
import ReglasGamificacion from "./pages/ReglasGamificacion";
import Reportes from "./pages/Reportes";
import LoginPage from "./pages/Login";
import ProtectedRoute from "./components/ProtectedRoute";

export default function App() {
  // Limpiar token en cada refresh/carga de la aplicación
  useEffect(() => {
    localStorage.removeItem("access");
  }, []);

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/admin/login" />} />
        <Route path="/admin/login" element={<LoginPage />} />
        <Route
          path="/admin"
          element={
            <ProtectedRoute>
              <AdminLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<Navigate to="/admin/dashboard" />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="users" element={<Users />} />
          <Route path="cursos" element={<Cursos />} />
          <Route path="reglas-gamificacion" element={<ReglasGamificacion />} />
          <Route path="reportes" element={<Reportes />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
