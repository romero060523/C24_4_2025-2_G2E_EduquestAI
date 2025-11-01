import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

interface ProtectedRouteProps {
  children: React.ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
  const { usuario, isAuthenticated, isLoading } = useAuth();
  const location = useLocation();

  // Mostrar un loader mientras verifica la autenticación
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Verificando autenticación...</p>
        </div>
      </div>
    );
  }

  // Si no está autenticado, redirigir al login
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Validar que el rol coincide con la ruta
  const currentPath = location.pathname;
  const isProfesorRoute = currentPath.startsWith("/profesor");
  const isEstudianteRoute = currentPath.startsWith("/estudiante");

  // Si es ruta de profesor pero el usuario es estudiante, redirigir
  if (isProfesorRoute && usuario?.rol === "estudiante") {
    return <Navigate to="/estudiante/inicio" replace />;
  }

  // Si es ruta de estudiante pero el usuario es profesor, redirigir
  if (isEstudianteRoute && usuario?.rol === "profesor") {
    return <Navigate to="/profesor/inicio" replace />;
  }

  // Si está autenticado y el rol coincide, mostrar el contenido protegido
  return <>{children}</>;
};

export default ProtectedRoute;
