import { useEffect, useState } from "react";
import { apiService } from "../../services/api";
import type { MisionProgresoResponse, MisionListResponse, Curso } from "../../types";
import { TrendingUp, Users, CheckCircle2, Clock, XCircle, Search } from "lucide-react";

const ProgresoEstudiantesPage = () => {
  const [cursos, setCursos] = useState<Curso[]>([]);
  const [misiones, setMisiones] = useState<MisionListResponse[]>([]);
  const [progreso, setProgreso] = useState<MisionProgresoResponse | null>(null);
  const [cursoSeleccionado, setCursoSeleccionado] = useState<string | null>(null);
  const [misionSeleccionada, setMisionSeleccionada] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    loadCursos();
  }, []);

  useEffect(() => {
    if (cursoSeleccionado) {
      loadMisionesPorCurso();
    }
  }, [cursoSeleccionado]);

  useEffect(() => {
    if (misionSeleccionada) {
      loadProgreso();
    }
  }, [misionSeleccionada]);

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

  const loadMisionesPorCurso = async () => {
    if (!cursoSeleccionado) return;
    try {
      setLoading(true);
      const misionesData = await apiService.listarMisionesPorCurso(cursoSeleccionado);
      setMisiones(misionesData);
      if (misionesData.length > 0 && !misionSeleccionada) {
        setMisionSeleccionada(misionesData[0].id);
      }
    } catch (e) {
      console.error("Error cargando misiones:", e);
      setError("Error al cargar las misiones");
    } finally {
      setLoading(false);
    }
  };

  const loadProgreso = async () => {
    if (!misionSeleccionada) return;
    try {
      setLoading(true);
      setError(null);
      const progresoData = await apiService.obtenerProgresoMision(misionSeleccionada);
      setProgreso(progresoData);
    } catch (e: unknown) {
      const errorMessage = e instanceof Error ? e.message : "Error al cargar el progreso";
      setError(errorMessage);
      console.error("Error:", e);
    } finally {
      setLoading(false);
    }
  };

  const estudiantesFiltrados = progreso?.estudiantes.filter((est) =>
    est.nombreCompleto.toLowerCase().includes(searchTerm.toLowerCase())
  ) || [];

  const getEstadoColor = (estado: string) => {
    switch (estado.toLowerCase()) {
      case "completada":
        return "bg-green-100 text-green-800 border-green-200";
      case "en_progreso":
        return "bg-blue-100 text-blue-800 border-blue-200";
      case "no_iniciada":
        return "bg-gray-100 text-gray-800 border-gray-200";
      default:
        return "bg-gray-100 text-gray-800 border-gray-200";
    }
  };

  const getEstadoIcon = (estado: string) => {
    switch (estado.toLowerCase()) {
      case "completada":
        return <CheckCircle2 className="w-4 h-4" />;
      case "en_progreso":
        return <Clock className="w-4 h-4" />;
      case "no_iniciada":
        return <XCircle className="w-4 h-4" />;
      default:
        return <XCircle className="w-4 h-4" />;
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Progreso de Estudiantes</h1>
          <p className="text-gray-600 mt-1">
            Visualiza el progreso de tus estudiantes en las misiones
          </p>
        </div>
      </div>

      {/* Selectores */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Curso
            </label>
            <select
              value={cursoSeleccionado || ""}
              onChange={(e) => {
                setCursoSeleccionado(e.target.value);
                setMisionSeleccionada(null);
                setProgreso(null);
              }}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500"
            >
              <option value="">Selecciona un curso</option>
              {cursos.map((curso) => (
                <option key={curso.id} value={curso.id}>
                  {curso.nombre}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Misión
            </label>
            <select
              value={misionSeleccionada || ""}
              onChange={(e) => setMisionSeleccionada(e.target.value)}
              disabled={!cursoSeleccionado || misiones.length === 0}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
            >
              <option value="">Selecciona una misión</option>
              {misiones.map((mision) => (
                <option key={mision.id} value={mision.id}>
                  {mision.titulo}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Estadísticas */}
      {progreso && (
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Total Estudiantes</p>
                <p className="text-2xl font-bold text-gray-900 mt-1">
                  {progreso.totalEstudiantes}
                </p>
              </div>
              <Users className="w-8 h-8 text-blue-500" />
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm border border-green-200 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Completados</p>
                <p className="text-2xl font-bold text-green-600 mt-1">
                  {progreso.completados}
                </p>
              </div>
              <CheckCircle2 className="w-8 h-8 text-green-500" />
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm border border-blue-200 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">En Progreso</p>
                <p className="text-2xl font-bold text-blue-600 mt-1">
                  {progreso.enProgreso}
                </p>
              </div>
              <Clock className="w-8 h-8 text-blue-500" />
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">No Iniciados</p>
                <p className="text-2xl font-bold text-gray-600 mt-1">
                  {progreso.noIniciados}
                </p>
              </div>
              <XCircle className="w-8 h-8 text-gray-500" />
            </div>
          </div>
        </div>
      )}

      {/* Tabla de Estudiantes */}
      {progreso && (
        <div className="bg-white rounded-lg shadow-sm border border-gray-200">
          <div className="p-6 border-b border-gray-200">
            <div className="flex items-center justify-between">
              <h2 className="text-xl font-semibold text-gray-900">
                Progreso: {progreso.titulo}
              </h2>
              <div className="relative w-64">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                <input
                  type="text"
                  placeholder="Buscar estudiante..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500"
                />
              </div>
            </div>
          </div>

          {loading ? (
            <div className="p-12 text-center">
              <div className="text-gray-500">Cargando progreso...</div>
            </div>
          ) : error ? (
            <div className="p-12 text-center">
              <div className="text-red-600">{error}</div>
            </div>
          ) : estudiantesFiltrados.length === 0 ? (
            <div className="p-12 text-center">
              <div className="text-gray-500">No hay estudiantes para mostrar</div>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Estudiante
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Estado
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Progreso
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Última Actividad
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {estudiantesFiltrados.map((estudiante) => (
                    <tr key={estudiante.estudianteId} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center">
                          <div className="w-10 h-10 bg-green-500 rounded-full flex items-center justify-center text-white font-semibold flex-shrink-0">
                            {estudiante.nombreCompleto
                              .split(" ")
                              .map((n) => n[0])
                              .join("")
                              .substring(0, 2)
                              .toUpperCase()}
                          </div>
                          <div className="ml-4">
                            <div className="text-sm font-medium text-gray-900">
                              {estudiante.nombreCompleto}
                            </div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span
                          className={`inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-medium border ${getEstadoColor(
                            estudiante.estado
                          )}`}
                        >
                          {getEstadoIcon(estudiante.estado)}
                          {estudiante.estado.replace("_", " ").toUpperCase()}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center">
                          <div className="w-full bg-gray-200 rounded-full h-2 mr-3">
                            <div
                              className="bg-green-500 h-2 rounded-full transition-all"
                              style={{ width: `${estudiante.porcentajeCompletado}%` }}
                            ></div>
                          </div>
                          <span className="text-sm text-gray-600 min-w-[3rem]">
                            {estudiante.porcentajeCompletado}%
                          </span>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {new Date(estudiante.ultimaActividad).toLocaleDateString("es-ES", {
                          day: "2-digit",
                          month: "short",
                          year: "numeric",
                          hour: "2-digit",
                          minute: "2-digit",
                        })}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {!progreso && !loading && misionSeleccionada && (
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
          <TrendingUp className="w-12 h-12 text-gray-400 mx-auto mb-4" />
          <p className="text-gray-500">Selecciona una misión para ver el progreso</p>
        </div>
      )}
    </div>
  );
};

export default ProgresoEstudiantesPage;

