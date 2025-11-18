import React, { useEffect, useState } from 'react';
import { apiService } from '../services/api';
import type { MisionListResponse, EvaluacionGamificadaResponse } from '../types';
import MisionList from '../components/MisionList';
import CrearMisionModal from '../components/CrearMisionModal';
import CrearEvaluacionModal from '../components/profesor/CrearEvaluacionModal';
import { Plus, FileQuestion, Trash2, Clock, Award } from 'lucide-react';

const MisionesPage: React.FC = () => {
  const [misiones, setMisiones] = useState<MisionListResponse[]>([]);
  const [evaluaciones, setEvaluaciones] = useState<EvaluacionGamificadaResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isEvaluacionModalOpen, setIsEvaluacionModalOpen] = useState(false);
  const [misionParaEvaluacion, setMisionParaEvaluacion] = useState<MisionListResponse | null>(null);
  const [showSeleccionarMision, setShowSeleccionarMision] = useState(false);

  useEffect(() => {
    loadMisiones();
    loadEvaluaciones();
  }, []);

  const loadMisiones = async () => {
    setLoading(true);
    setError(null);
    try {
      const profesorId = localStorage.getItem('profesorId') || '709be72f-3f2c-4a51-8f2f-815fece92bda';
      const result = await apiService.listarMisionesPorProfesor(profesorId);
      setMisiones(result);
    } catch (e: unknown) {
      const errorMessage = e instanceof Error ? e.message : 'Error cargando misiones';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const loadEvaluaciones = async () => {
    try {
      const result = await apiService.listarEvaluacionesProfesor();
      setEvaluaciones(result);
      console.log('✅ Evaluaciones cargadas:', result);
    } catch (e: unknown) {
      console.error('❌ Error cargando evaluaciones:', e);
    }
  };

  const handleNuevaMision = () => {
    setIsModalOpen(true);
  };

  const handleMisionCreated = () => {
    loadMisiones();
  };

  const handleNuevaEvaluacion = () => {
    // Ahora las evaluaciones se crean directamente por curso, no necesitan misión
    setIsEvaluacionModalOpen(true);
  };

  const handleSeleccionarMision = (mision: MisionListResponse) => {
    setMisionParaEvaluacion(mision);
    setShowSeleccionarMision(false);
    setIsEvaluacionModalOpen(true);
  };

  const handleEvaluacionCreada = () => {
    setIsEvaluacionModalOpen(false);
    setMisionParaEvaluacion(null);
    loadMisiones();
    loadEvaluaciones();
  };

  const handleEliminarEvaluacion = async (evaluacionId: string) => {
    if (!confirm('¿Estás seguro de eliminar esta evaluación?')) return;
    
    try {
      await apiService.eliminarEvaluacion(evaluacionId);
      loadEvaluaciones();
      alert('Evaluación eliminada exitosamente');
    } catch (error) {
      console.error('Error eliminando evaluación:', error);
      alert('Error al eliminar la evaluación');
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-gray-600">Cargando misiones...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
        Error: {error}
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-start justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Misiones</h1>
          <p className="text-gray-500 mt-1">Crea y gestiona misiones gamificadas</p>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={handleNuevaEvaluacion}
            className="flex items-center gap-2 bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-lg font-medium transition-colors"
          >
            <FileQuestion className="w-5 h-5" />
            Nueva Evaluación
          </button>
          <button
            onClick={handleNuevaMision}
            className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg font-medium transition-colors"
          >
            <Plus className="w-5 h-5" />
            Nueva Misión
          </button>
        </div>
      </div>

      {/* Lista de Evaluaciones Gamificadas */}
      {evaluaciones.length > 0 && (
        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center gap-2">
            <FileQuestion className="w-6 h-6 text-purple-600" />
            Evaluaciones Creadas ({evaluaciones.length})
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {evaluaciones.map((evaluacion) => (
              <div
                key={evaluacion.id}
                className="border-2 border-purple-200 rounded-lg p-4 hover:shadow-lg transition-shadow bg-gradient-to-br from-purple-50 to-white"
              >
                <div className="flex justify-between items-start mb-3">
                  <h3 className="font-bold text-gray-900 text-lg flex-1">{evaluacion.titulo}</h3>
                  <button
                    onClick={() => handleEliminarEvaluacion(evaluacion.id)}
                    className="text-red-500 hover:text-red-700 transition-colors"
                    title="Eliminar evaluación"
                  >
                    <Trash2 className="w-5 h-5" />
                  </button>
                </div>
                
                <p className="text-sm text-gray-600 mb-3 line-clamp-2">{evaluacion.descripcion}</p>
                
                <div className="space-y-2">
                  <div className="flex items-center gap-2 text-sm">
                    <Award className="w-4 h-4 text-purple-600" />
                    <span className="font-medium text-gray-700">Curso:</span>
                    <span className="text-gray-600">{evaluacion.cursoNombre}</span>
                  </div>
                  
                  <div className="flex items-center gap-2 text-sm">
                    <Clock className="w-4 h-4 text-purple-600" />
                    <span className="font-medium text-gray-700">{evaluacion.tiempoLimiteMinutos} min</span>
                    <span className="text-gray-500">•</span>
                    <span className="text-gray-600">{evaluacion.preguntas?.length || 0} preguntas</span>
                  </div>
                  
                  <div className="flex items-center gap-2">
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                      evaluacion.activo ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-700'
                    }`}>
                      {evaluacion.activo ? '✓ Activa' : '✗ Inactiva'}
                    </span>
                    <span className="px-2 py-1 rounded-full text-xs font-medium bg-purple-100 text-purple-700">
                      {evaluacion.intentosPermitidos} intentos
                    </span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Lista de misiones */}
      <MisionList misiones={misiones} onMisionUpdated={loadMisiones} />

      {/* Modal de creación de misión */}
      <CrearMisionModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSuccess={handleMisionCreated}
      />

      {/* Modal para seleccionar misión QUIZ */}
      {showSeleccionarMision && (
        <div className="fixed inset-0 backdrop-blur-sm bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[80vh] overflow-y-auto">
            <div className="flex items-center justify-between p-6 border-b border-gray-200 sticky top-0 bg-white z-10">
              <div>
                <h2 className="text-2xl font-bold text-gray-900">Seleccionar Misión QUIZ</h2>
                <p className="text-sm text-gray-600 mt-1">Elige una misión tipo QUIZ para crear la evaluación</p>
              </div>
              <button
                onClick={() => setShowSeleccionarMision(false)}
                className="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <div className="p-6">
              {misiones.filter(m => m.categoria === 'QUIZ').length === 0 ? (
                <div className="text-center py-12">
                  <FileQuestion className="w-16 h-16 text-gray-400 mx-auto mb-4" />
                  <p className="text-gray-600 text-lg font-medium">No hay misiones tipo QUIZ</p>
                  <p className="text-gray-500 text-sm mt-2">Crea una misión tipo QUIZ primero</p>
                </div>
              ) : (
                <div className="space-y-3">
                  {misiones
                    .filter(m => m.categoria === 'QUIZ')
                    .map((mision) => (
                      <button
                        key={mision.id}
                        onClick={() => handleSeleccionarMision(mision)}
                        className="w-full text-left p-4 border-2 border-gray-200 rounded-lg hover:border-purple-500 hover:bg-purple-50 transition-all"
                      >
                        <div className="flex items-center justify-between">
                          <div className="flex-1">
                            <h3 className="font-semibold text-gray-900">{mision.titulo}</h3>
                            <p className="text-sm text-gray-600 mt-1">{mision.cursoNombre}</p>
                            <div className="flex items-center gap-2 mt-2">
                              <span className="px-2 py-1 bg-purple-100 text-purple-700 rounded text-xs font-medium">
                                QUIZ
                              </span>
                              <span className={`px-2 py-1 rounded text-xs font-medium ${
                                mision.activo ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-700'
                              }`}>
                                {mision.activo ? 'Activa' : 'Finalizada'}
                              </span>
                            </div>
                          </div>
                          <FileQuestion className="w-6 h-6 text-purple-600" />
                        </div>
                      </button>
                    ))}
                </div>
              )}
            </div>
          </div>
        </div>
      )}

             {/* Modal de creación de evaluación */}
             <CrearEvaluacionModal
               isOpen={isEvaluacionModalOpen}
               onClose={() => {
                 setIsEvaluacionModalOpen(false);
                 setMisionParaEvaluacion(null);
               }}
               onSuccess={handleEvaluacionCreada}
               misionOpcional={misionParaEvaluacion}
             />
    </div>
  );
};

export default MisionesPage;
