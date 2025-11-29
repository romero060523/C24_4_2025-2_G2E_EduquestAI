import React, { useEffect, useState } from "react";
import { apiService } from "../../services/api";
import { useAuth } from "../../hooks/useAuth";
import type { SugerenciaIAResponse, SugerenciaMeta, SugerenciaRecompensa } from "../../types";

const SugerenciasIACard: React.FC = () => {
  const { usuario } = useAuth();
  const [sugerencias, setSugerencias] = useState<SugerenciaIAResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadSugerencias();
  }, []);

  const loadSugerencias = async () => {
    try {
      setLoading(true);
      setError(null);
      const estudianteId = localStorage.getItem("estudianteId") || usuario?.id || "";
      console.log("🔍 [SugerenciasIA] estudianteId:", estudianteId);
      console.log("🔍 [SugerenciasIA] usuario:", usuario);
      
      if (!estudianteId) {
        throw new Error("No se encontró el ID del estudiante. Por favor, inicia sesión nuevamente.");
      }
      
      console.log("🔍 [SugerenciasIA] Llamando a obtenerSugerenciasIA...");
      const data = await apiService.obtenerSugerenciasIA(estudianteId);
      console.log("✅ [SugerenciasIA] Datos recibidos:", data);
      setSugerencias(data);
    } catch (e: unknown) {
      const errorMessage = e instanceof Error ? e.message : "Error cargando sugerencias";
      setError(errorMessage);
      console.error("❌ [SugerenciasIA] Error cargando sugerencias:", e);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <div className="flex items-center justify-center py-8">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <div className="text-center py-4">
          <p className="text-red-600 text-sm">{error}</p>
          <button
            onClick={loadSugerencias}
            className="mt-2 text-blue-600 hover:text-blue-700 text-sm font-medium"
          >
            Reintentar
          </button>
        </div>
      </div>
    );
  }

  if (!sugerencias) {
    return null;
  }

  return (
    <div className="bg-gradient-to-br from-blue-50 to-indigo-50 rounded-xl shadow-sm border border-blue-200 p-6">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <span className="text-2xl">🤖</span>
          <h2 className="text-xl font-bold text-gray-900">Sugerencias de IA</h2>
        </div>
        <button
          onClick={loadSugerencias}
          className="text-blue-600 hover:text-blue-700 text-sm font-medium"
        >
          Actualizar
        </button>
      </div>

      {/* Mensaje Motivacional */}
      {sugerencias.mensajeMotivacional && (
        <div className="mb-6 p-4 bg-white rounded-lg border border-blue-100">
          <p className="text-gray-700 text-sm italic">"{sugerencias.mensajeMotivacional}"</p>
        </div>
      )}

      {/* Análisis de Progreso */}
      {sugerencias.analisisProgreso && (
        <div className="mb-6 p-4 bg-white rounded-lg border border-blue-100">
          <h3 className="text-sm font-semibold text-gray-700 mb-2">Análisis de tu progreso</h3>
          <p className="text-gray-600 text-sm">{sugerencias.analisisProgreso}</p>
        </div>
      )}

      {/* Metas Sugeridas */}
      {sugerencias.metasSugeridas && sugerencias.metasSugeridas.length > 0 && (
        <div className="mb-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-3 flex items-center gap-2">
            <span>🎯</span> Metas Sugeridas
          </h3>
          <div className="space-y-3">
            {sugerencias.metasSugeridas.map((meta: SugerenciaMeta, index: number) => (
              <div
                key={index}
                className="bg-white rounded-lg p-4 border border-gray-200 hover:border-blue-300 transition"
              >
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <h4 className="font-semibold text-gray-900 mb-1">{meta.titulo}</h4>
                    <p className="text-sm text-gray-600 mb-2">{meta.descripcion}</p>
                    {meta.objetivo && (
                      <div className="flex items-center gap-2 text-xs text-gray-500">
                        <span className="font-medium">Objetivo:</span>
                        <span>{meta.objetivo} {meta.tipo === "puntos" ? "puntos" : meta.tipo === "mision" ? "misiones" : ""}</span>
                      </div>
                    )}
                    <p className="text-xs text-blue-600 mt-2 italic">💡 {meta.razon}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Recompensas Sugeridas */}
      {sugerencias.recompensasSugeridas && sugerencias.recompensasSugeridas.length > 0 && (
        <div>
          <h3 className="text-lg font-semibold text-gray-900 mb-3 flex items-center gap-2">
            <span>🏆</span> Recompensas Sugeridas
          </h3>
          <div className="space-y-3">
            {sugerencias.recompensasSugeridas.map((recompensa: SugerenciaRecompensa, index: number) => (
              <div
                key={index}
                className="bg-white rounded-lg p-4 border border-gray-200 hover:border-yellow-300 transition"
              >
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <h4 className="font-semibold text-gray-900 mb-1">{recompensa.nombre}</h4>
                    <p className="text-sm text-gray-600 mb-2">{recompensa.descripcion}</p>
                    <p className="text-xs text-yellow-600 mt-2 italic">✨ {recompensa.razon}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default SugerenciasIACard;

