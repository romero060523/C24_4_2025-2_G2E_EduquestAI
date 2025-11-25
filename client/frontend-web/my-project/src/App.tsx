import { Routes, Route, Navigate } from "react-router-dom";
import ProfesorLayout from "./layout/ProfesorLayout";
import EstudianteLayout from "./layout/EstudianteLayout";
import DashboardPage from "./pages/profesor/DashboardPage";
import CursosPage from "./pages/profesor/CursosPage";
import MisionesPage from "./pages/MisionesPage";
import EstudiantesPage from "./pages/profesor/EstudiantesPage";
import ReportesPage from "./pages/profesor/ReportesPage";
import RecursosPage from "./pages/profesor/RecursosPage";
import ProgresoEstudiantesPage from "./pages/profesor/ProgresoEstudiantesPage";
import RankingGrupoPage from "./pages/profesor/RankingGrupoPage";
import LoginPage from "./pages/LoginPage";
import DashboardEstudiante from "./pages/estudiante/DashboardEstudiante";
import CursosEstudiante from "./pages/estudiante/CursosEstudiante";
import MisionesEstudiante from "./pages/estudiante/MisionesEstudiante";
import RecompensasEstudiante from "./pages/estudiante/RecompensasEstudiante";
import ProtectedRoute from "./components/ProtectedRoute";
import RankingPage from "./pages/estudiante/RankingPage";
import PerfilGamificado from "./pages/estudiante/PerfilGamificado";
import ChatIAPage from "./pages/estudiante/ChatIAPage";
import AlertasPage from "./pages/profesor/AlertasPage";
import ConfigurarAlertasPage from "./pages/profesor/ConfigurarAlertasPage";
import EvaluacionesPage from "./pages/profesor/EvaluacionesPage";

function App() {
  return (
    <Routes>
      {/* Ruta de login unificada */}
      <Route path="/login" element={<LoginPage />} />

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
        <Route path="progreso" element={<ProgresoEstudiantesPage />} />
        <Route path="alertas" element={<AlertasPage />} />
        <Route path="configurar-alertas" element={<ConfigurarAlertasPage />} />
        <Route path="ranking" element={<RankingGrupoPage />} />
        <Route path="reportes" element={<ReportesPage />} />
        <Route path="recursos" element={<RecursosPage />} />
        <Route path="evaluaciones" element={<EvaluacionesPage />} />

        {/* Redirección por defecto */}
        <Route index element={<Navigate to="inicio" replace />} />
      </Route>

      {/* Layout anidado para el área de estudiante - PROTEGIDO */}
      <Route
        path="/estudiante"
        element={
          <ProtectedRoute>
            <EstudianteLayout />
          </ProtectedRoute>
        }
      >
        <Route path="inicio" element={<DashboardEstudiante />} />
        <Route path="cursos" element={<CursosEstudiante />} />
        <Route path="misiones" element={<MisionesEstudiante />} />
        <Route path="recompensas" element={<RecompensasEstudiante />} />
        <Route path="ranking" element={<RankingPage />} />
        <Route path="chat-ia" element={<ChatIAPage />} />
        <Route path="perfil" element={<PerfilGamificado />} />

        {/* Redirección por defecto */}
        <Route index element={<Navigate to="inicio" replace />} />
      </Route>

      {/* Redirección raíz */}
      <Route path="/" element={<Navigate to="/login" replace />} />

      {/* Ruta 404 */}
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

export default App;
