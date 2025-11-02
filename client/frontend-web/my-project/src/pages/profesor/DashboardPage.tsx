// src/pages/profesor/DashboardPage.tsx
import { useAuth } from "../../hooks/useAuth";

const DashboardPage = () => {
  const { usuario } = useAuth();

  return (
    <div>
      <h1 className="text-2xl font-bold mb-4">Panel de Control</h1>
      <p className="text-gray-700 mb-2">
        Bienvenido, {usuario?.nombre || "Profesor"}
      </p>
      <div className="bg-white rounded-lg p-6 mt-4">
        {/* Aquí irá el dashboard gráfico más adelante */}
        <div className="text-gray-500">
          Aquí irá el contenido dinámico del dashboard.
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
