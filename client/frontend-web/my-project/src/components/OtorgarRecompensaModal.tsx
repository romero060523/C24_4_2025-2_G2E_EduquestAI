import React, { useState, useEffect } from "react";
import { X, Gift } from "lucide-react";
import { apiService } from "../services/api";
import type { OtorgarRecompensaRequest, Curso, EstudianteSimple } from "../types";

interface Props {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
  estudiante?: EstudianteSimple;
  cursoId?: string;
}

const OtorgarRecompensaModal: React.FC<Props> = ({
  isOpen,
  onClose,
  onSuccess,
  estudiante,
  cursoId,
}) => {
  const [cursos, setCursos] = useState<Curso[]>([]);
  const [estudiantes, setEstudiantes] = useState<EstudianteSimple[]>([]);
  const [loading, setLoading] = useState(false);
  const [loadingCursos, setLoadingCursos] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState<OtorgarRecompensaRequest>({
    estudianteId: estudiante?.id || "",
    cursoId: cursoId || "",
    puntos: 50,
    motivo: "",
    observaciones: "",
  });

  useEffect(() => {
    if (isOpen) {
      loadCursos();
      if (!estudiante) {
        loadEstudiantes();
      }
      if (estudiante) {
        setFormData((prev) => ({ ...prev, estudianteId: estudiante.id }));
      }
      if (cursoId) {
        setFormData((prev) => ({ ...prev, cursoId }));
      }
    } else {
      setError(null);
      setFormData({
        estudianteId: "",
        cursoId: "",
        puntos: 50,
        motivo: "",
        observaciones: "",
      });
    }
  }, [isOpen, estudiante, cursoId]);

  const loadCursos = async () => {
    setLoadingCursos(true);
    setError(null);
    try {
      const profesorId = localStorage.getItem("profesorId") || localStorage.getItem("userId") || "";
      if (!profesorId) {
        console.error("No se encontró el ID del profesor en localStorage");
        setError("No se pudo identificar al profesor. Por favor, inicia sesión nuevamente.");
        setLoadingCursos(false);
        return;
      }
      console.log("Cargando cursos para profesor:", profesorId);
      const cursosData = await apiService.listarCursosPorProfesor(profesorId);
      console.log("Cursos recibidos:", cursosData);
      setCursos(cursosData);
      if (cursosData.length > 0 && !cursoId) {
        setFormData((prev) => ({ ...prev, cursoId: cursosData[0].id }));
      }
    } catch (e: unknown) {
      console.error("Error cargando cursos:", e);
      const errorMessage = e instanceof Error ? e.message : "Error al cargar los cursos";
      setError(`Error al cargar cursos: ${errorMessage}`);
    } finally {
      setLoadingCursos(false);
    }
  };

  const loadEstudiantes = async () => {
    try {
      const estudiantesData = await apiService.listarTodosLosEstudiantes();
      setEstudiantes(estudiantesData);
    } catch (e: unknown) {
      console.error("Error cargando estudiantes:", e);
    }
  };

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === "puntos" ? parseInt(value) || 0 : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await apiService.otorgarRecompensaManual(formData);
      onSuccess();
      onClose();
    } catch (e: unknown) {
      const errorMessage =
        e instanceof Error ? e.message : "Error al otorgar la recompensa";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 backdrop-blur-sm flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <div className="flex items-center gap-3">
            <Gift className="w-6 h-6 text-yellow-500" />
            <h2 className="text-2xl font-bold text-gray-900">
              Otorgar Recompensa Manual
            </h2>
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
              {error}
            </div>
          )}

          {/* Curso */}
          <div>
            <label
              htmlFor="cursoId"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Curso <span className="text-red-500">*</span>
            </label>
            <select
              id="cursoId"
              name="cursoId"
              value={formData.cursoId}
              onChange={handleChange}
              required
              disabled={!!cursoId || loadingCursos}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
            >
              <option value="">
                {loadingCursos ? "Cargando cursos..." : cursos.length === 0 ? "No hay cursos disponibles" : "Selecciona un curso"}
              </option>
              {cursos.map((curso) => (
                <option key={curso.id} value={curso.id}>
                  {curso.nombre}
                </option>
              ))}
            </select>
            {!loadingCursos && cursos.length === 0 && (
              <p className="text-xs text-gray-500 mt-1">
                Si no ves cursos, asegúrate de que hay cursos creados en el sistema. Los cursos se crean desde el panel de administración.
              </p>
            )}
          </div>

          {/* Estudiante */}
          <div>
            <label
              htmlFor="estudianteId"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Estudiante <span className="text-red-500">*</span>
            </label>
            {estudiante ? (
              <div className="px-3 py-2 bg-gray-50 border border-gray-300 rounded-lg">
                <p className="text-gray-900 font-medium">
                  {estudiante.nombreCompleto}
                </p>
                <p className="text-sm text-gray-500">{estudiante.email}</p>
              </div>
            ) : (
              <select
                id="estudianteId"
                name="estudianteId"
                value={formData.estudianteId}
                onChange={handleChange}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Selecciona un estudiante</option>
                {estudiantes.map((est) => (
                  <option key={est.id} value={est.id}>
                    {est.nombreCompleto} ({est.email})
                  </option>
                ))}
              </select>
            )}
          </div>

          {/* Puntos */}
          <div>
            <label
              htmlFor="puntos"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Puntos <span className="text-red-500">*</span>
            </label>
            <input
              type="number"
              id="puntos"
              name="puntos"
              value={formData.puntos}
              onChange={handleChange}
              required
              min="1"
              max="1000"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Ej: 50"
            />
            <p className="text-xs text-gray-500 mt-1">
              Mínimo: 1 punto, Máximo: 1000 puntos
            </p>
          </div>

          {/* Motivo */}
          <div>
            <label
              htmlFor="motivo"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Motivo <span className="text-red-500">*</span>
            </label>
            <textarea
              id="motivo"
              name="motivo"
              value={formData.motivo}
              onChange={handleChange}
              required
              rows={3}
              maxLength={500}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Ej: Excelente participación en clase, trabajo destacado en el proyecto..."
            />
            <p className="text-xs text-gray-500 mt-1">
              {formData.motivo.length}/500 caracteres
            </p>
          </div>

          {/* Observaciones */}
          <div>
            <label
              htmlFor="observaciones"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Observaciones (opcional)
            </label>
            <textarea
              id="observaciones"
              name="observaciones"
              value={formData.observaciones}
              onChange={handleChange}
              rows={2}
              maxLength={255}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Notas adicionales sobre la recompensa..."
            />
            <p className="text-xs text-gray-500 mt-1">
              {formData.observaciones?.length || 0}/255 caracteres
            </p>
          </div>

          {/* Botones */}
          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 px-4 py-2 bg-yellow-500 text-white rounded-lg hover:bg-yellow-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
              <Gift className="w-4 h-4" />
              {loading ? "Otorgando..." : "Otorgar Recompensa"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default OtorgarRecompensaModal;

