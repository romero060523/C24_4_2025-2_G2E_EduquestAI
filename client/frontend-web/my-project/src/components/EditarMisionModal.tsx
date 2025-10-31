import React, { useState, useEffect } from "react";
import { X } from "lucide-react";
import { apiService } from "../services/api";
import type { ActualizarMisionDTO, MisionListResponse } from "../types";

interface Props {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
  mision: MisionListResponse;
}

const EditarMisionModal: React.FC<Props> = ({
  isOpen,
  onClose,
  onSuccess,
  mision,
}) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState<ActualizarMisionDTO>({
    titulo: mision.titulo,
    categoria: mision.categoria,
    dificultad: mision.dificultad,
    puntosRecompensa: mision.puntosRecompensa,
    activo: mision.activo,
  });

  useEffect(() => {
    if (isOpen) {
      // Resetear formulario con datos de la misión
      setFormData({
        titulo: mision.titulo,
        categoria: mision.categoria,
        dificultad: mision.dificultad,
        puntosRecompensa: mision.puntosRecompensa,
        activo: mision.activo,
      });
    } else {
      setError(null);
    }
  }, [isOpen, mision]);

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
  ) => {
    const { name, value, type } = e.target;

    if (type === "checkbox") {
      const checked = (e.target as HTMLInputElement).checked;
      setFormData((prev) => ({ ...prev, [name]: checked }));
    } else {
      setFormData((prev) => ({
        ...prev,
        [name]:
          name === "puntosRecompensa" || name === "experienciaRecompensa"
            ? parseInt(value) || 0
            : value,
      }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await apiService.actualizarMision(mision.id, formData);
      onSuccess();
      onClose();
    } catch (e: unknown) {
      const errorMessage =
        e instanceof Error ? e.message : "Error al actualizar la misión";
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
          <h2 className="text-2xl font-bold text-gray-900">Editar Misión</h2>
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

          {/* Título */}
          <div>
            <label
              htmlFor="titulo"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Título <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              id="titulo"
              name="titulo"
              value={formData.titulo || ""}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Descripción */}
          <div>
            <label
              htmlFor="descripcion"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Descripción
            </label>
            <textarea
              id="descripcion"
              name="descripcion"
              value={formData.descripcion || ""}
              onChange={handleChange}
              rows={3}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Categoría y Dificultad */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label
                htmlFor="categoria"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Categoría
              </label>
              <select
                id="categoria"
                name="categoria"
                value={formData.categoria || ""}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="LECTURA">Lectura</option>
                <option value="EJERCICIO">Ejercicio</option>
                <option value="PROYECTO">Proyecto</option>
                <option value="QUIZ">Quiz</option>
                <option value="DESAFIO">Desafío</option>
              </select>
            </div>

            <div>
              <label
                htmlFor="dificultad"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Dificultad
              </label>
              <select
                id="dificultad"
                name="dificultad"
                value={formData.dificultad || ""}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="FACIL">Fácil</option>
                <option value="MEDIO">Medio</option>
                <option value="DIFICIL">Difícil</option>
                <option value="EXPERTO">Experto</option>
              </select>
            </div>
          </div>

          {/* Puntos y Experiencia */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label
                htmlFor="puntosRecompensa"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Puntos
              </label>
              <input
                type="number"
                id="puntosRecompensa"
                name="puntosRecompensa"
                value={formData.puntosRecompensa || 0}
                onChange={handleChange}
                min="0"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label
                htmlFor="experienciaRecompensa"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Experiencia
              </label>
              <input
                type="number"
                id="experienciaRecompensa"
                name="experienciaRecompensa"
                value={formData.experienciaRecompensa || 0}
                onChange={handleChange}
                min="0"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          {/* Estado Activo */}
          <div className="flex items-center gap-2">
            <input
              type="checkbox"
              id="activo"
              name="activo"
              checked={formData.activo || false}
              onChange={handleChange}
              className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <label
              htmlFor="activo"
              className="text-sm font-medium text-gray-700"
            >
              Misión activa
            </label>
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
              className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? "Guardando..." : "Guardar Cambios"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditarMisionModal;
