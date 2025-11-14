import { useEffect, useState } from "react";
import { apiService } from "../../services/api";
import type { RankingResponse, Curso } from "../../types";
import { Trophy, Medal, Award, Users, TrendingUp } from "lucide-react";

const RankingGrupoPage = () => {
  const [cursos, setCursos] = useState<Curso[]>([]);
  const [ranking, setRanking] = useState<RankingResponse | null>(null);
  const [cursoSeleccionado, setCursoSeleccionado] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadCursos();
  }, []);

  useEffect(() => {
    if (cursoSeleccionado) {
      loadRanking();
    }
  }, [cursoSeleccionado]);

  const loadCursos = async () => {
    try {
      const profesorId = localStorage.getItem("profesorId") || localStorage.getItem("userId") || "";
      if (profesorId) {
        const cursosData = await apiService.listarCursosPorProfesor(profesorId);
        setCursos(cursosData);
        if (cursosData.length > 0 && !cursoSeleccionado) {
          setCursoSeleccionado(cursosData[0].id);
        }
      }
    } catch (e) {
      console.error("Error cargando cursos:", e);
      setError("Error al cargar los cursos");
    }
  };

  const loadRanking = async () => {
    if (!cursoSeleccionado) return;
    try {
      setLoading(true);
      setError(null);
      const rankingData = await apiService.obtenerRankingPorCursoProfesor(cursoSeleccionado);
      setRanking(rankingData);
    } catch (e: unknown) {
      const errorMessage = e instanceof Error ? e.message : "Error al cargar el ranking";
      setError(errorMessage);
      console.error("Error:", e);
    } finally {
      setLoading(false);
    }
  };

  const getMedalla = (posicion: number) => {
    switch (posicion) {
      case 1:
        return <Trophy className="w-6 h-6 text-yellow-500" />;
      case 2:
        return <Medal className="w-6 h-6 text-gray-400" />;
      case 3:
        return <Award className="w-6 h-6 text-orange-500" />;
      default:
        return (
          <span className="w-6 h-6 flex items-center justify-center text-gray-500 font-bold">
            {posicion}
          </span>
        );
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Ranking de Grupo</h1>
          <p className="text-gray-600 mt-1">
            Visualiza el ranking y fomenta la competencia sana entre tus estudiantes
          </p>
        </div>
      </div>

      {/* Selector de Curso */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Seleccionar Curso
        </label>
        <select
          value={cursoSeleccionado || ""}
          onChange={(e) => {
            setCursoSeleccionado(e.target.value);
            setRanking(null);
          }}
          className="w-full md:w-1/2 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500"
        >
          <option value="">Selecciona un curso</option>
          {cursos.map((curso) => (
            <option key={curso.id} value={curso.id}>
              {curso.nombre}
            </option>
          ))}
        </select>
      </div>

      {/* Estadísticas del Ranking */}
      {ranking && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Total Estudiantes</p>
                <p className="text-2xl font-bold text-gray-900 mt-1">
                  {ranking.totalEstudiantes}
                </p>
              </div>
              <Users className="w-8 h-8 text-blue-500" />
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm border border-green-200 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Curso</p>
                <p className="text-lg font-semibold text-green-700 mt-1 truncate">
                  {ranking.cursoNombre}
                </p>
              </div>
              <TrendingUp className="w-8 h-8 text-green-500" />
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm border border-yellow-200 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Puntos Máximos</p>
                <p className="text-2xl font-bold text-yellow-600 mt-1">
                  {ranking.estudiantes[0]?.puntosTotales || 0}
                </p>
              </div>
              <Trophy className="w-8 h-8 text-yellow-500" />
            </div>
          </div>
        </div>
      )}

      {/* Tabla de Ranking */}
      {ranking && (
        <div className="bg-white rounded-lg shadow-sm border border-gray-200">
          <div className="p-6 border-b border-gray-200">
            <h2 className="text-xl font-semibold text-gray-900">
              Ranking: {ranking.cursoNombre}
            </h2>
          </div>

          {loading ? (
            <div className="p-12 text-center">
              <div className="text-gray-500">Cargando ranking...</div>
            </div>
          ) : error ? (
            <div className="p-12 text-center">
              <div className="text-red-600">{error}</div>
            </div>
          ) : ranking.estudiantes.length === 0 ? (
            <div className="p-12 text-center">
              <div className="text-gray-500">
                No hay estudiantes en este curso aún
              </div>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Posición
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Estudiante
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Nivel
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Puntos
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Misiones Completadas
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {ranking.estudiantes.map((estudiante) => (
                    <tr
                      key={estudiante.estudianteId}
                      className={`hover:bg-gray-50 border-l-4 ${
                        estudiante.posicion === 1
                          ? "border-yellow-400"
                          : estudiante.posicion === 2
                          ? "border-gray-400"
                          : estudiante.posicion === 3
                          ? "border-orange-400"
                          : "border-transparent"
                      }`}
                    >
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center justify-center">
                          {getMedalla(estudiante.posicion)}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center">
                          <div className="w-10 h-10 bg-green-500 rounded-full flex items-center justify-center text-white font-semibold flex-shrink-0">
                            {estudiante.nombreEstudiante
                              .split(" ")
                              .map((n) => n[0])
                              .join("")
                              .substring(0, 2)
                              .toUpperCase()}
                          </div>
                          <div className="ml-4">
                            <div className="text-sm font-medium text-gray-900">
                              {estudiante.nombreEstudiante}
                            </div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                          {estudiante.nombreNivel} (Nv. {estudiante.nivel})
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-semibold text-gray-900">
                          {estudiante.puntosTotales.toLocaleString()}
                        </div>
                        <div className="text-xs text-gray-500">puntos</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-medium text-gray-900">
                          {estudiante.misionesCompletadas}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* Mensaje cuando no hay curso seleccionado */}
      {!ranking && !loading && cursoSeleccionado && (
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
          <Trophy className="w-12 h-12 text-gray-400 mx-auto mb-4" />
          <p className="text-gray-500">Cargando ranking del curso...</p>
        </div>
      )}

      {!cursoSeleccionado && (
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
          <Trophy className="w-12 h-12 text-gray-400 mx-auto mb-4" />
          <p className="text-gray-500">Selecciona un curso para ver el ranking</p>
        </div>
      )}
    </div>
  );
};

export default RankingGrupoPage;

