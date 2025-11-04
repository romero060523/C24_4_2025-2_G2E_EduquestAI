import { useEffect, useState } from 'react';
import { apiService } from '../../services/api';
import { useAuth } from '../../hooks/useAuth';
import type { MisionEstudianteResponse, CompletarMisionRequest } from '../../types';

const MisionesEstudiante = () => {
  const { usuario } = useAuth();
  const [misiones, setMisiones] = useState<MisionEstudianteResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedMision, setSelectedMision] = useState<MisionEstudianteResponse | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState<CompletarMisionRequest>({
    contenidoEntrega: '',
    archivoUrl: '',
    comentariosEstudiante: '',
  });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    loadMisiones();
  }, []);

  const loadMisiones = async () => {
    setLoading(true);
    setError(null);
    try {
      const estudianteId = localStorage.getItem('estudianteId') || usuario?.id || '';
      if (!estudianteId) {
        throw new Error('No se encontró el ID del estudiante');
      }
      const result = await apiService.listarMisionesPorEstudiante(estudianteId);
      setMisiones(result);
    } catch (e: unknown) {
      const errorMessage = e instanceof Error ? e.message : 'Error cargando misiones';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleCompletarClick = (mision: MisionEstudianteResponse) => {
    if (mision.completada) {
      alert('Esta misión ya fue completada');
      return;
    }
    setSelectedMision(mision);
    setFormData({
      contenidoEntrega: '',
      archivoUrl: '',
      comentariosEstudiante: '',
    });
    setShowModal(true);
  };

  const handleSubmit = async (e: { preventDefault: () => void }) => {
    e.preventDefault();
    if (!selectedMision) return;

    setSubmitting(true);
    try {
      await apiService.completarMision(selectedMision.id, formData);
      setShowModal(false);
      setSelectedMision(null);
      await loadMisiones();
      alert(`¡Felicidades! Has completado la misión y ganado ${selectedMision.puntosRecompensa} puntos.`);
    } catch (e: unknown) {
      const errorMessage = e instanceof Error ? e.message : 'Error al completar la misión';
      alert(errorMessage);
    } finally {
      setSubmitting(false);
    }
  };

  const getEstadoBadge = (mision: MisionEstudianteResponse) => {
    if (mision.completada) {
      return (
        <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
          <span>✓</span>
          Completada
        </span>
      );
    }
    if (mision.porcentajeCompletado > 0) {
      return (
        <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
          <span>⏱</span>
          En progreso
        </span>
      );
    }
    return (
      <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
        <span>🎯</span>
        Pendiente
      </span>
    );
  };

  const getDificultadColor = (dificultad: string) => {
    switch (dificultad) {
      case 'FACIL':
        return 'bg-green-100 text-green-800';
      case 'MEDIO':
        return 'bg-yellow-100 text-yellow-800';
      case 'DIFICIL':
        return 'bg-orange-100 text-orange-800';
      case 'EXPERTO':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
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
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Mis Misiones Asignadas</h1>
        <p className="text-gray-500 mt-1">
          Misiones creadas por tus profesores. Completa misiones para ganar puntos y experiencia
        </p>
      </div>

      {misiones.length === 0 ? (
        <div className="bg-white rounded-lg p-12 text-center border border-gray-200">
          <span className="text-6xl text-gray-400 mb-4 block">🎯</span>
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            No tienes misiones asignadas
          </h3>
          <p className="text-gray-500">
            Tus profesores crearán misiones para ti. ¡Revisa más tarde!
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {misiones.map((mision) => (
            <div
              key={mision.id}
              className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow"
            >
              <div className="flex items-start justify-between mb-4">
                <div className="flex-1">
                  <h3 className="text-lg font-semibold text-gray-900 mb-1">
                    {mision.titulo}
                  </h3>
                  <p className="text-sm text-gray-500 mb-2">
                    📚 {mision.cursoNombre} • Asignada por tu profesor
                  </p>
                  {getEstadoBadge(mision)}
                </div>
              </div>

              <p className="text-sm text-gray-600 mb-4 line-clamp-3">
                {mision.descripcion}
              </p>

              <div className="flex items-center gap-4 mb-4 text-sm">
                <span
                  className={`px-2 py-1 rounded text-xs font-medium ${getDificultadColor(
                    mision.dificultad
                  )}`}
                >
                  {mision.dificultad}
                </span>
                <span className="flex items-center gap-1 text-gray-600">
                  <span className="text-yellow-500">⭐</span>
                  {mision.puntosRecompensa} pts
                </span>
              </div>

              {mision.completada && mision.puntosObtenidos > 0 && (
                <div className="mb-4 p-2 bg-green-50 rounded text-sm text-green-800">
                  <strong>Puntos obtenidos: {mision.puntosObtenidos}</strong>
                </div>
              )}

              <div className="mb-4">
                <div className="flex items-center justify-between text-xs text-gray-500 mb-1">
                  <span>Progreso</span>
                  <span>{mision.porcentajeCompletado}%</span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2">
                  <div
                    className="bg-blue-600 h-2 rounded-full transition-all"
                    style={{ width: `${mision.porcentajeCompletado}%` }}
                  />
                </div>
              </div>

              <div className="text-xs text-gray-500 mb-4">
                <div>Fecha límite: {formatDate(mision.fechaLimite)}</div>
                {mision.fechaCompletado && (
                  <div>Completada: {formatDate(mision.fechaCompletado)}</div>
                )}
              </div>

              {!mision.completada && (
                <button
                  onClick={() => handleCompletarClick(mision)}
                  className="w-full bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors"
                >
                  Completar Misión
                </button>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Modal para completar misión */}
      {showModal && selectedMision && (
        <div className="fixed inset-0 backdrop-blur-sm bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-2xl font-bold text-gray-900">
                  Completar Misión: {selectedMision.titulo}
                </h2>
                <button
                  onClick={() => setShowModal(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  ✕
                </button>
              </div>

              <div className="mb-6 p-4 bg-blue-50 rounded-lg">
                <p className="text-sm text-blue-800 mb-2">
                  <strong>Puntos a ganar:</strong> {selectedMision.puntosRecompensa}
                </p>
                <p className="text-sm text-gray-600">{selectedMision.descripcion}</p>
              </div>

              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Contenido de la entrega <span className="text-red-500">*</span>
                  </label>
                  <textarea
                    value={formData.contenidoEntrega}
                    onChange={(e) =>
                      setFormData({ ...formData, contenidoEntrega: e.target.value })
                    }
                    required
                    rows={6}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Describe tu solución, respuestas, o trabajo realizado..."
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    URL del archivo (opcional)
                  </label>
                  <input
                    type="url"
                    value={formData.archivoUrl || ''}
                    onChange={(e) =>
                      setFormData({ ...formData, archivoUrl: e.target.value })
                    }
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="https://..."
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Comentarios adicionales (opcional)
                  </label>
                  <textarea
                    value={formData.comentariosEstudiante || ''}
                    onChange={(e) =>
                      setFormData({ ...formData, comentariosEstudiante: e.target.value })
                    }
                    rows={3}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Comentarios, observaciones, etc."
                  />
                </div>

                <div className="flex gap-3 pt-4">
                  <button
                    type="button"
                    onClick={() => setShowModal(false)}
                    className="flex-1 px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 font-medium"
                    disabled={submitting}
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    disabled={submitting}
                    className="flex-1 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium disabled:opacity-50"
                  >
                    {submitting ? 'Enviando...' : 'Completar y Enviar'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default MisionesEstudiante;
