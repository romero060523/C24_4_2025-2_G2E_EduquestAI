import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { apiService } from "../../services/api";
import type { AlertaRendimiento, Curso } from "../../types";
import AlertaCard from "../../components/profesor/AlertaCard";

const AlertasPage: React.FC = () => {
  const [cursos, setCursos] = useState<Curso[]>([]);
  const [cursoSeleccionado, setCursoSeleccionado] = useState<string>("");
  const [alertas, setAlertas] = useState<AlertaRendimiento[]>([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    cargarCursos();
  }, []);

  useEffect(() => {
    if (cursoSeleccionado) {
      cargarAlertas(cursoSeleccionado);
    }
  }, [cursoSeleccionado]);

  const cargarCursos = async () => {
    try {
      const profesorId = localStorage.getItem("profesorId");
      if (!profesorId) return;

      const data = await apiService.listarCursosPorProfesor(profesorId);
      setCursos(data);

      // Seleccionar el primer curso por defecto
      if (data.length > 0) {
        setCursoSeleccionado(data[0].id);
      }
    } catch (error) {
      console.error("Error cargando cursos:", error);
    }
  };

  const cargarAlertas = async (cursoId: string) => {
    setLoading(true);
    try {
      const data = await apiService.obtenerAlertasCurso(cursoId);
      setAlertas(data);
    } catch (error) {
      console.error("Error cargando alertas:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleResolverAlerta = async (alertaId: string) => {
    try {
      await apiService.resolverAlerta(alertaId);
      // Recargar alertas
      if (cursoSeleccionado) {
        cargarAlertas(cursoSeleccionado);
      }
    } catch (error) {
      console.error("Error resolviendo alerta:", error);
    }
  };

  const handleIgnorarAlerta = async (alertaId: string) => {
    try {
      await apiService.ignorarAlerta(alertaId);
      if (cursoSeleccionado) {
        cargarAlertas(cursoSeleccionado);
      }
    } catch (error) {
      console.error("Error ignorando alerta:", error);
    }
  };

  const handleEvaluarManualmente = async () => {
    if (!cursoSeleccionado) return;

    setLoading(true);
    try {
      await apiService.evaluarCursoManualmente(cursoSeleccionado);
      // Esperar un momento y recargar
      setTimeout(() => cargarAlertas(cursoSeleccionado), 1000);
    } catch (error) {
      console.error("Error evaluando curso:", error);
    } finally {
      setLoading(false);
    }
  };

  const alertasPorTipo = {
    INACTIVIDAD: alertas.filter((a) => a.tipo === "INACTIVIDAD").length,
    BAJO_RENDIMIENTO: alertas.filter((a) => a.tipo === "BAJO_RENDIMIENTO")
      .length,
    MISIONES_PENDIENTES: alertas.filter((a) => a.tipo === "MISIONES_PENDIENTES")
      .length,
    DEBAJO_PROMEDIO: alertas.filter((a) => a.tipo === "DEBAJO_PROMEDIO").length,
  };

  return (
    <div className="max-w-7xl mx-auto p-6">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold">Alertas Tempranas</h1>
          <p className="text-gray-600">Estudiantes que requieren atención</p>
        </div>
        <div className="flex gap-3">
          <button
            onClick={handleEvaluarManualmente}
            disabled={!cursoSeleccionado || loading}
            className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg font-medium disabled:opacity-50"
          >
            🔄 Evaluar Ahora
          </button>
          <button
            onClick={() => navigate("/profesor/alertas/configurar")}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium"
          >
            ⚙️ Configurar
          </button>
        </div>
      </div>

      {/* Selector de Curso */}
      <div className="mb-6">
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Curso
        </label>
        <select
          value={cursoSeleccionado}
          onChange={(e) => setCursoSeleccionado(e.target.value)}
          className="w-full max-w-md border border-gray-300 rounded-lg px-3 py-2"
        >
          <option value="">Selecciona un curso</option>
          {cursos.map((curso) => (
            <option key={curso.id} value={curso.id}>
              {curso.nombre}
            </option>
          ))}
        </select>
      </div>

      {/* Estadísticas */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-red-600 font-medium">Inactividad</p>
              <p className="text-2xl font-bold text-red-700">
                {alertasPorTipo.INACTIVIDAD}
              </p>
            </div>
            <span className="text-3xl">😴</span>
          </div>
        </div>

        <div className="bg-orange-50 border border-orange-200 rounded-lg p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-orange-600 font-medium">
                Bajo Rendimiento
              </p>
              <p className="text-2xl font-bold text-orange-700">
                {alertasPorTipo.BAJO_RENDIMIENTO}
              </p>
            </div>
            <span className="text-3xl">📉</span>
          </div>
        </div>

        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-yellow-600 font-medium">
                Misiones Pendientes
              </p>
              <p className="text-2xl font-bold text-yellow-700">
                {alertasPorTipo.MISIONES_PENDIENTES}
              </p>
            </div>
            <span className="text-3xl">📝</span>
          </div>
        </div>

        <div className="bg-purple-50 border border-purple-200 rounded-lg p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-purple-600 font-medium">
                Debajo Promedio
              </p>
              <p className="text-2xl font-bold text-purple-700">
                {alertasPorTipo.DEBAJO_PROMEDIO}
              </p>
            </div>
            <span className="text-3xl">📊</span>
          </div>
        </div>
      </div>

      {/* Lista de Alertas */}
      {loading ? (
        <div className="text-center py-12">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p className="mt-4 text-gray-600">Cargando alertas...</p>
        </div>
      ) : alertas.length === 0 ? (
        <div className="text-center py-12 bg-gray-50 rounded-lg">
          <span className="text-6xl">✅</span>
          <p className="mt-4 text-lg font-medium text-gray-700">
            No hay alertas activas
          </p>
          <p className="text-gray-500">Todos los estudiantes están al día</p>
        </div>
      ) : (
        <div className="space-y-4">
          {alertas.map((alerta) => (
            <AlertaCard
              key={alerta.id}
              alerta={alerta}
              onResolver={handleResolverAlerta}
              onIgnorar={handleIgnorarAlerta}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default AlertasPage;
