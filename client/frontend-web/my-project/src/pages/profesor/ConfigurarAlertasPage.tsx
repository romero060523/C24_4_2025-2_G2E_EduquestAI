import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { apiService } from "../../services/api";
import type { ConfiguracionAlertaRequest, Curso } from "../../types";

const ConfigurarAlertasPage: React.FC = () => {
  const [cursos, setCursos] = useState<Curso[]>([]);
  const [cursoSeleccionado, setCursoSeleccionado] = useState<string>("");
  const [loading, setLoading] = useState(false);
  const [mensaje, setMensaje] = useState<{
    tipo: "success" | "error";
    texto: string;
  } | null>(null);

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<ConfiguracionAlertaRequest>();

  useEffect(() => {
    cargarCursos();
  }, []);

  useEffect(() => {
    if (cursoSeleccionado) {
      cargarConfiguracionExistente(cursoSeleccionado);
    }
  }, [cursoSeleccionado]);

  const cargarCursos = async () => {
    try {
      const profesorId = localStorage.getItem("profesorId");
      if (!profesorId) return;

      const data = await apiService.listarCursosPorProfesor(profesorId);
      setCursos(data);
    } catch (error) {
      console.error("Error cargando cursos:", error);
    }
  };

  const cargarConfiguracionExistente = async (cursoId: string) => {
    try {
      const config = await apiService.obtenerConfiguracionAlerta(cursoId);
      if (config) {
        setValue("diasInactividad", config.diasInactividad || undefined);
        setValue(
          "porcentajeCompletitudMinimo",
          config.porcentajeCompletitudMinimo || undefined
        );
        setValue("puntosDebajoPromedio", config.puntosDebajoPromedio || false);
        setValue(
          "misionesPendientesMinimo",
          config.misionesPendientesMinimo || undefined
        );
      }
    } catch (error) {
      console.error("Error cargando configuración:", error);
    }
  };

  const onSubmit = async (
    data: Omit<ConfiguracionAlertaRequest, "cursoId">
  ) => {
    if (!cursoSeleccionado) {
      setMensaje({ tipo: "error", texto: "Selecciona un curso" });
      return;
    }

    setLoading(true);
    setMensaje(null);

    try {
      await apiService.configurarAlertas({
        ...data,
        cursoId: cursoSeleccionado,
      });

      setMensaje({
        tipo: "success",
        texto: "Configuración guardada exitosamente",
      });
    } catch (error: any) {
      setMensaje({
        tipo: "error",
        texto: error.message || "Error al guardar configuración",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-6">
      <h1 className="text-2xl font-bold mb-6">Configurar Alertas Tempranas</h1>

      <p className="text-gray-600 mb-6">
        Define criterios para detectar estudiantes en riesgo y recibir alertas
        automáticas.
      </p>

      {mensaje && (
        <div
          className={`mb-4 p-4 rounded-lg ${
            mensaje.tipo === "success"
              ? "bg-green-50 text-green-700"
              : "bg-red-50 text-red-700"
          }`}
        >
          {mensaje.texto}
        </div>
      )}

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        {/* Selector de Curso */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Curso
          </label>
          <select
            value={cursoSeleccionado}
            onChange={(e) => setCursoSeleccionado(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2"
            required
          >
            <option value="">Selecciona un curso</option>
            {cursos.map((curso) => (
              <option key={curso.id} value={curso.id}>
                {curso.nombre}
              </option>
            ))}
          </select>
        </div>

        {/* Criterios de Alerta */}
        <div className="bg-white border border-gray-200 rounded-lg p-6 space-y-4">
          <h2 className="text-lg font-semibold">Criterios de Alerta</h2>

          {/* Días de Inactividad */}
          <div>
            <label className="flex items-center">
              <input
                type="checkbox"
                onChange={(e) => {
                  if (!e.target.checked) setValue("diasInactividad", undefined);
                }}
                className="mr-2"
              />
              <span className="text-sm font-medium">
                Alerta por inactividad
              </span>
            </label>
            <div className="mt-2 ml-6">
              <label className="block text-sm text-gray-600 mb-1">
                Días sin actividad
              </label>
              <input
                type="number"
                {...register("diasInactividad", {
                  min: { value: 1, message: "Mínimo 1 día" },
                  valueAsNumber: true,
                })}
                placeholder="Ej: 7"
                className="w-32 border border-gray-300 rounded px-3 py-2"
              />
              {errors.diasInactividad && (
                <p className="text-red-500 text-xs mt-1">
                  {errors.diasInactividad.message}
                </p>
              )}
            </div>
          </div>

          {/* Porcentaje de Completitud */}
          <div>
            <label className="flex items-center">
              <input
                type="checkbox"
                onChange={(e) => {
                  if (!e.target.checked)
                    setValue("porcentajeCompletitudMinimo", undefined);
                }}
                className="mr-2"
              />
              <span className="text-sm font-medium">
                Alerta por bajo % de misiones completadas
              </span>
            </label>
            <div className="mt-2 ml-6">
              <label className="block text-sm text-gray-600 mb-1">
                Porcentaje mínimo (%)
              </label>
              <input
                type="number"
                {...register("porcentajeCompletitudMinimo", {
                  min: { value: 0, message: "Mínimo 0%" },
                  max: { value: 100, message: "Máximo 100%" },
                  valueAsNumber: true,
                })}
                placeholder="Ej: 50"
                className="w-32 border border-gray-300 rounded px-3 py-2"
              />
              {errors.porcentajeCompletitudMinimo && (
                <p className="text-red-500 text-xs mt-1">
                  {errors.porcentajeCompletitudMinimo.message}
                </p>
              )}
            </div>
          </div>

          {/* Puntos Debajo del Promedio */}
          <div>
            <label className="flex items-center">
              <input
                type="checkbox"
                {...register("puntosDebajoPromedio")}
                className="mr-2"
              />
              <span className="text-sm font-medium">
                Alerta si está por debajo del promedio del grupo
              </span>
            </label>
          </div>

          {/* Misiones Pendientes */}
          <div>
            <label className="flex items-center">
              <input
                type="checkbox"
                onChange={(e) => {
                  if (!e.target.checked)
                    setValue("misionesPendientesMinimo", undefined);
                }}
                className="mr-2"
              />
              <span className="text-sm font-medium">
                Alerta por misiones sin completar
              </span>
            </label>
            <div className="mt-2 ml-6">
              <label className="block text-sm text-gray-600 mb-1">
                Número mínimo de misiones pendientes
              </label>
              <input
                type="number"
                {...register("misionesPendientesMinimo", {
                  min: { value: 1, message: "Mínimo 1 misión" },
                  valueAsNumber: true,
                })}
                placeholder="Ej: 3"
                className="w-32 border border-gray-300 rounded px-3 py-2"
              />
              {errors.misionesPendientesMinimo && (
                <p className="text-red-500 text-xs mt-1">
                  {errors.misionesPendientesMinimo.message}
                </p>
              )}
            </div>
          </div>
        </div>

        {/* Botones */}
        <div className="flex gap-4">
          <button
            type="submit"
            disabled={loading}
            className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-6 py-2 rounded-lg disabled:opacity-50"
          >
            {loading ? "Guardando..." : "Guardar Configuración"}
          </button>
          <button
            type="button"
            onClick={() => window.history.back()}
            className="bg-gray-200 hover:bg-gray-300 text-gray-700 font-semibold px-6 py-2 rounded-lg"
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  );
};

export default ConfigurarAlertasPage;
