import { Routes, Route, Navigate } from "react-router-dom";
import ProfesorLayout from "./layout/ProfesorLayout";
import DashboardPage from "./pages/profesor/DashboardPage";
import CursosPage from "./pages/profesor/CursosPage";
import MisionesPage from "./pages/MisionesPage";
import EstudiantesPage from "./pages/profesor/EstudiantesPage";
import LoginProfesor from "./pages/profesor/LoginProfesor";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
  return (
    <Routes>
      {/* Ruta de login */}
      <Route path="/login" element={<LoginProfesor />} />
      
      {/* Layout anidado para el área de profesor - PROTEGIDO */}
      <Route 
        path="/profesor" 
        element={
          <ProtectedRoute>
            <ProfesorLayout />
          </ProtectedRoute>
        }
      >
        <Route path="inicio" element={<DashboardPage />} />
        <Route path="cursos" element={<CursosPage />} />
        <Route path="misiones" element={<MisionesPage />} />
        <Route path="estudiantes" element={<EstudiantesPage />} />
        
        {/* Redirección por defecto */}
        <Route index element={<Navigate to="inicio" replace />} />
      </Route>
      
      {/* Redirección raíz */}
      <Route path="/" element={<Navigate to="/profesor/inicio" replace />} />
      
      {/* Ruta 404 */}
      <Route path="*" element={<Navigate to="/profesor/inicio" replace />} />
    </Routes>
  );
}

export default App;
