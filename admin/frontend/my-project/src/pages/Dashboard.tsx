import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import {
  reportesApi,
  reglasGamificacionApi,
  type EstadisticasGamificacion,
} from "../services/gamificacionApi";
import API from "../services/api";
import {
  Users,
  BookOpen,
  Target,
  Trophy,
  TrendingUp,
  Award,
  Activity,
  Settings,
  BarChart3,
  ArrowRight,
  Zap,
  CheckCircle2,
  Clock,
  Star,
} from "lucide-react";

interface UserStats {
  total: number;
  activos: number;
}

interface CursoStats {
  total: number;
  activos: number;
}

export default function Dashboard() {
  const [estadisticas, setEstadisticas] = useState<EstadisticasGamificacion | null>(null);
  const [reglasActivas, setReglasActivas] = useState<number>(0);
  const [userStats, setUserStats] = useState<UserStats>({ total: 0, activos: 0 });
  const [cursoStats, setCursoStats] = useState<CursoStats>({ total: 0, activos: 0 });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      setLoading(true);
      const [stats, reglas] = await Promise.all([
        reportesApi.estadisticasGenerales(),
        reglasGamificacionApi.listar(true),
      ]);
      setEstadisticas(stats);
      setReglasActivas(reglas.filter((r) => r.activo).length);

      // Cargar estadísticas de usuarios y cursos
      try {
        const [usersRes, cursosRes] = await Promise.all([
          API.get("/users/"),
          API.get("/cursos/"),
        ]);
        const users = usersRes.data || [];
        const cursos = cursosRes.data || [];
        setUserStats({
          total: users.length,
          activos: users.filter((u: any) => u.activo).length,
        });
        setCursoStats({
          total: cursos.length,
          activos: cursos.filter((c: any) => c.activo).length,
        });
      } catch (error) {
        console.error("Error cargando datos adicionales:", error);
      }
    } catch (error) {
      console.error("Error cargando dashboard:", error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header con Accesos Rápidos */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Link
          to="/admin/reglas-gamificacion"
          className="bg-gradient-to-r from-purple-600 to-purple-700 rounded-lg shadow-lg p-6 text-white hover:from-purple-700 hover:to-purple-800 transition-all transform hover:scale-105"
        >
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-lg font-semibold mb-1">Reglas</h3>
              <p className="text-sm opacity-90">
                {reglasActivas} reglas activas
              </p>
            </div>
            <Settings className="w-12 h-12 opacity-80" />
          </div>
          <div className="mt-4 flex items-center text-sm font-medium">
            <span>Gestionar reglas</span>
            <ArrowRight className="w-4 h-4 ml-2" />
          </div>
        </Link>

        <Link
          to="/admin/reportes"
          className="bg-gradient-to-r from-blue-600 to-blue-700 rounded-lg shadow-lg p-6 text-white hover:from-blue-700 hover:to-blue-800 transition-all transform hover:scale-105"
        >
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-lg font-semibold mb-1">Reportes Generales</h3>
              <p className="text-sm opacity-90">
                Ver estadísticas completas
              </p>
            </div>
            <BarChart3 className="w-12 h-12 opacity-80" />
          </div>
          <div className="mt-4 flex items-center text-sm font-medium">
            <span>Ver reportes</span>
            <ArrowRight className="w-4 h-4 ml-2" />
          </div>
        </Link>

        <Link
          to="/admin/users"
          className="bg-gradient-to-r from-green-600 to-green-700 rounded-lg shadow-lg p-6 text-white hover:from-green-700 hover:to-green-800 transition-all transform hover:scale-105"
        >
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-lg font-semibold mb-1">Gestión de Usuarios</h3>
              <p className="text-sm opacity-90">
                {userStats.total} usuarios totales
              </p>
            </div>
            <Users className="w-12 h-12 opacity-80" />
          </div>
          <div className="mt-4 flex items-center text-sm font-medium">
            <span>Administrar</span>
            <ArrowRight className="w-4 h-4 ml-2" />
          </div>
        </Link>
      </div>

      {/* Estadísticas Principales */}
      {estadisticas && (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {/* Total Estudiantes */}
            <div className="bg-white rounded-xl shadow-md p-6 border-l-4 border-blue-500 hover:shadow-lg transition-shadow">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600 mb-1">
                    Total Estudiantes
                  </p>
                  <p className="text-3xl font-bold text-gray-900">
                    {estadisticas.total_estudiantes}
                  </p>
                  <p className="text-xs text-gray-500 mt-1">
                    {estadisticas.estudiantes_activos_mes} activos este mes
                  </p>
                </div>
                <div className="bg-blue-100 rounded-full p-3">
                  <Users className="w-8 h-8 text-blue-600" />
                </div>
              </div>
            </div>

            {/* Total Profesores */}
            <div className="bg-white rounded-xl shadow-md p-6 border-l-4 border-green-500 hover:shadow-lg transition-shadow">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600 mb-1">
                    Total Profesores
                  </p>
                  <p className="text-3xl font-bold text-gray-900">
                    {estadisticas.total_profesores}
                  </p>
                  <p className="text-xs text-gray-500 mt-1">En la plataforma</p>
                </div>
                <div className="bg-green-100 rounded-full p-3">
                  <BookOpen className="w-8 h-8 text-green-600" />
                </div>
              </div>
            </div>

            {/* Total Cursos */}
            <div className="bg-white rounded-xl shadow-md p-6 border-l-4 border-purple-500 hover:shadow-lg transition-shadow">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600 mb-1">
                    Total Cursos
                  </p>
                  <p className="text-3xl font-bold text-gray-900">
                    {estadisticas.total_cursos}
                  </p>
                  <p className="text-xs text-gray-500 mt-1">
                    {cursoStats.activos} cursos activos
                  </p>
                </div>
                <div className="bg-purple-100 rounded-full p-3">
                  <Target className="w-8 h-8 text-purple-600" />
                </div>
              </div>
            </div>

            {/* Total Misiones */}
            <div className="bg-white rounded-xl shadow-md p-6 border-l-4 border-yellow-500 hover:shadow-lg transition-shadow">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600 mb-1">
                    Total Misiones
                  </p>
                  <p className="text-3xl font-bold text-gray-900">
                    {estadisticas.total_misiones}
                  </p>
                  <p className="text-xs text-gray-500 mt-1">
                    {estadisticas.misiones_completadas_mes} completadas este mes
                  </p>
                </div>
                <div className="bg-yellow-100 rounded-full p-3">
                  <Trophy className="w-8 h-8 text-yellow-600" />
                </div>
              </div>
            </div>
          </div>

          {/* Estadísticas de Gamificación */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {/* Puntos Totales */}
            <div className="bg-gradient-to-br from-indigo-500 to-indigo-600 rounded-xl shadow-lg p-6 text-white">
              <div className="flex items-center justify-between mb-2">
                <p className="text-sm font-medium opacity-90">Puntos Totales</p>
                <Zap className="w-5 h-5 opacity-80" />
              </div>
              <p className="text-3xl font-bold">
                {estadisticas.total_puntos_otorgados.toLocaleString()}
              </p>
              <p className="text-xs opacity-75 mt-1">
                {estadisticas.promedio_puntos_por_estudiante.toFixed(1)} pts/estudiante
              </p>
            </div>

            {/* Logros Obtenidos */}
            <div className="bg-gradient-to-br from-amber-500 to-amber-600 rounded-xl shadow-lg p-6 text-white">
              <div className="flex items-center justify-between mb-2">
                <p className="text-sm font-medium opacity-90">Logros Obtenidos</p>
                <Award className="w-5 h-5 opacity-80" />
              </div>
              <p className="text-3xl font-bold">
                {estadisticas.total_logros_obtenidos}
              </p>
              <p className="text-xs opacity-75 mt-1">En toda la plataforma</p>
            </div>

            {/* Estudiantes Activos */}
            <div className="bg-gradient-to-br from-emerald-500 to-emerald-600 rounded-xl shadow-lg p-6 text-white">
              <div className="flex items-center justify-between mb-2">
                <p className="text-sm font-medium opacity-90">Activos Este Mes</p>
                <Activity className="w-5 h-5 opacity-80" />
              </div>
              <p className="text-3xl font-bold">
                {estadisticas.estudiantes_activos_mes}
              </p>
              <p className="text-xs opacity-75 mt-1">
                {estadisticas.total_estudiantes > 0
                  ? Math.round(
                      (estadisticas.estudiantes_activos_mes /
                        estadisticas.total_estudiantes) *
                        100
                    )
                  : 0}
                % del total
              </p>
            </div>

            {/* Misiones Completadas */}
            <div className="bg-gradient-to-br from-rose-500 to-rose-600 rounded-xl shadow-lg p-6 text-white">
              <div className="flex items-center justify-between mb-2">
                <p className="text-sm font-medium opacity-90">Misiones Completadas</p>
                <CheckCircle2 className="w-5 h-5 opacity-80" />
              </div>
              <p className="text-3xl font-bold">
                {estadisticas.misiones_completadas_mes}
              </p>
              <p className="text-xs opacity-75 mt-1">Este mes</p>
            </div>
          </div>

          {/* Top Estudiantes y Cursos Más Activos */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Top 5 Estudiantes */}
            <div className="bg-white rounded-xl shadow-md p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                  <Star className="w-5 h-5 text-yellow-500" />
                  Top 5 Estudiantes
                </h3>
                <Link
                  to="/admin/reportes"
                  className="text-sm text-blue-600 hover:text-blue-700 font-medium flex items-center gap-1"
                >
                  Ver todos
                  <ArrowRight className="w-4 h-4" />
                </Link>
              </div>
              <div className="space-y-3">
                {estadisticas.estudiantes_top_puntos.slice(0, 5).map((estudiante, index) => (
                  <div
                    key={estudiante.estudiante_id}
                    className="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition"
                  >
                    <div className="flex items-center gap-3">
                      <div className="flex-shrink-0 w-8 h-8 rounded-full bg-gradient-to-br from-yellow-400 to-yellow-600 flex items-center justify-center text-white font-bold text-sm">
                        {index === 0 ? "🥇" : index === 1 ? "🥈" : index === 2 ? "🥉" : index + 1}
                      </div>
                      <div>
                        <p className="font-medium text-gray-900">{estudiante.nombre}</p>
                        <p className="text-xs text-gray-500">
                          {estudiante.puntos_totales.toLocaleString()} puntos
                        </p>
                      </div>
                    </div>
                    <TrendingUp className="w-5 h-5 text-green-500" />
                  </div>
                ))}
                {estadisticas.estudiantes_top_puntos.length === 0 && (
                  <p className="text-center text-gray-500 py-4">
                    No hay estudiantes con puntos aún
                  </p>
                )}
              </div>
            </div>

            {/* Cursos Más Activos */}
            <div className="bg-white rounded-xl shadow-md p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                  <BookOpen className="w-5 h-5 text-purple-500" />
                  Cursos Más Activos
                </h3>
                <Link
                  to="/admin/cursos"
                  className="text-sm text-blue-600 hover:text-blue-700 font-medium flex items-center gap-1"
                >
                  Ver todos
                  <ArrowRight className="w-4 h-4" />
                </Link>
              </div>
              <div className="space-y-3">
                {estadisticas.cursos_mas_activos.slice(0, 5).map((curso, index) => (
                  <div
                    key={curso.id}
                    className="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition"
                  >
                    <div className="flex items-center gap-3 flex-1 min-w-0">
                      <div className="flex-shrink-0 w-8 h-8 rounded-full bg-gradient-to-br from-purple-400 to-purple-600 flex items-center justify-center text-white font-bold text-sm">
                        {index + 1}
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="font-medium text-gray-900 truncate">{curso.nombre}</p>
                        <p className="text-xs text-gray-500">
                          {curso.codigo_curso} • {curso.total_estudiantes} estudiantes
                        </p>
                      </div>
                    </div>
                    <Users className="w-5 h-5 text-purple-500 flex-shrink-0" />
                  </div>
                ))}
                {estadisticas.cursos_mas_activos.length === 0 && (
                  <p className="text-center text-gray-500 py-4">No hay cursos activos aún</p>
                )}
              </div>
            </div>
          </div>

          {/* Resumen de Actividad */}
          <div className="bg-white rounded-xl shadow-md p-6">
            <h3 className="text-lg font-bold text-gray-900 mb-4 flex items-center gap-2">
              <Clock className="w-5 h-5 text-blue-500" />
              Resumen de Actividad
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="p-4 bg-blue-50 rounded-lg border border-blue-200">
                <p className="text-sm font-medium text-blue-900 mb-1">
                  Estudiantes Activos
                </p>
                <p className="text-2xl font-bold text-blue-600">
                  {estadisticas.estudiantes_activos_mes}
                </p>
                <p className="text-xs text-blue-700 mt-1">
                  de {estadisticas.total_estudiantes} totales
                </p>
              </div>
              <div className="p-4 bg-green-50 rounded-lg border border-green-200">
                <p className="text-sm font-medium text-green-900 mb-1">
                  Misiones Completadas
                </p>
                <p className="text-2xl font-bold text-green-600">
                  {estadisticas.misiones_completadas_mes}
                </p>
                <p className="text-xs text-green-700 mt-1">Este mes</p>
              </div>
              <div className="p-4 bg-purple-50 rounded-lg border border-purple-200">
                <p className="text-sm font-medium text-purple-900 mb-1">
                  Promedio de Puntos
                </p>
                <p className="text-2xl font-bold text-purple-600">
                  {estadisticas.promedio_puntos_por_estudiante.toFixed(1)}
                </p>
                <p className="text-xs text-purple-700 mt-1">Por estudiante</p>
              </div>
            </div>
          </div>
        </>
      )}

      {/* Acciones Rápidas */}
      <div className="bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl p-6 border border-gray-200">
        <h3 className="text-lg font-bold text-gray-900 mb-4">Acciones Rápidas</h3>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <Link
            to="/admin/users"
            className="bg-white rounded-lg p-4 shadow-sm hover:shadow-md transition-shadow border border-gray-200 hover:border-blue-300"
          >
            <Users className="w-6 h-6 text-blue-600 mb-2" />
            <p className="font-medium text-gray-900">Gestionar Usuarios</p>
            <p className="text-xs text-gray-500 mt-1">Crear y editar usuarios</p>
          </Link>
          <Link
            to="/admin/cursos"
            className="bg-white rounded-lg p-4 shadow-sm hover:shadow-md transition-shadow border border-gray-200 hover:border-purple-300"
          >
            <BookOpen className="w-6 h-6 text-purple-600 mb-2" />
            <p className="font-medium text-gray-900">Gestionar Cursos</p>
            <p className="text-xs text-gray-500 mt-1">Crear y editar cursos</p>
          </Link>
          <Link
            to="/admin/reglas-gamificacion"
            className="bg-white rounded-lg p-4 shadow-sm hover:shadow-md transition-shadow border border-gray-200 hover:border-yellow-300"
          >
            <Settings className="w-6 h-6 text-yellow-600 mb-2" />
            <p className="font-medium text-gray-900">Reglas</p>
            <p className="text-xs text-gray-500 mt-1">Configurar reglas</p>
          </Link>
          <Link
            to="/admin/reportes"
            className="bg-white rounded-lg p-4 shadow-sm hover:shadow-md transition-shadow border border-gray-200 hover:border-green-300"
          >
            <BarChart3 className="w-6 h-6 text-green-600 mb-2" />
            <p className="font-medium text-gray-900">Ver Reportes</p>
            <p className="text-xs text-gray-500 mt-1">Estadísticas completas</p>
          </Link>
        </div>
      </div>
    </div>
  );
}
