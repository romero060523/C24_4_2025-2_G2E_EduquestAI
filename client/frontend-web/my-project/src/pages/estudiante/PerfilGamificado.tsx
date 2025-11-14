import { useEffect, useState } from "react";
import { useAuth } from "../../hooks/useAuth";
import { apiService } from "../../services/api";
import type { PerfilGamificadoResponse } from "../../types";

const PerfilGamificado = () => {
  const { usuario } = useAuth();
  const [perfil, setPerfil] = useState<PerfilGamificadoResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadPerfil();
  }, []);

  const loadPerfil = async () => {
    setLoading(true);
    setError(null);
    try {
      const estudianteId = localStorage.getItem("estudianteId") || usuario?.id || "";
      if (!estudianteId) {
        throw new Error("No se encontró el ID del estudiante");
      }
      const data = await apiService.obtenerPerfilGamificado(estudianteId);
      setPerfil(data);
    } catch (e: unknown) {
      const errorMessage = e instanceof Error ? e.message : "Error cargando perfil";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-gray-600">Cargando perfil...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
        Error: {error}
      </div>
    );
  }

  if (!perfil) {
    return null;
  }

  const porcentajeProgreso = perfil.puntosParaSiguienteNivel > 0
    ? ((perfil.puntosTotales % getPuntosParaNivelActual(perfil.nivel)) / perfil.puntosParaSiguienteNivel) * 100
    : 100;

  function getPuntosParaNivelActual(nivel: number): number {
    switch (nivel) {
      case 1: return 0;
      case 2: return 100;
      case 3: return 500;
      case 4: return 1000;
      case 5: return 2500;
      case 6: return 5000;
      default: return 0;
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Mi Perfil Gamificado</h1>
        <p className="text-gray-500 mt-1">
          Tu progreso, logros y estadísticas
        </p>
      </div>

      {/* Tarjetas de estadísticas principales */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Nivel */}
        <div className="bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl shadow-lg p-6 text-white">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-medium opacity-90">Nivel Actual</h3>
            <span className="text-3xl">🎯</span>
          </div>
          <p className="text-4xl font-bold mb-2">{perfil.nivel}</p>
          <p className="text-lg opacity-90">{perfil.nombreNivel}</p>
          {perfil.puntosParaSiguienteNivel > 0 && (
            <div className="mt-4">
              <div className="flex items-center justify-between text-xs mb-1">
                <span>Próximo nivel</span>
                <span>{perfil.puntosParaSiguienteNivel} pts</span>
              </div>
              <div className="w-full bg-white/20 rounded-full h-2">
                <div
                  className="bg-white rounded-full h-2 transition-all"
                  style={{ width: `${Math.min(porcentajeProgreso, 100)}%` }}
                />
              </div>
            </div>
          )}
        </div>

        {/* Puntos */}
        <div className="bg-gradient-to-br from-yellow-500 to-yellow-600 rounded-xl shadow-lg p-6 text-white">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-medium opacity-90">Puntos Totales</h3>
            <span className="text-3xl">⭐</span>
          </div>
          <p className="text-4xl font-bold mb-2">{perfil.puntosTotales}</p>
          <p className="text-sm opacity-90">
            {perfil.puntosParaSiguienteNivel > 0
              ? `${perfil.puntosParaSiguienteNivel} para el siguiente nivel`
              : "¡Nivel máximo alcanzado!"}
          </p>
        </div>

        {/* Misiones */}
        <div className="bg-gradient-to-br from-green-500 to-green-600 rounded-xl shadow-lg p-6 text-white">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-medium opacity-90">Misiones</h3>
            <span className="text-3xl">✅</span>
          </div>
          <p className="text-4xl font-bold mb-2">{perfil.misionesCompletadas}</p>
          <p className="text-sm opacity-90">Completadas</p>
        </div>
      </div>

      {/* Logros */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-xl font-bold text-gray-900">Logros</h2>
            <p className="text-sm text-gray-500">
              {perfil.logrosObtenidos} de {perfil.logros.length} logros obtenidos
            </p>
          </div>
        </div>

        {perfil.logros.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            No hay logros disponibles aún
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {perfil.logros.map((logro) => (
              <div
                key={logro.id}
                className={`p-4 rounded-lg border-2 transition-all ${
                  logro.obtenido
                    ? "bg-green-50 border-green-300 shadow-md"
                    : "bg-gray-50 border-gray-200 opacity-60"
                }`}
              >
                <div className="flex items-start gap-3">
                  <div className="text-3xl">
                    {logro.icono || (logro.obtenido ? "🏆" : "🔒")}
                  </div>
                  <div className="flex-1">
                    <h3
                      className={`font-semibold mb-1 ${
                        logro.obtenido ? "text-gray-900" : "text-gray-500"
                      }`}
                    >
                      {logro.nombre}
                    </h3>
                    <p
                      className={`text-sm mb-2 ${
                        logro.obtenido ? "text-gray-600" : "text-gray-400"
                      }`}
                    >
                      {logro.descripcion}
                    </p>
                    <div className="flex items-center gap-2 text-xs">
                      <span
                        className={`px-2 py-1 rounded ${
                          logro.obtenido
                            ? "bg-green-100 text-green-800"
                            : "bg-gray-100 text-gray-500"
                        }`}
                      >
                        {logro.puntosRequeridos} pts
                      </span>
                      {logro.fechaObtenido && (
                        <span className="text-gray-500">
                          Obtenido: {new Date(logro.fechaObtenido).toLocaleDateString("es-ES")}
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default PerfilGamificado;

