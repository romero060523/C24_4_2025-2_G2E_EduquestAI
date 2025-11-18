import { useState, useEffect } from "react";
import { reportesApi } from "../services/gamificacionApi";
import type {
  EstadisticasGamificacion,
  ReporteEstudiante,
  ReporteCurso,
} from "../services/gamificacionApi";
import {
  Users,
  BookOpen,
  Target,
  Trophy,
  TrendingUp,
  Award,
  Activity,
} from "lucide-react";

export default function Reportes() {
  const [estadisticas, setEstadisticas] = useState<EstadisticasGamificacion | null>(
    null
  );
  const [reporteEstudiantes, setReporteEstudiantes] = useState<ReporteEstudiante[]>([]);
  const [reporteCursos, setReporteCursos] = useState<ReporteCurso[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<"general" | "estudiantes" | "cursos">(
    "general"
  );

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      setLoading(true);
      const [stats, estudiantes, cursos] = await Promise.all([
        reportesApi.estadisticasGenerales(),
        reportesApi.reporteEstudiantes(),
        reportesApi.reporteCursos(),
      ]);
      setEstadisticas(stats);
      setReporteEstudiantes(estudiantes);
      setReporteCursos(cursos);
    } catch (error) {
      console.error("Error cargando reportes:", error);
      alert("Error al cargar los reportes");
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-gray-500">Cargando reportes...</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Tabs */}
      <div className="bg-white rounded-lg shadow-md p-4">
        <div className="flex gap-2 border-b">
          <button
            onClick={() => setActiveTab("general")}
            className={`px-4 py-2 font-medium transition ${
              activeTab === "general"
                ? "border-b-2 border-blue-600 text-blue-600"
                : "text-gray-600 hover:text-gray-900"
            }`}
          >
            Estadísticas Generales
          </button>
          <button
            onClick={() => setActiveTab("estudiantes")}
            className={`px-4 py-2 font-medium transition ${
              activeTab === "estudiantes"
                ? "border-b-2 border-blue-600 text-blue-600"
                : "text-gray-600 hover:text-gray-900"
            }`}
          >
            Reporte de Estudiantes
          </button>
          <button
            onClick={() => setActiveTab("cursos")}
            className={`px-4 py-2 font-medium transition ${
              activeTab === "cursos"
                ? "border-b-2 border-blue-600 text-blue-600"
                : "text-gray-600 hover:text-gray-900"
            }`}
          >
            Reporte de Cursos
          </button>
        </div>
      </div>

      {/* Estadísticas Generales */}
      {activeTab === "general" && estadisticas && (
        <div className="space-y-6">
          {/* Cards de Estadísticas */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <div className="bg-white rounded-lg shadow-md p-6 border-l-4 border-blue-500">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600">Total Estudiantes</p>
                  <p className="text-2xl font-bold text-gray-900">
                    {estadisticas.total_estudiantes}
                  </p>
                </div>
                <Users className="w-10 h-10 text-blue-500" />
              </div>
            </div>

            <div className="bg-white rounded-lg shadow-md p-6 border-l-4 border-green-500">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600">Total Profesores</p>
                  <p className="text-2xl font-bold text-gray-900">
                    {estadisticas.total_profesores}
                  </p>
                </div>
                <BookOpen className="w-10 h-10 text-green-500" />
              </div>
            </div>

            <div className="bg-white rounded-lg shadow-md p-6 border-l-4 border-purple-500">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600">Total Cursos</p>
                  <p className="text-2xl font-bold text-gray-900">
                    {estadisticas.total_cursos}
                  </p>
                </div>
                <Target className="w-10 h-10 text-purple-500" />
              </div>
            </div>

            <div className="bg-white rounded-lg shadow-md p-6 border-l-4 border-yellow-500">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600">Total Misiones</p>
                  <p className="text-2xl font-bold text-gray-900">
                    {estadisticas.total_misiones}
                  </p>
                </div>
                <Trophy className="w-10 h-10 text-yellow-500" />
              </div>
            </div>
          </div>

          {/* Estadísticas de Gamificación */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="bg-white rounded-lg shadow-md p-6">
              <h3 className="text-lg font-bold mb-4 flex items-center gap-2">
                <TrendingUp className="w-5 h-5" />
                Puntos y Logros
              </h3>
              <div className="space-y-4">
                <div className="flex justify-between items-center">
                  <span className="text-gray-600">Total Puntos Otorgados</span>
                  <span className="text-xl font-bold">
                    {estadisticas.total_puntos_otorgados.toLocaleString()}
                  </span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-gray-600">Promedio por Estudiante</span>
                  <span className="text-xl font-bold">
                    {estadisticas.promedio_puntos_por_estudiante.toFixed(2)}
                  </span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-gray-600">Total Logros Obtenidos</span>
                  <span className="text-xl font-bold">
                    {estadisticas.total_logros_obtenidos}
                  </span>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow-md p-6">
              <h3 className="text-lg font-bold mb-4 flex items-center gap-2">
                <Activity className="w-5 h-5" />
                Actividad del Mes
              </h3>
              <div className="space-y-4">
                <div className="flex justify-between items-center">
                  <span className="text-gray-600">Estudiantes Activos</span>
                  <span className="text-xl font-bold">
                    {estadisticas.estudiantes_activos_mes}
                  </span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-gray-600">Misiones Completadas</span>
                  <span className="text-xl font-bold">
                    {estadisticas.misiones_completadas_mes}
                  </span>
                </div>
              </div>
            </div>
          </div>

          {/* Top Estudiantes */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h3 className="text-lg font-bold mb-4 flex items-center gap-2">
              <Award className="w-5 h-5" />
              Top 10 Estudiantes por Puntos
            </h3>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-4 py-3 text-left text-sm font-semibold">Posición</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold">Nombre</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold">
                      Puntos Totales
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {estadisticas.estudiantes_top_puntos.map((estudiante, index) => (
                    <tr key={estudiante.estudiante_id}>
                      <td className="px-4 py-3">
                        <span className="text-lg font-bold">
                          {index === 0 ? "🥇" : index === 1 ? "🥈" : index === 2 ? "🥉" : index + 1}
                        </span>
                      </td>
                      <td className="px-4 py-3 font-medium">{estudiante.nombre}</td>
                      <td className="px-4 py-3 font-bold text-blue-600">
                        {estudiante.puntos_totales.toLocaleString()}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* Cursos Más Activos */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h3 className="text-lg font-bold mb-4">Cursos Más Activos</h3>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-4 py-3 text-left text-sm font-semibold">Curso</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold">Código</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold">
                      Estudiantes
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {estadisticas.cursos_mas_activos.map((curso) => (
                    <tr key={curso.id}>
                      <td className="px-4 py-3 font-medium">{curso.nombre}</td>
                      <td className="px-4 py-3 text-gray-600">{curso.codigo_curso}</td>
                      <td className="px-4 py-3 font-bold">{curso.total_estudiantes}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* Reporte de Estudiantes */}
      {activeTab === "estudiantes" && (
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="text-lg font-bold mb-4">Reporte Detallado de Estudiantes</h3>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-4 py-3 text-left text-sm font-semibold">Nombre</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">Email</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">Puntos</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">Nivel</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">
                    Misiones Completadas
                  </th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">Logros</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">Cursos</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {reporteEstudiantes.map((estudiante) => (
                  <tr key={estudiante.estudiante_id}>
                    <td className="px-4 py-3 font-medium">{estudiante.nombre}</td>
                    <td className="px-4 py-3 text-gray-600">{estudiante.email}</td>
                    <td className="px-4 py-3 font-bold text-blue-600">
                      {estudiante.puntos_totales.toLocaleString()}
                    </td>
                    <td className="px-4 py-3">
                      <span className="px-2 py-1 bg-purple-100 text-purple-800 rounded text-sm">
                        {estudiante.nombre_nivel}
                      </span>
                    </td>
                    <td className="px-4 py-3">{estudiante.misiones_completadas}</td>
                    <td className="px-4 py-3">{estudiante.logros_obtenidos}</td>
                    <td className="px-4 py-3">{estudiante.cursos_inscritos}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Reporte de Cursos */}
      {activeTab === "cursos" && (
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="text-lg font-bold mb-4">Reporte Detallado de Cursos</h3>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-4 py-3 text-left text-sm font-semibold">Curso</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">Código</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">Profesor</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">
                    Estudiantes
                  </th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">
                    Misiones Activas
                  </th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">
                    Misiones Completadas
                  </th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">
                    Promedio Puntos
                  </th>
                  <th className="px-4 py-3 text-left text-sm font-semibold">
                    Tasa Completación
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {reporteCursos.map((curso) => (
                  <tr key={curso.curso_id}>
                    <td className="px-4 py-3 font-medium">{curso.nombre}</td>
                    <td className="px-4 py-3 text-gray-600">{curso.codigo}</td>
                    <td className="px-4 py-3">{curso.profesor_nombre}</td>
                    <td className="px-4 py-3 font-bold">{curso.total_estudiantes}</td>
                    <td className="px-4 py-3">{curso.misiones_activas}</td>
                    <td className="px-4 py-3">{curso.misiones_completadas}</td>
                    <td className="px-4 py-3 font-medium">
                      {curso.promedio_puntos_curso.toFixed(2)}
                    </td>
                    <td className="px-4 py-3">
                      <span className="px-2 py-1 bg-green-100 text-green-800 rounded text-sm">
                        {curso.tasa_completacion.toFixed(1)}%
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}

