import React, { useState, useEffect, useRef } from "react";
import { ChevronLeft, ChevronRight, Clock, CheckCircle, Trophy } from "lucide-react";
import { apiService } from "../../services/api";
import type {
  EvaluacionGamificadaResponse,
  PreguntaResponse,
  RespuestaRequest,
  ResultadoEvaluacionResponse,
} from "../../types";
import Confetti from "react-confetti";

interface Props {
  isOpen: boolean;
  onClose: () => void;
  evaluacion: EvaluacionGamificadaResponse;
  onComplete: (resultado: ResultadoEvaluacionResponse) => void;
}

const TomarEvaluacionModal: React.FC<Props> = ({ isOpen, onClose, evaluacion, onComplete }) => {
  const [preguntaActual, setPreguntaActual] = useState(0);
  const [respuestas, setRespuestas] = useState<Map<string, RespuestaRequest>>(new Map());
  const [tiempoInicio] = useState(Date.now());
  const [tiempoTranscurrido, setTiempoTranscurrido] = useState(0);
  const [mostrarResultado, setMostrarResultado] = useState(false);
  const [resultado, setResultado] = useState<ResultadoEvaluacionResponse | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const [tiemposPorPregunta, setTiemposPorPregunta] = useState<Map<string, number>>(new Map());
  const [inicioPregunta, setInicioPregunta] = useState(Date.now());

  useEffect(() => {
    if (isOpen && evaluacion.tiempoLimiteMinutos) {
      intervalRef.current = setInterval(() => {
        const transcurrido = Math.floor((Date.now() - tiempoInicio) / 1000);
        setTiempoTranscurrido(transcurrido);

        // Verificar si se agotó el tiempo
        const tiempoLimiteSegundos = evaluacion.tiempoLimiteMinutos! * 60;
        if (transcurrido >= tiempoLimiteSegundos) {
          handleFinalizar();
        }
      }, 1000);
    }

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [isOpen, evaluacion.tiempoLimiteMinutos, tiempoInicio]);

  useEffect(() => {
    // Registrar tiempo de inicio de cada pregunta
    if (isOpen && preguntaActual < evaluacion.preguntas.length) {
      setInicioPregunta(Date.now());
    }
  }, [preguntaActual, isOpen, evaluacion.preguntas.length]);

  const formatearTiempo = (segundos: number): string => {
    const mins = Math.floor(segundos / 60);
    const secs = segundos % 60;
    return `${mins.toString().padStart(2, "0")}:${secs.toString().padStart(2, "0")}`;
  };

  const tiempoRestante = evaluacion.tiempoLimiteMinutos
    ? evaluacion.tiempoLimiteMinutos * 60 - tiempoTranscurrido
    : null;

  const handleRespuesta = (pregunta: PreguntaResponse, opcionId?: string, respuestaTexto?: string) => {
    const tiempoRespuesta = Math.floor((Date.now() - inicioPregunta) / 1000);
    setTiemposPorPregunta(new Map(tiemposPorPregunta.set(pregunta.id, tiempoRespuesta)));

    const nuevaRespuesta: RespuestaRequest = {
      preguntaId: pregunta.id,
      opcionId,
      respuestaTexto,
      tiempoRespuestaSegundos: tiempoRespuesta,
    };

    setRespuestas(new Map(respuestas.set(pregunta.id, nuevaRespuesta)));
  };

  const handleSiguiente = () => {
    if (preguntaActual < evaluacion.preguntas.length - 1) {
      setPreguntaActual(preguntaActual + 1);
    }
  };

  const handleAnterior = () => {
    if (preguntaActual > 0) {
      setPreguntaActual(preguntaActual - 1);
    }
  };

  const handleFinalizar = async () => {
    if (submitting) return;

    // Verificar que todas las preguntas tengan respuesta
    const preguntasSinRespuesta = evaluacion.preguntas.filter(
      (p) => !respuestas.has(p.id)
    );
    if (preguntasSinRespuesta.length > 0) {
      if (
        !confirm(
          `Tienes ${preguntasSinRespuesta.length} pregunta(s) sin responder. ¿Deseas finalizar de todas formas?`
        )
      ) {
        return;
      }
    }

    setSubmitting(true);
    try {
      const tiempoTotal = Math.floor((Date.now() - tiempoInicio) / 1000);
      const respuestasArray = Array.from(respuestas.values());

      const resultado = await apiService.responderEvaluacion({
        evaluacionId: evaluacion.id,
        respuestas: respuestasArray,
        tiempoTotalSegundos: tiempoTotal,
      });

      setResultado(resultado);
      setMostrarResultado(true);
      onComplete(resultado);
    } catch (error: any) {
      alert(error?.response?.data?.message || "Error al enviar la evaluación");
    } finally {
      setSubmitting(false);
    }
  };

  const getProgreso = () => {
    return ((preguntaActual + 1) / evaluacion.preguntas.length) * 100;
  };

  const pregunta = evaluacion.preguntas[preguntaActual];
  const respuestaActual = respuestas.get(pregunta.id);

  if (!isOpen) return null;

  if (mostrarResultado && resultado) {
    return (
      <div className="fixed inset-0 backdrop-blur-sm bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
        <div className="bg-white rounded-xl shadow-2xl max-w-2xl w-full p-8 relative overflow-hidden">
          <Confetti width={window.innerWidth} height={window.innerHeight} recycle={false} />
          
          <div className="text-center">
            <div className="mb-6">
              {resultado.porcentaje >= 70 ? (
                <div className="w-24 h-24 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                  <CheckCircle className="w-16 h-16 text-green-600" />
                </div>
              ) : (
                <div className="w-24 h-24 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-4">
                  <Trophy className="w-16 h-16 text-yellow-600" />
                </div>
              )}
            </div>

            <h2 className="text-3xl font-bold text-gray-900 mb-2">
              {resultado.porcentaje >= 70 ? "¡Excelente!" : "¡Buen trabajo!"}
            </h2>
            <p className="text-gray-600 mb-6">Has completado la evaluación</p>

            <div className="grid grid-cols-2 gap-4 mb-6">
              <div className="bg-blue-50 p-4 rounded-lg">
                <div className="text-2xl font-bold text-blue-600">{resultado.puntosTotales}</div>
                <div className="text-sm text-gray-600">Puntos Obtenidos</div>
              </div>
              <div className="bg-green-50 p-4 rounded-lg">
                <div className="text-2xl font-bold text-green-600">{resultado.porcentaje.toFixed(1)}%</div>
                <div className="text-sm text-gray-600">Porcentaje</div>
              </div>
              <div className="bg-purple-50 p-4 rounded-lg">
                <div className="text-2xl font-bold text-purple-600">
                  {resultado.preguntasCorrectas}/{resultado.preguntasTotales}
                </div>
                <div className="text-sm text-gray-600">Correctas</div>
              </div>
              <div className="bg-yellow-50 p-4 rounded-lg">
                <div className="text-2xl font-bold text-yellow-600">+{resultado.puntosBonus}</div>
                <div className="text-sm text-gray-600">Bonus Tiempo</div>
              </div>
            </div>

            <button
              onClick={onClose}
              className="w-full px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-semibold"
            >
              Cerrar
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="fixed inset-0 backdrop-blur-sm bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-hidden flex flex-col">
        {/* Header con Timer y Progreso */}
        <div className="bg-gradient-to-r from-blue-600 to-purple-600 text-white p-6">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h2 className="text-2xl font-bold">{evaluacion.titulo}</h2>
              <p className="text-sm opacity-90">{evaluacion.cursoNombre}</p>
            </div>
            {tiempoRestante !== null && (
              <div className="flex items-center gap-2 bg-black bg-opacity-30 px-4 py-2 rounded-lg">
                <Clock className="w-5 h-5" />
                <span className="text-xl font-bold">
                  {formatearTiempo(Math.max(0, tiempoRestante))}
                </span>
              </div>
            )}
          </div>

          {/* Barra de Progreso */}
          <div className="mb-2">
            <div className="flex items-center justify-between text-sm mb-1">
              <span>Pregunta {preguntaActual + 1} de {evaluacion.preguntas.length}</span>
              <span>{Math.round(getProgreso())}%</span>
            </div>
            <div className="w-full bg-white bg-opacity-30 rounded-full h-3">
              <div
                className="bg-white h-3 rounded-full transition-all duration-300"
                style={{ width: `${getProgreso()}%` }}
              />
            </div>
          </div>
        </div>

        {/* Contenido de la Pregunta */}
        <div className="flex-1 overflow-y-auto p-6">
          <div className="max-w-3xl mx-auto">
            <div className="mb-6">
              <div className="flex items-center gap-2 mb-4">
                <span className="text-2xl font-bold text-gray-800">
                  {preguntaActual + 1}.
                </span>
                <h3 className="text-xl font-semibold text-gray-900">{pregunta.enunciado}</h3>
              </div>

              {pregunta.imagenUrl && (
                <img
                  src={pregunta.imagenUrl}
                  alt="Pregunta"
                  className="mb-4 rounded-lg max-w-full"
                />
              )}

              {/* Opciones según tipo */}
              {pregunta.tipoPregunta === "OPCION_MULTIPLE" ||
              pregunta.tipoPregunta === "VERDADERO_FALSO" ||
              pregunta.tipoPregunta === "SELECCION_MULTIPLE" ? (
                <div className="space-y-3">
                  {pregunta.opciones.map((opcion) => (
                    <button
                      key={opcion.id}
                      onClick={() => handleRespuesta(pregunta, opcion.id)}
                      className={`w-full text-left p-4 rounded-lg border-2 transition-all ${
                        respuestaActual?.opcionId === opcion.id
                          ? "border-blue-600 bg-blue-50"
                          : "border-gray-200 hover:border-blue-300 hover:bg-gray-50"
                      }`}
                    >
                      <div className="flex items-center gap-3">
                        <div
                          className={`w-6 h-6 rounded-full border-2 flex items-center justify-center ${
                            respuestaActual?.opcionId === opcion.id
                              ? "border-blue-600 bg-blue-600"
                              : "border-gray-300"
                          }`}
                        >
                          {respuestaActual?.opcionId === opcion.id && (
                            <div className="w-3 h-3 bg-white rounded-full" />
                          )}
                        </div>
                        <span className="flex-1">{opcion.texto}</span>
                      </div>
                    </button>
                  ))}
                </div>
              ) : pregunta.tipoPregunta === "COMPLETAR_ESPACIOS" ? (
                <div>
                  <textarea
                    value={respuestaActual?.respuestaTexto || ""}
                    onChange={(e) => handleRespuesta(pregunta, undefined, e.target.value)}
                    placeholder="Escribe tu respuesta..."
                    rows={4}
                    className="w-full px-4 py-3 border-2 border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              ) : null}
            </div>
          </div>
        </div>

        {/* Footer con Navegación */}
        <div className="border-t border-gray-200 p-4 bg-gray-50">
          <div className="flex items-center justify-between">
            <button
              onClick={handleAnterior}
              disabled={preguntaActual === 0}
              className="flex items-center gap-2 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <ChevronLeft className="w-5 h-5" />
              Anterior
            </button>

            <div className="flex gap-2">
              {evaluacion.preguntas.map((_, index) => (
                <button
                  key={index}
                  onClick={() => setPreguntaActual(index)}
                  className={`w-10 h-10 rounded-lg border-2 transition-colors ${
                    index === preguntaActual
                      ? "border-blue-600 bg-blue-600 text-white"
                      : respuestas.has(evaluacion.preguntas[index].id)
                      ? "border-green-500 bg-green-100 text-green-700"
                      : "border-gray-300 bg-white text-gray-600 hover:border-gray-400"
                  }`}
                >
                  {index + 1}
                </button>
              ))}
            </div>

            {preguntaActual === evaluacion.preguntas.length - 1 ? (
              <button
                onClick={handleFinalizar}
                disabled={submitting}
                className="flex items-center gap-2 px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors disabled:opacity-50"
              >
                {submitting ? "Enviando..." : "Finalizar Evaluación"}
              </button>
            ) : (
              <button
                onClick={handleSiguiente}
                className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                Siguiente
                <ChevronRight className="w-5 h-5" />
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default TomarEvaluacionModal;

