import { useState, useEffect } from "react";
import { AlertTriangle, CheckCircle, Clock, Archive } from "lucide-react";
import { apiService } from "../../services/api";
import type { AlertaTempranaResponse, EstadoAlerta } from "../../types";

const AlertasEstudiantePage = () => {
  const [alertas, setAlertas] = useState<AlertaTempranaResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [filter, setFilter] = useState<EstadoAlerta | "TODAS">("TODAS");

  useEffect(() => {
    loadAlertas();
  }, []);

  const loadAlertas = async () => {
    setLoading(true);
    try {
      const estudianteId = localStorage.getItem("estudianteId") || localStorage.getItem("userId") || "";
      const alertasData = await apiService.obtenerAlertasPorEstudiante(estudianteId);
      setAlertas(alertasData);
    } catch (error) {
      console.error("Error cargando alertas:", error);
    } finally {
      setLoading(false);
    }
  };

  const getEstadoColor = (estado: EstadoAlerta) => {
    switch (estado) {
      case "ACTIVA": return "bg-red-100 text-red-800 border-red-300";
      case "EN_SEGUIMIENTO": return "bg-yellow-100 text-yellow-800 border-yellow-300";
      case "RESUELTA": return "bg-green-100 text-green-800 border-green-300";
      case "ARCHIVADA": return "bg-gray-100 text-gray-800 border-gray-300";
      default: return "bg-gray-100 text-gray-800 border-gray-300";
    }
  };

  const getEstadoIcon = (estado: EstadoAlerta) => {
    switch (estado) {
      case "ACTIVA": return <AlertTriangle className="w-5 h-5" />;
      case "EN_SEGUIMIENTO": return <Clock className="w-5 h-5" />;
      case "RESUELTA": return <CheckCircle className="w-5 h-5" />;
      case "ARCHIVADA": return <Archive className="w-5 h-5" />;
      default: return <AlertTriangle className="w-5 h-5" />;
    }
  };

  const filteredAlertas = filter === "TODAS" 
    ? alertas 
    : alertas.filter(a => a.estado === filter);

  const alertasActivas = alertas.filter(a => a.estado === "ACTIVA").length;

  return (
    <div className="p-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Mis Alertas Académicas</h1>
        <p className="text-gray-600 mt-1">Revisa las alertas que tus profesores han creado sobre tu rendimiento</p>
      </div>

      {alertasActivas > 0 && (
        <div className="bg-red-50 border-l-4 border-red-500 p-4 mb-6 rounded">
          <div className="flex items-center">
            <AlertTriangle className="w-6 h-6 text-red-600 mr-3" />
            <div>
              <h3 className="text-red-800 font-semibold">Tienes {alertasActivas} alerta{alertasActivas > 1 ? 's' : ''} activa{alertasActivas > 1 ? 's' : ''}</h3>
              <p className="text-red-700 text-sm">Revisa las alertas y trabaja en mejorar tu rendimiento académico</p>
            </div>
          </div>
        </div>
      )}

      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="mb-4 flex gap-2">
          <button
            onClick={() => setFilter("TODAS")}
            className={`px-4 py-2 rounded-lg transition-colors ${
              filter === "TODAS" ? "bg-blue-600 text-white" : "bg-gray-100 text-gray-700 hover:bg-gray-200"
            }`}
          >
            Todas
          </button>
          <button
            onClick={() => setFilter("ACTIVA")}
            className={`px-4 py-2 rounded-lg transition-colors ${
              filter === "ACTIVA" ? "bg-red-600 text-white" : "bg-gray-100 text-gray-700 hover:bg-gray-200"
            }`}
          >
            Activas
          </button>
          <button
            onClick={() => setFilter("EN_SEGUIMIENTO")}
            className={`px-4 py-2 rounded-lg transition-colors ${
              filter === "EN_SEGUIMIENTO" ? "bg-yellow-600 text-white" : "bg-gray-100 text-gray-700 hover:bg-gray-200"
            }`}
          >
            En Seguimiento
          </button>
          <button
            onClick={() => setFilter("RESUELTA")}
            className={`px-4 py-2 rounded-lg transition-colors ${
              filter === "RESUELTA" ? "bg-green-600 text-white" : "bg-gray-100 text-gray-700 hover:bg-gray-200"
            }`}
          >
            Resueltas
          </button>
        </div>

        {loading ? (
          <div className="text-center py-8">Cargando...</div>
        ) : filteredAlertas.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            {filter === "TODAS" ? "No tienes alertas registradas" : `No hay alertas ${filter.toLowerCase()}`}
          </div>
        ) : (
          <div className="space-y-4">
            {filteredAlertas.map((alerta) => (
              <div
                key={alerta.id}
                className={`border-l-4 rounded-lg p-4 ${getEstadoColor(alerta.estado)}`}
              >
                <div className="flex items-start justify-between mb-2">
                  <div className="flex items-center gap-2">
                    {getEstadoIcon(alerta.estado)}
                    <h3 className="text-lg font-semibold">{alerta.titulo}</h3>
                  </div>
                  <span className="text-sm font-medium">
                    {alerta.estado.replace("_", " ")}
                  </span>
                </div>
                <p className="text-gray-700 mb-3 mt-2">{alerta.mensaje}</p>
                <div className="flex gap-4 text-sm text-gray-600">
                  <span><strong>Curso:</strong> {alerta.cursoNombre}</span>
                  <span><strong>Profesor:</strong> {alerta.profesorNombre}</span>
                  <span><strong>Fecha:</strong> {new Date(alerta.fechaCreacion).toLocaleDateString()}</span>
                </div>
                {alerta.accionTomada && (
                  <div className="mt-3 p-3 bg-white bg-opacity-50 rounded">
                    <p className="text-sm"><strong>Acción tomada:</strong> {alerta.accionTomada}</p>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default AlertasEstudiantePage;

