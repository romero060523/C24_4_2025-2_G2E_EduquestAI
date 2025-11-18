import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiService } from "../../services/api";
import type { AlertaRendimiento } from "../../types";

interface Props {
  cursoId: string;
}

const AlertasWidget: React.FC<Props> = ({ cursoId }) => {
  const [alertas, setAlertas] = useState<AlertaRendimiento[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    if (cursoId) {
      cargarAlertas();
    }
  }, [cursoId]);

  const cargarAlertas = async () => {
    setLoading(true);
    try {
      const data = await apiService.obtenerAlertasCurso(cursoId);
      setAlertas(data.slice(0, 5)); // Solo las primeras 5
    } catch (error) {
      console.error("Error cargando alertas:", error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-lg font-semibold mb-4">Alertas Recientes</h3>
        <div className="text-center py-4">Cargando...</div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow p-6">
      <div className="flex justify-between items-center mb-4">
        <h3 className="text-lg font-semibold">Alertas Recientes</h3>
        {alertas.length > 0 && (
          <span className="bg-red-100 text-red-700 px-3 py-1 rounded-full text-sm font-medium">
            {alertas.length}
          </span>
        )}
      </div>

      {alertas.length === 0 ? (
        <div className="text-center py-8 text-gray-500">
          <span className="text-4xl block mb-2">✅</span>
          <p>No hay alertas activas</p>
        </div>
      ) : (
        <>
          <div className="space-y-3">
            {alertas.map((alerta) => (
              <div
                key={alerta.id}
                className="border border-gray-200 rounded-lg p-3 hover:bg-gray-50 cursor-pointer"
                onClick={() => navigate("/profesor/alertas")}
              >
                <div className="flex items-start gap-2">
                  <span className="text-xl">
                    {alerta.tipo === "INACTIVIDAD" && "😴"}
                    {alerta.tipo === "BAJO_RENDIMIENTO" && "📉"}
                    {alerta.tipo === "MISIONES_PENDIENTES" && "📝"}
                    {alerta.tipo === "DEBAJO_PROMEDIO" && "📊"}
                  </span>
                  <div className="flex-1">
                    <p className="font-medium text-sm">
                      {alerta.estudianteNombre}
                    </p>
                    <p className="text-xs text-gray-600">
                      {alerta.descripcion}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>

          <button
            onClick={() => navigate("/profesor/alertas")}
            className="mt-4 w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-lg font-medium"
          >
            Ver todas las alertas
          </button>
        </>
      )}
    </div>
  );
};

export default AlertasWidget;
