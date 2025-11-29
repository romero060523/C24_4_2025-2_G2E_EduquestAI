import React, { useState, useEffect } from "react";
import { X, Plus, Trash2, Clock, Save, Sparkles, Loader2 } from "lucide-react";
import { apiService } from "../../services/api";
import type { CrearEvaluacionRequest, CrearPreguntaRequest, CrearOpcionRequest, TipoPregunta, MisionListResponse, Curso } from "../../types";

interface Props {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
  misionOpcional?: MisionListResponse | null; // Ahora la misión es opcional
}

const CrearEvaluacionModal: React.FC<Props> = ({ isOpen, onClose, onSuccess, misionOpcional }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [cursos, setCursos] = useState<Curso[]>([]);
  const [generandoActividades, setGenerandoActividades] = useState(false);
  const [formData, setFormData] = useState<CrearEvaluacionRequest>({
    misionId: misionOpcional?.id,
    cursoId: "",
    titulo: misionOpcional ? `Evaluación: ${misionOpcional.titulo}` : "",
    descripcion: "",
    tiempoLimiteMinutos: 30,
    intentosPermitidos: 10, // Más intentos por defecto
    mostrarResultadosInmediato: true,
    puntosPorPregunta: 10,
    puntosBonusTiempo: 5,
    preguntas: [],
  });

  // Cargar cursos del profesor al abrir el modal
  useEffect(() => {
    if (isOpen) {
      loadCursos();
    }
  }, [isOpen]);

  const loadCursos = async () => {
    try {
      const cursosData = await apiService.getMisCursos();
      setCursos(cursosData);
      // Si hay misión, preseleccionar su curso
      if (misionOpcional) {
        setFormData(prev => ({ ...prev, cursoId: misionOpcional.cursoId }));
      } else if (cursosData.length > 0) {
        // Sino, seleccionar el primer curso
        setFormData(prev => ({ ...prev, cursoId: cursosData[0].id }));
      }
    } catch (error) {
      console.error("Error cargando cursos:", error);
    }
  };

  const tiposPregunta: { value: TipoPregunta; label: string; icon: string }[] = [
    { value: "OPCION_MULTIPLE", label: "Opción Múltiple", icon: "🔘" },
    { value: "VERDADERO_FALSO", label: "Verdadero/Falso", icon: "✅" },
    { value: "SELECCION_MULTIPLE", label: "Selección Múltiple", icon: "☑️" },
  ];

  const agregarPregunta = () => {
    const nuevaPregunta: CrearPreguntaRequest = {
      enunciado: "",
      tipoPregunta: "OPCION_MULTIPLE",
      puntos: formData.puntosPorPregunta,
      orden: formData.preguntas.length,
      opciones: [
        { texto: "", esCorrecta: false, orden: 0 },
        { texto: "", esCorrecta: false, orden: 1 },
        { texto: "", esCorrecta: false, orden: 2 },
        { texto: "", esCorrecta: false, orden: 3 },
      ],
    };
    setFormData({
      ...formData,
      preguntas: [...formData.preguntas, nuevaPregunta],
    });
  };

  const eliminarPregunta = (index: number) => {
    const nuevasPreguntas = formData.preguntas.filter((_, i) => i !== index);
    setFormData({ ...formData, preguntas: nuevasPreguntas });
  };

  const actualizarPregunta = (index: number, campo: keyof CrearPreguntaRequest, valor: any) => {
    const nuevasPreguntas = [...formData.preguntas];
    nuevasPreguntas[index] = { ...nuevasPreguntas[index], [campo]: valor };
    setFormData({ ...formData, preguntas: nuevasPreguntas });
  };

  const agregarOpcion = (preguntaIndex: number) => {
    const nuevasPreguntas = [...formData.preguntas];
    const pregunta = nuevasPreguntas[preguntaIndex];
    const nuevaOpcion: CrearOpcionRequest = {
      texto: "",
      esCorrecta: false,
      orden: pregunta.opciones.length,
    };
    pregunta.opciones.push(nuevaOpcion);
    setFormData({ ...formData, preguntas: nuevasPreguntas });
  };

  const eliminarOpcion = (preguntaIndex: number, opcionIndex: number) => {
    const nuevasPreguntas = [...formData.preguntas];
    nuevasPreguntas[preguntaIndex].opciones = nuevasPreguntas[preguntaIndex].opciones.filter(
      (_, i) => i !== opcionIndex
    );
    setFormData({ ...formData, preguntas: nuevasPreguntas });
  };

  const actualizarOpcion = (
    preguntaIndex: number,
    opcionIndex: number,
    campo: keyof CrearOpcionRequest,
    valor: any
  ) => {
    const nuevasPreguntas = [...formData.preguntas];
    nuevasPreguntas[preguntaIndex].opciones[opcionIndex] = {
      ...nuevasPreguntas[preguntaIndex].opciones[opcionIndex],
      [campo]: valor,
    };
    setFormData({ ...formData, preguntas: nuevasPreguntas });
  };

  const generarActividadesAdaptadas = async () => {
    if (!formData.cursoId) {
      setError("Debes seleccionar un curso primero");
      return;
    }

    setGenerandoActividades(true);
    setError(null);

    try {
      const response = await apiService.generarActividadesAdaptadas({
        cursoId: formData.cursoId,
        cantidadPreguntas: 5,
        tipoActividad: "evaluacion",
      });

      // Convertir las actividades generadas a preguntas
      if (response.actividades && response.actividades.length > 0) {
        const actividad = response.actividades[0]; // Tomar la primera actividad
        const nuevasPreguntas: CrearPreguntaRequest[] = actividad.preguntas.map((p, index) => ({
          enunciado: p.enunciado,
          tipoPregunta: p.tipoPregunta as TipoPregunta,
          puntos: formData.puntosPorPregunta,
          orden: formData.preguntas.length + index,
          explicacion: p.explicacion,
          opciones: p.opciones.map((opcion, opcionIndex) => ({
            texto: opcion,
            esCorrecta: opcionIndex === p.indiceCorrecta,
            orden: opcionIndex,
          })),
        }));

        setFormData({
          ...formData,
          preguntas: [...formData.preguntas, ...nuevasPreguntas],
        });
      }
    } catch (e: unknown) {
      const errorMessage = e instanceof Error ? e.message : "Error al generar actividades adaptadas";
      setError(errorMessage);
    } finally {
      setGenerandoActividades(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    // Validaciones
    if (formData.preguntas.length === 0) {
      setError("Debes agregar al menos una pregunta");
      setLoading(false);
      return;
    }

    for (let i = 0; i < formData.preguntas.length; i++) {
      const pregunta = formData.preguntas[i];
      if (!pregunta.enunciado.trim()) {
        setError(`La pregunta ${i + 1} debe tener un enunciado`);
        setLoading(false);
        return;
      }
      if (pregunta.opciones.length < 2) {
        setError(`La pregunta ${i + 1} debe tener al menos 2 opciones`);
        setLoading(false);
        return;
      }
      const tieneCorrecta = pregunta.opciones.some((o) => o.esCorrecta);
      if (!tieneCorrecta) {
        setError(`La pregunta ${i + 1} debe tener al menos una opción correcta`);
        setLoading(false);
        return;
      }
    }

    try {
      await apiService.crearEvaluacionGamificada(formData);
      onSuccess();
      onClose();
    } catch (e: any) {
      setError(e?.response?.data?.message || "Error al crear la evaluación");
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 backdrop-blur-sm bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200 sticky top-0 bg-white z-10">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">Crear Evaluación Gamificada</h2>
            {misionOpcional && (
              <p className="text-sm text-gray-600 mt-1">Asociada a misión: {misionOpcional.titulo}</p>
            )}
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-6">
          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
              {error}
            </div>
          )}

          {/* Configuración General */}
          <div className="bg-blue-50 p-4 rounded-lg border border-blue-200">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">⚙️ Configuración General</h3>
            
            <div className="grid grid-cols-2 gap-4">
              {/* Selector de Curso */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  📚 Curso <span className="text-red-500">*</span>
                </label>
                <select
                  value={formData.cursoId}
                  onChange={(e) => setFormData({ ...formData, cursoId: e.target.value })}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">Selecciona un curso</option>
                  {cursos.map((curso) => (
                    <option key={curso.id} value={curso.id}>
                      {curso.codigoCurso} - {curso.nombre}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Título <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  value={formData.titulo}
                  onChange={(e) => setFormData({ ...formData, titulo: e.target.value })}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  <Clock className="w-4 h-4 inline mr-1" />
                  Tiempo Límite (minutos)
                </label>
                <input
                  type="number"
                  min="1"
                  value={formData.tiempoLimiteMinutos || ""}
                  onChange={(e) =>
                    setFormData({
                      ...formData,
                      tiempoLimiteMinutos: e.target.value ? parseInt(e.target.value) : undefined,
                    })
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Sin límite"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Intentos Permitidos
                </label>
                <input
                  type="number"
                  min="1"
                  value={formData.intentosPermitidos || 1}
                  onChange={(e) =>
                    setFormData({
                      ...formData,
                      intentosPermitidos: parseInt(e.target.value) || 1,
                    })
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Puntos por Pregunta
                </label>
                <input
                  type="number"
                  min="1"
                  value={formData.puntosPorPregunta || 10}
                  onChange={(e) =>
                    setFormData({
                      ...formData,
                      puntosPorPregunta: parseInt(e.target.value) || 10,
                    })
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div className="mt-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">Descripción</label>
              <textarea
                value={formData.descripcion || ""}
                onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })}
                rows={2}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Descripción de la evaluación..."
              />
            </div>
          </div>

          {/* Preguntas */}
          <div>
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-semibold text-gray-800">
                📝 Preguntas ({formData.preguntas.length})
              </h3>
              <div className="flex gap-2">
                {formData.cursoId && (
                  <button
                    type="button"
                    onClick={generarActividadesAdaptadas}
                    disabled={generandoActividades}
                    className="flex items-center gap-2 px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {generandoActividades ? (
                      <>
                        <Loader2 className="w-4 h-4 animate-spin" />
                        Generando...
                      </>
                    ) : (
                      <>
                        <Sparkles className="w-4 h-4" />
                        Generar con IA
                      </>
                    )}
                  </button>
                )}
                <button
                  type="button"
                  onClick={agregarPregunta}
                  className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                >
                  <Plus className="w-4 h-4" />
                  Agregar Pregunta
                </button>
              </div>
            </div>

            <div className="space-y-6">
              {formData.preguntas.map((pregunta, preguntaIndex) => (
                <div key={preguntaIndex} className="border border-gray-300 rounded-lg p-4 bg-gray-50">
                  <div className="flex justify-between items-start mb-4">
                    <h4 className="font-semibold text-gray-800">
                      Pregunta {preguntaIndex + 1}
                    </h4>
                    <button
                      type="button"
                      onClick={() => eliminarPregunta(preguntaIndex)}
                      className="text-red-600 hover:text-red-800"
                    >
                      <Trash2 className="w-5 h-5" />
                    </button>
                  </div>

                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Tipo de Pregunta
                      </label>
                      <select
                        value={pregunta.tipoPregunta}
                        onChange={(e) =>
                          actualizarPregunta(preguntaIndex, "tipoPregunta", e.target.value as TipoPregunta)
                        }
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      >
                        {tiposPregunta.map((tipo) => (
                          <option key={tipo.value} value={tipo.value}>
                            {tipo.icon} {tipo.label}
                          </option>
                        ))}
                      </select>
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Enunciado <span className="text-red-500">*</span>
                      </label>
                      <textarea
                        value={pregunta.enunciado}
                        onChange={(e) =>
                          actualizarPregunta(preguntaIndex, "enunciado", e.target.value)
                        }
                        required
                        rows={2}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="Escribe la pregunta..."
                      />
                    </div>

                    {/* Opciones */}
                    <div>
                      <div className="flex justify-between items-center mb-2">
                        <label className="block text-sm font-medium text-gray-700">
                          Opciones de Respuesta
                        </label>
                        <button
                          type="button"
                          onClick={() => agregarOpcion(preguntaIndex)}
                          className="text-sm text-blue-600 hover:text-blue-800 flex items-center gap-1"
                        >
                          <Plus className="w-4 h-4" />
                          Agregar Opción
                        </button>
                      </div>

                      <div className="space-y-2">
                        {pregunta.opciones.map((opcion, opcionIndex) => (
                          <div
                            key={opcionIndex}
                            className="flex items-center gap-2 p-2 bg-white rounded border border-gray-200"
                          >
                            <input
                              type="checkbox"
                              checked={opcion.esCorrecta || false}
                              onChange={(e) =>
                                actualizarOpcion(
                                  preguntaIndex,
                                  opcionIndex,
                                  "esCorrecta",
                                  e.target.checked
                                )
                              }
                              className="w-5 h-5 text-green-600"
                            />
                            <input
                              type="text"
                              value={opcion.texto}
                              onChange={(e) =>
                                actualizarOpcion(preguntaIndex, opcionIndex, "texto", e.target.value)
                              }
                              placeholder={`Opción ${opcionIndex + 1}`}
                              className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                            <button
                              type="button"
                              onClick={() => eliminarOpcion(preguntaIndex, opcionIndex)}
                              className="text-red-600 hover:text-red-800"
                            >
                              <Trash2 className="w-4 h-4" />
                            </button>
                          </div>
                        ))}
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Botones */}
          <div className="flex gap-3 pt-4 border-t border-gray-200 sticky bottom-0 bg-white">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loading || formData.preguntas.length === 0}
              className="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
              <Save className="w-5 h-5" />
              {loading ? "Creando..." : "Crear Evaluación"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CrearEvaluacionModal;


