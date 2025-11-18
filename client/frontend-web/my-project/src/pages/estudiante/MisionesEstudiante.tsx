import { useEffect, useState } from "react";
import { apiService } from "../../services/api";
import { useAuth } from "../../hooks/useAuth";
import type {
  MisionEstudianteResponse,
  CompletarMisionRequest,
  EvaluacionGamificadaResponse,
  ResultadoEvaluacionResponse,
} from "../../types";
import { getTemaConfig, getTemaImage } from "../../utils/temaUtils";
import TomarEvaluacionModal from "../../components/estudiante/TomarEvaluacionModal";

const MisionesEstudiante = () => {
  const { usuario } = useAuth();
  const [misiones, setMisiones] = useState<MisionEstudianteResponse[]>([]);
  const [evaluaciones, setEvaluaciones] = useState<EvaluacionGamificadaResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedMision, setSelectedMision] =
    useState<MisionEstudianteResponse | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState<CompletarMisionRequest>({
    contenidoEntrega: "",
    archivoUrl: "",
    comentariosEstudiante: "",
  });
  const [submitting, setSubmitting] = useState(false);
  const [evaluacionActual, setEvaluacionActual] = useState<EvaluacionGamificadaResponse | null>(null);
  const [mostrarEvaluacion, setMostrarEvaluacion] = useState(false);
  const [cargandoEvaluacion, setCargandoEvaluacion] = useState(false);

  useEffect(() => {
    loadMisiones();
    loadEvaluaciones();
  }, []);

  const loadEvaluaciones = async () => {
    try {
      const result = await apiService.listarEvaluacionesEstudiante();
      setEvaluaciones(result);
      console.log('✅ Evaluaciones del estudiante:', result);
    } catch (e: unknown) {
      console.error('❌ Error cargando evaluaciones:', e);
    }
  };

  const loadMisiones = async () => {
    setLoading(true);
    setError(null);
    try {
      const estudianteId =
        localStorage.getItem("estudianteId") || usuario?.id || "";
      if (!estudianteId) {
        throw new Error("No se encontró el ID del estudiante");
      }
      const result = await apiService.listarMisionesPorEstudiante(estudianteId);
      setMisiones(result);
    } catch (e: unknown) {
      const errorMessage =
        e instanceof Error ? e.message : "Error cargando misiones";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleCompletarClick = async (mision: MisionEstudianteResponse) => {
    if (mision.completada) {
      alert("Esta misión ya fue completada");
      return;
    }

    // Verificar si la misión está expirada
    const fechaLimite = new Date(mision.fechaLimite);
    const ahora = new Date();
    if (fechaLimite < ahora) {
      alert(
        "Esta misión ya ha expirado. La fecha límite era: " +
          formatDate(mision.fechaLimite)
      );
      return;
    }

    // Si es una misión QUIZ, verificar si tiene evaluación
    if (mision.categoria === "QUIZ") {
      setCargandoEvaluacion(true);
      try {
        const evaluacion = await apiService.obtenerEvaluacionPorMision(mision.id);
        if (evaluacion && evaluacion.activo) {
          // Verificar intentos restantes
          const intentosRestantes = await apiService.obtenerIntentosRestantes(evaluacion.id);
          if (intentosRestantes <= 0) {
            alert("No tienes más intentos disponibles para esta evaluación");
            setCargandoEvaluacion(false);
            return;
          }
          setEvaluacionActual(evaluacion);
          setMostrarEvaluacion(true);
          setCargandoEvaluacion(false);
          return;
        }
      } catch (error: any) {
        // Si no hay evaluación, continuar con el flujo normal
        console.log("No se encontró evaluación para esta misión");
      }
      setCargandoEvaluacion(false);
    }

    // Flujo normal para misiones no-QUIZ o QUIZ sin evaluación
    setSelectedMision(mision);
    setFormData({
      contenidoEntrega: "",
      archivoUrl: "",
      comentariosEstudiante: "",
    });
    setShowModal(true);
  };

  const handleEvaluacionCompletada = async (resultado: ResultadoEvaluacionResponse) => {
    setMostrarEvaluacion(false);
    setEvaluacionActual(null);
    await loadMisiones();
    await loadEvaluaciones();
    alert(
      `¡Felicidades! Has completado la evaluación.\nPuntos: ${resultado.puntosTotales}\nPorcentaje: ${resultado.porcentaje.toFixed(1)}%`
    );
  };

  const handleTomarEvaluacion = async (evaluacion: EvaluacionGamificadaResponse) => {
    setCargandoEvaluacion(true);
    try {
      const intentosRestantes = await apiService.obtenerIntentosRestantes(evaluacion.id);
      if (intentosRestantes <= 0) {
        alert("No tienes más intentos disponibles para esta evaluación");
        setCargandoEvaluacion(false);
        return;
      }
      setEvaluacionActual(evaluacion);
      setMostrarEvaluacion(true);
    } catch (error: any) {
      alert("Error al cargar la evaluación");
      console.error(error);
    } finally {
      setCargandoEvaluacion(false);
    }
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
      alert(
        `¡Felicidades! Has completado la misión y ganado ${selectedMision.puntosRecompensa} puntos.`
      );
    } catch (e: unknown) {
      const errorMessage =
        e instanceof Error ? e.message : "Error al completar la misión";
      alert(errorMessage);
    } finally {
      setSubmitting(false);
    }
  };

  const isExpired = (fechaLimite: string) => {
    return new Date(fechaLimite) < new Date();
  };

  // getTemaStyles ahora usa getTemaConfig de temaUtils

  const getEstadoBadge = (mision: MisionEstudianteResponse) => {
    if (mision.completada) {
      return (
        <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
          <span>✓</span>
          Completada
        </span>
      );
    }

    // Verificar si está expirada
    if (isExpired(mision.fechaLimite)) {
      return (
        <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800">
          <span>🔒</span>
          Expirada
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
      case "FACIL":
        return "bg-green-100 text-green-800";
      case "MEDIO":
        return "bg-yellow-100 text-yellow-800";
      case "DIFICIL":
        return "bg-orange-100 text-orange-800";
      case "EXPERTO":
        return "bg-red-100 text-red-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString("es-ES", {
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
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
        <h1 className="text-3xl font-bold text-gray-900">
          Mis Misiones Asignadas
        </h1>
        <p className="text-gray-500 mt-1">
          Misiones creadas por tus profesores. Completa misiones para ganar
          puntos y experiencia
        </p>
      </div>

      {/* Evaluaciones Disponibles */}
      {evaluaciones.length > 0 && (
        <div className="bg-gradient-to-r from-purple-50 to-pink-50 rounded-lg shadow-md p-6 border-2 border-purple-200">
          <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center gap-2">
            📝 Evaluaciones Disponibles ({evaluaciones.length})
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {evaluaciones.map((evaluacion) => (
              <div
                key={evaluacion.id}
                className="bg-white border-2 border-purple-300 rounded-lg p-4 hover:shadow-lg transition-all"
              >
                <h3 className="font-bold text-gray-900 mb-2">{evaluacion.titulo}</h3>
                <p className="text-sm text-gray-600 mb-3 line-clamp-2">{evaluacion.descripcion}</p>
                <div className="space-y-2 mb-4">
                  <div className="text-sm text-gray-700">
                    <span className="font-semibold">Curso:</span> {evaluacion.cursoNombre}
                  </div>
                  <div className="text-sm text-gray-700">
                    <span className="font-semibold">Tiempo:</span> {evaluacion.tiempoLimiteMinutos} min
                  </div>
                  <div className="text-sm text-gray-700">
                    <span className="font-semibold">Preguntas:</span> {evaluacion.preguntas?.length || 0}
                  </div>
                  <div className="text-sm text-gray-700">
                    <span className="font-semibold">Intentos:</span> {evaluacion.intentosPermitidos}
                  </div>
                </div>
                <button
                  onClick={() => handleTomarEvaluacion(evaluacion)}
                  disabled={cargandoEvaluacion}
                  className="w-full bg-gradient-to-r from-purple-600 to-pink-600 text-white py-2 px-4 rounded-lg font-bold hover:from-purple-700 hover:to-pink-700 transition-all disabled:opacity-50"
                >
                  {cargandoEvaluacion ? "Cargando..." : "📝 Tomar Evaluación"}
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

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
          {misiones.map((mision) => {
            const expired = isExpired(mision.fechaLimite);
            const tema = getTemaConfig(mision.temaVisual || "DEFAULT");
            const headerImage = getTemaImage(mision.temaVisual || "DEFAULT", "header-bg.jpg");
            
            return (
              <div
                key={mision.id}
                className={`rounded-xl shadow-2xl border-2 overflow-hidden hover:scale-105 hover:shadow-3xl transition-all duration-300 transform ${
                  expired && !mision.completada
                    ? "opacity-60 grayscale"
                    : ""
                } ${tema.border} bg-white`}
              >
                {/* Header temático con imagen de fondo */}
                <div 
                  className={`relative ${tema.headerBg} p-6 text-white min-h-[180px] flex flex-col justify-between overflow-hidden`}
                  style={{
                    backgroundImage: `url(${headerImage})`,
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                    backgroundBlendMode: 'overlay'
                  }}
                >
                  {/* Overlay oscuro para mejor legibilidad */}
                  <div className="absolute inset-0 bg-black bg-opacity-40"></div>
                  
                  {/* Contenido del header */}
                  <div className="relative z-10">
                    <div className="flex items-center justify-between mb-3">
                      <div className="flex items-center gap-2">
                        <span className="text-4xl drop-shadow-lg">{tema.icon}</span>
                        <div className="bg-black bg-opacity-30 px-3 py-1 rounded-full backdrop-blur-sm">
                          <span className="text-xs font-semibold">{mision.cursoNombre}</span>
                        </div>
                      </div>
                      {getEstadoBadge(mision)}
                    </div>
                    <h3 className="text-xl font-bold mb-2 drop-shadow-lg">{mision.titulo}</h3>
                  </div>
                  
                  {/* Badge de dificultad en el header */}
                  <div className="relative z-10 flex items-center gap-2">
                    <span className={`px-3 py-1 rounded-full text-xs font-bold backdrop-blur-sm bg-black bg-opacity-40 ${getDificultadColor(mision.dificultad)}`}>
                      {mision.dificultad}
                    </span>
                    <span className="px-3 py-1 rounded-full text-xs font-bold backdrop-blur-sm bg-yellow-500 bg-opacity-80 text-yellow-900 flex items-center gap-1">
                      ⭐ {mision.puntosRecompensa} pts
                    </span>
                  </div>
                </div>
                
                {/* Contenido mejorado */}
                <div className={`${tema.cardBg} p-5`}>
                  <p className="text-sm text-gray-700 mb-4 line-clamp-3 leading-relaxed">
                    {mision.descripcion}
                  </p>

                {mision.completada && mision.puntosObtenidos > 0 && (
                  <div className="mb-4 p-2 bg-green-50 rounded text-sm text-green-800">
                    <strong>Puntos obtenidos: {mision.puntosObtenidos}</strong>
                  </div>
                )}

                {/* Barra de progreso mejorada */}
                <div className="mb-4">
                  <div className="flex items-center justify-between text-xs font-semibold text-gray-700 mb-2">
                    <span className="flex items-center gap-1">
                      <span className="text-lg">📊</span> Progreso
                    </span>
                    <span className={`font-bold ${mision.porcentajeCompletado === 100 ? 'text-green-600' : 'text-gray-600'}`}>
                      {mision.porcentajeCompletado}%
                    </span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-3 overflow-hidden shadow-inner">
                    <div
                      className={`h-3 rounded-full transition-all duration-500 ${tema.gradient} bg-gradient-to-r`}
                      style={{ width: `${mision.porcentajeCompletado}%` }}
                    />
                  </div>
                </div>

                {/* Información de fechas mejorada */}
                <div className="space-y-2 mb-4">
                  <div className={`text-xs p-2 rounded-lg ${
                    isExpired(mision.fechaLimite)
                      ? "bg-red-50 text-red-700 border border-red-200"
                      : "bg-gray-50 text-gray-600"
                  }`}>
                    <div className="flex items-center gap-1 font-semibold">
                      <span>📅</span> Fecha límite: {formatDate(mision.fechaLimite)}
                      {isExpired(mision.fechaLimite) && <span className="ml-1">⚠️</span>}
                    </div>
                  </div>
                  {mision.fechaCompletado && (
                    <div className="text-xs p-2 rounded-lg bg-green-50 text-green-700 border border-green-200">
                      <div className="flex items-center gap-1 font-semibold">
                        <span>✅</span> Completada: {formatDate(mision.fechaCompletado)}
                      </div>
                    </div>
                  )}
                </div>

                {!mision.completada && (
                  <>
                    {isExpired(mision.fechaLimite) ? (
                      <button
                        disabled
                        className="w-full bg-gray-300 text-gray-500 px-4 py-2 rounded-lg font-medium cursor-not-allowed"
                      >
                        🔒 Misión Expirada
                      </button>
                    ) : (
                      <button
                        onClick={() => handleCompletarClick(mision)}
                        className={`w-full py-3 px-4 rounded-lg font-bold text-sm transition-all transform hover:scale-105 shadow-lg bg-gradient-to-r ${tema.gradient} text-white hover:shadow-xl`}
                      >
                        🎯 Completar Misión
                      </button>
                    )}
                  </>
                )}
                </div>
              </div>
            );
          })}
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
                  <strong>Puntos a ganar:</strong>{" "}
                  {selectedMision.puntosRecompensa}
                </p>
                <p className="text-sm text-gray-600">
                  {selectedMision.descripcion}
                </p>
              </div>

              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Contenido de la entrega{" "}
                    <span className="text-red-500">*</span>
                  </label>
                  <textarea
                    value={formData.contenidoEntrega}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        contenidoEntrega: e.target.value,
                      })
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
                    value={formData.archivoUrl || ""}
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
                    value={formData.comentariosEstudiante || ""}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        comentariosEstudiante: e.target.value,
                      })
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
                    {submitting ? "Enviando..." : "Completar y Enviar"}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* Modal de Evaluación Gamificada */}
      {evaluacionActual && (
        <TomarEvaluacionModal
          isOpen={mostrarEvaluacion}
          onClose={() => {
            setMostrarEvaluacion(false);
            setEvaluacionActual(null);
          }}
          evaluacion={evaluacionActual}
          onComplete={handleEvaluacionCompletada}
        />
      )}

      {cargandoEvaluacion && (
        <div className="fixed inset-0 backdrop-blur-sm bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6">
            <div className="text-center">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
              <p className="text-gray-700">Cargando evaluación...</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default MisionesEstudiante;
