import React, { useState } from 'react';
import type { MisionListResponse } from '../types';
import { Target, Edit2, Trash2, FileQuestion } from 'lucide-react';
import EditarMisionModal from './EditarMisionModal';
import CrearEvaluacionModal from './profesor/CrearEvaluacionModal';
import { apiService } from '../services/api';

interface Props {
  misiones: MisionListResponse[];
  onMisionUpdated: () => void;
}

const MisionList: React.FC<Props> = ({ misiones, onMisionUpdated }) => {
  const [misionToEdit, setMisionToEdit] = useState<MisionListResponse | null>(null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [misionParaEvaluacion, setMisionParaEvaluacion] = useState<MisionListResponse | null>(null);
  const [isEvaluacionModalOpen, setIsEvaluacionModalOpen] = useState(false);

  const handleEdit = (mision: MisionListResponse) => {
    setMisionToEdit(mision);
    setIsEditModalOpen(true);
  };

  const handleDelete = async (mision: MisionListResponse) => {
    if (!confirm(`¿Estás seguro de eliminar la misión "${mision.titulo}"?`)) {
      return;
    }

    try {
      await apiService.eliminarMision(mision.id);
      onMisionUpdated();
    } catch (error: any) {
      console.error('Error eliminando misión:', error);
      const errorMessage = error?.response?.data?.message || error?.message || 'Error al eliminar la misión';
      alert(errorMessage);
    }
  };

  const handleEditSuccess = () => {
    onMisionUpdated();
  };
  if (misiones.length === 0) {
    return (
      <div className="text-center py-12 bg-gray-50 rounded-lg border-2 border-dashed border-gray-300">
        <Target className="w-16 h-16 text-gray-400 mx-auto mb-4" />
        <p className="text-gray-600 text-lg font-medium">No hay misiones registradas</p>
        <p className="text-gray-500 text-sm mt-2">Crea tu primera misión para comenzar</p>
      </div>
    );
  }

  const getStatusColor = (activo: boolean) => {
    return activo
      ? 'bg-green-100 text-green-700'
      : 'bg-gray-100 text-gray-700';
  };

  const getStatusText = (activo: boolean) => {
    return activo ? 'Activa' : 'Finalizada';
  };

  const getCategoryIcon = (categoria: string) => {
    // Retorna un emoji según la categoría
    const icons: Record<string, string> = {
      LECTURA: '📖',
      EJERCICIO: '✏️',
      PROYECTO: '📁',
      QUIZ: '❓',
      DESAFIO: '🎯',
    };
    return icons[categoria] || '📝';
  };

  return (
    <div className="space-y-3">
      {misiones.map((mision) => (
        <div
          key={mision.id}
          className="bg-white rounded-lg border border-gray-200 hover:shadow-md transition-shadow"
        >
          <div className="p-6">
            <div className="flex items-start gap-4">
              {/* Icono de categoría */}
              <div className="shrink-0 w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center text-2xl">
                {getCategoryIcon(mision.categoria)}
              </div>

              {/* Contenido principal */}
              <div className="flex-1 min-w-0">
                <div className="flex items-start justify-between gap-4">
                  <div className="flex-1">
                    <h3 className="text-lg font-semibold text-gray-900 mb-1">
                      {mision.titulo}
                    </h3>
                    <div className="flex items-center gap-3 text-sm text-gray-600">
                      <span className="capitalize">{mision.categoria.toLowerCase()}</span>
                      <span
                        className={`px-2 py-0.5 rounded text-xs font-medium ${getStatusColor(
                          mision.activo
                        )}`}
                      >
                        {getStatusText(mision.activo)}
                      </span>
                    </div>
                  </div>

                  {/* Botones de acción */}
                  <div className="flex items-center gap-2">
                    {mision.categoria === "QUIZ" && (
                      <button
                        onClick={() => {
                          setMisionParaEvaluacion(mision);
                          setIsEvaluacionModalOpen(true);
                        }}
                        className="p-2 text-purple-600 hover:text-purple-700 hover:bg-purple-50 rounded-lg transition-colors"
                        title="Crear Evaluación"
                      >
                        <FileQuestion className="w-5 h-5" />
                      </button>
                    )}
                    <button
                      onClick={() => handleEdit(mision)}
                      className="p-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                      title="Editar"
                    >
                      <Edit2 className="w-5 h-5" />
                    </button>
                    <button
                      onClick={() => handleDelete(mision)}
                      className="p-2 text-gray-600 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                      title="Eliminar"
                    >
                      <Trash2 className="w-5 h-5" />
                    </button>
                  </div>
                </div>

                {/* Estadísticas */}
                <div className="mt-3 text-sm text-gray-600">
                  <span>
                    {mision.estudiantesCompletados}/{mision.totalEstudiantes} completadas
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      ))}

      {/* Modal de edición */}
      {misionToEdit && (
        <EditarMisionModal
          isOpen={isEditModalOpen}
          onClose={() => setIsEditModalOpen(false)}
          onSuccess={handleEditSuccess}
          mision={misionToEdit}
        />
      )}

      {/* Modal de crear evaluación */}
      {misionParaEvaluacion && (
        <CrearEvaluacionModal
          isOpen={isEvaluacionModalOpen}
          onClose={() => {
            setIsEvaluacionModalOpen(false);
            setMisionParaEvaluacion(null);
          }}
          onSuccess={() => {
            onMisionUpdated();
            setIsEvaluacionModalOpen(false);
            setMisionParaEvaluacion(null);
          }}
          misionOpcional={misionParaEvaluacion}
        />
      )}
    </div>
  );
};

export default MisionList;
