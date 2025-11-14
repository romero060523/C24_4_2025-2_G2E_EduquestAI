import { useEffect, useState } from "react";
import { apiService } from "../../services/api";
import type { RankingResponse, Curso } from "../../types";

const RankingPage = () => {
  const [ranking, setRanking] = useState<RankingResponse | null>(null);
  const [cursos, setCursos] = useState<Curso[]>([]);
  const [cursoSeleccionado, setCursoSeleccionado] = useState<string | null>(null);
  const [tipoRanking, setTipoRanking] = useState<"global" | "curso">("global");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadCursos();
  }, []);

  useEffect(() => {
    if (tipoRanking === "global") {
      loadRankingGlobal();
    } else if (cursoSeleccionado) {
      loadRankingPorCurso(cursoSeleccionado);
    }
  }, [tipoRanking, cursoSeleccionado]);

  const loadCursos = async () => {
    try {
      const estudianteId = localStorage.getItem("estudianteId") || "";
      if (estudianteId) {
        const cursosData = await apiService.listarCursosPorEstudiante(estudianteId);
        setCursos(cursosData);
        if (cursosData.length > 0 && !cursoSeleccionado) {
          setCursoSeleccionado(cursosData[0].id);
        }
      }
    } catch (e) {
      console.error("Error cargando cursos:", e);
    }
  };

  const loadRankingGlobal = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await apiService.obtenerRankingGlobal();
      setRanking(data);
    } catch (e: unknown) {
      const errorMessage = e instanceof Error ? e.message : "Error cargando ranking";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const loadRankingPorCurso = async (cursoId: string) => {
    setLoading(true);
    setError(null);
    try {
      const data = await apiService.obtenerRankingPorCurso(cursoId);
      setRanking(data);
    } catch (e: unknown) {
      const errorMessage = e instanceof Error ? e.message : "Error cargando ranking";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const getMedalla = (posicion: number) => {
    if (posicion === 1) return "🥇";
    if (posicion === 2) return "🥈";
    if (posicion === 3) return "🥉";
    return `#${posicion}`;
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-gray-600">Cargando ranking...</div>
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

  if (!ranking) {
    return null;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Ranking</h1>
        <p className="text-gray-500 mt-1">
          Clasificación de estudiantes por puntos y misiones completadas
        </p>
      </div>

      {/* Selector de tipo de ranking */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <div className="flex items-center gap-4">
          <label className="text-sm font-medium text-gray-700">Tipo de ranking:</label>
          <div className="flex gap-2">
            <button
              onClick={() => setTipoRanking("global")}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                tipoRanking === "global"
                  ? "bg-blue-600 text-white"
                  : "bg-gray-100 text-gray-700 hover:bg-gray-200"
              }`}
            >
              Global
            </button>
            <button
              onClick={() => setTipoRanking("curso")}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                tipoRanking === "curso"
                  ? "bg-blue-600 text-white"
                  : "bg-gray-100 text-gray-700 hover:bg-gray-200"
              }`}
            >
              Por Curso
            </button>
          </div>

          {tipoRanking === "curso" && cursos.length > 0 && (
            <select
              value={cursoSeleccionado || ""}
              onChange={(e) => setCursoSeleccionado(e.target.value)}
              className="ml-4 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              {cursos.map((curso) => (
                <option key={curso.id} value={curso.id}>
                  {curso.nombre}
                </option>
              ))}
            </select>
          )}
        </div>
      </div>

      {/* Ranking */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        <div className="p-6 border-b border-gray-200">
          <h2 className="text-xl font-bold text-gray-900">{ranking.cursoNombre}</h2>
          <p className="text-sm text-gray-500">
            Total de estudiantes: {ranking.totalEstudiantes}
          </p>
        </div>

        {ranking.estudiantes.length === 0 ? (
          <div className="p-12 text-center text-gray-500">
            No hay estudiantes en este ranking
          </div>
        ) : (
          <div className="divide-y divide-gray-200">
            {ranking.estudiantes.map((estudiante, index) => (
              <div
                key={estudiante.estudianteId}
                className={`p-4 hover:bg-gray-50 transition-colors ${
                  index < 3 ? "bg-gradient-to-r from-yellow-50 to-transparent" : ""
                }`}
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-4 flex-1">
                    <div className="text-2xl font-bold w-12 text-center">
                      {getMedalla(estudiante.posicion)}
                    </div>
                    <div className="flex-1">
                      <h3 className="font-semibold text-gray-900">
                        {estudiante.nombreEstudiante}
                      </h3>
                      <div className="flex items-center gap-4 mt-1 text-sm text-gray-500">
                        <span>Nivel {estudiante.nivel} - {estudiante.nombreNivel}</span>
                        <span>•</span>
                        <span>{estudiante.misionesCompletadas} misiones</span>
                      </div>
                    </div>
                  </div>
                  <div className="text-right">
                    <div className="text-2xl font-bold text-blue-600">
                      {estudiante.puntosTotales}
                    </div>
                    <div className="text-xs text-gray-500">puntos</div>
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

export default RankingPage;

