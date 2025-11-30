import React, { useState } from "react";
import { X, Sparkles, Loader2 } from "lucide-react";
import { apiService } from "../../services/api";
import type { RetroalimentacionResponse } from "../../types";

interface Props {
  isOpen: boolean;
  onClose: () => void;
  estudianteId: string;
  estudianteNombre: string;
  evaluacionId: string;
  evaluacionTitulo: string;
}

const RetroalimentacionModal: React.FC<Props> = ({
  isOpen,
  onClose,
  estudianteId,
  estudianteNombre,
  evaluacionId,
  evaluacionTitulo,
}) => {
  const [retroalimentacion, setRetroalimentacion] = useState<RetroalimentacionResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const generarRetroalimentacion = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await apiService.generarRetroalimentacion({
        estudianteId,
        evaluacionId,
      });
      setRetroalimentacion(response);
    } catch (e: unknown) {
      const errorMessage = e instanceof Error ? e.message : "Error al generar la retroalimentación";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 backdrop-blur-sm bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200 sticky top-0 bg-white z-10">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">Retroalimentación Automática</h2>
            <p className="text-sm text-gray-600 mt-1">
              {estudianteNombre} - {evaluacionTitulo}
            </p>
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6 space-y-4">
          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
              {error}
            </div>
          )}

          {!retroalimentacion && !loading && (
            <div className="text-center py-8">
              <Sparkles className="w-16 h-16 text-blue-500 mx-auto mb-4" />
              <p className="text-gray-600 mb-6">
                Genera retroalimentación personalizada para este estudiante basada en su desempeño en la evaluación.
              </p>
              <button
                onClick={generarRetroalimentacion}
                className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center gap-2 mx-auto"
              >
                <Sparkles className="w-5 h-5" />
                Generar Retroalimentación con IA
              </button>
            </div>
          )}

          {loading && (
            <div className="text-center py-12">
              <Loader2 className="w-12 h-12 text-blue-500 mx-auto mb-4 animate-spin" />
              <p className="text-gray-600">Generando retroalimentación personalizada...</p>
            </div>
          )}

          {retroalimentacion && (
            <div className="space-y-4">
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                <h3 className="font-semibold text-blue-900 mb-2">Retroalimentación Generada</h3>
                <p className="text-sm text-blue-700">
                  Generada el {new Date(retroalimentacion.fechaGeneracion).toLocaleString("es-ES")}
                </p>
              </div>

              <div className="bg-gray-50 rounded-lg p-6 border border-gray-200">
                <div className="prose max-w-none">
                  <p className="text-gray-800 whitespace-pre-wrap leading-relaxed">
                    {retroalimentacion.retroalimentacion}
                  </p>
                </div>
              </div>

              <div className="flex gap-3 pt-4">
                <button
                  onClick={generarRetroalimentacion}
                  disabled={loading}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                >
                  <Sparkles className="w-4 h-4" />
                  Regenerar
                </button>
                <button
                  onClick={onClose}
                  className="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
                >
                  Cerrar
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default RetroalimentacionModal;

