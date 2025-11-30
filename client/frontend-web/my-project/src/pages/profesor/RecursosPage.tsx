// src/pages/profesor/RecursosPage.tsx
import { useState, useEffect } from "react";
import { Sparkles, MessageSquare, BookOpen, Loader2, Users, ClipboardCheck } from "lucide-react";
import { apiService } from "../../services/api";
import type { Curso, EvaluacionGamificadaResponse, RetroalimentacionResponse, ActividadesAdaptadasResponse } from "../../types";

const RecursosPage = () => {
  // Estados generales
  const [cursos, setCursos] = useState<Curso[]>([]);
  const [cursoSeleccionado, setCursoSeleccionado] = useState<string>("");

  // Estados para Tarea 19 - Retroalimentación
  const [evaluaciones, setEvaluaciones] = useState<EvaluacionGamificadaResponse[]>([]);
  const [evaluacionSeleccionada, setEvaluacionSeleccionada] = useState<string>("");
  const [estudiantes, setEstudiantes] = useState<{ id: string; nombreCompleto: string }[]>([]);
  const [estudianteSeleccionado, setEstudianteSeleccionado] = useState<string>("");
  const [retroalimentacion, setRetroalimentacion] = useState<RetroalimentacionResponse | null>(null);
  const [loadingRetro, setLoadingRetro] = useState(false);
  const [errorRetro, setErrorRetro] = useState<string | null>(null);

  // Estados para Tarea 21 - Actividades Adaptadas
  const [tema, setTema] = useState("");
  const [cantidadPreguntas, setCantidadPreguntas] = useState(5);
  const [estudianteActividades, setEstudianteActividades] = useState<string>("");
  const [actividades, setActividades] = useState<ActividadesAdaptadasResponse | null>(null);
  const [loadingActividades, setLoadingActividades] = useState(false);
  const [errorActividades, setErrorActividades] = useState<string | null>(null);

  // Cargar cursos al inicio
  useEffect(() => {
    cargarCursos();
  }, []);

  // Cargar evaluaciones cuando cambia el curso
  useEffect(() => {
    if (cursoSeleccionado) {
      cargarEvaluaciones();
      cargarEstudiantes();
    }
  }, [cursoSeleccionado]);

  const cargarCursos = async () => {
    try {
      const profesorId = localStorage.getItem("profesorId") || localStorage.getItem("userId") || "";
      console.log("🔍 profesorId encontrado:", profesorId);
      console.log("🔍 Todos los items en localStorage:", Object.keys(localStorage));
      
      if (profesorId) {
        console.log("📡 Llamando a listarCursosPorProfesor...");
        const data = await apiService.listarCursosPorProfesor(profesorId);
        console.log("✅ Cursos recibidos:", data);
        setCursos(data);
      } else {
        console.warn("⚠️ No se encontró profesorId en localStorage");
      }
    } catch (error) {
      console.error("❌ Error cargando cursos:", error);
    }
  };

  const cargarEvaluaciones = async () => {
    try {
      const data = await apiService.listarEvaluacionesPorCurso(cursoSeleccionado);
      setEvaluaciones(data);
    } catch (error) {
      console.error("Error cargando evaluaciones:", error);
    }
  };

  const cargarEstudiantes = async () => {
    try {
      const data = await apiService.obtenerEstudiantesPorCurso(cursoSeleccionado);
      setEstudiantes(data);
    } catch (error) {
      console.error("Error cargando estudiantes:", error);
    }
  };

  // Tarea 19: Generar Retroalimentación Automática
  const generarRetroalimentacion = async () => {
    if (!estudianteSeleccionado || !evaluacionSeleccionada) {
      setErrorRetro("Selecciona un estudiante y una evaluación");
      return;
    }

    setLoadingRetro(true);
    setErrorRetro(null);
    setRetroalimentacion(null);

    try {
      const response = await apiService.generarRetroalimentacion({
        estudianteId: estudianteSeleccionado,
        evaluacionId: evaluacionSeleccionada,
      });
      setRetroalimentacion(response);
    } catch (error: unknown) {
      const errorMessage = error instanceof Error ? error.message : "Error al generar retroalimentación";
      setErrorRetro(errorMessage);
    } finally {
      setLoadingRetro(false);
    }
  };

  // Tarea 21: Generar Actividades Adaptadas
  const generarActividadesAdaptadas = async () => {
    if (!cursoSeleccionado) {
      setErrorActividades("Selecciona un curso primero");
      return;
    }

    if (!estudianteActividades) {
      setErrorActividades("Selecciona un estudiante para generar actividades");
      return;
    }

    if (!tema.trim()) {
      setErrorActividades("Ingresa un tema específico para generar actividades");
      return;
    }

    setLoadingActividades(true);
    setErrorActividades(null);
    setActividades(null);

    try {
      const response = await apiService.generarActividadesAdaptadas({
        cursoId: cursoSeleccionado,
        tema: tema.trim(),
        cantidadPreguntas: cantidadPreguntas,
        tipoActividad: "evaluacion",
        estudianteId: estudianteActividades,
      });
      setActividades(response);
    } catch (error: unknown) {
      const errorMessage = error instanceof Error ? error.message : "Error al generar actividades";
      setErrorActividades(errorMessage);
    } finally {
      setLoadingActividades(false);
    }
  };

  return (
    <div className="space-y-8">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900 flex items-center gap-3">
          <Sparkles className="w-8 h-8 text-purple-600" />
          Recursos IA
        </h1>
        <p className="text-gray-600 mt-2">
          Genera recursos educativos personalizados utilizando Inteligencia Artificial
        </p>
      </div>

      {/* Selector de Curso */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Selecciona un Curso
        </label>
        <select
          value={cursoSeleccionado}
          onChange={(e) => {
            setCursoSeleccionado(e.target.value);
            setEvaluacionSeleccionada("");
            setEstudianteSeleccionado("");
            setRetroalimentacion(null);
            setActividades(null);
          }}
          className="w-full max-w-md px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
        >
          <option value="">-- Selecciona un curso --</option>
          {cursos.map((curso) => (
            <option key={curso.id} value={curso.id}>
              {curso.nombre} ({curso.codigoCurso})
            </option>
          ))}
        </select>
      </div>

      {cursoSeleccionado && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* ========== TAREA 19: Retroalimentación Automática ========== */}
          <div className="bg-gradient-to-br from-blue-50 to-indigo-50 rounded-xl shadow-sm border border-blue-200 p-6">
            <div className="flex items-center gap-3 mb-6">
              <div className="w-12 h-12 bg-blue-600 rounded-xl flex items-center justify-center">
                <MessageSquare className="w-6 h-6 text-white" />
              </div>
              <div>
                <h2 className="text-xl font-bold text-gray-900">Retroalimentación Automática</h2>
                <p className="text-sm text-gray-600">Genera feedback personalizado con IA</p>
              </div>
            </div>

            <div className="space-y-4">
              {/* Selector de Evaluación */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  <ClipboardCheck className="w-4 h-4 inline mr-1" />
                  Evaluación
                </label>
                <select
                  value={evaluacionSeleccionada}
                  onChange={(e) => setEvaluacionSeleccionada(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">-- Selecciona una evaluación --</option>
                  {evaluaciones.map((ev) => (
                    <option key={ev.id} value={ev.id}>
                      {ev.titulo}
                    </option>
                  ))}
                </select>
              </div>

              {/* Selector de Estudiante */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  <Users className="w-4 h-4 inline mr-1" />
                  Estudiante
                </label>
                <select
                  value={estudianteSeleccionado}
                  onChange={(e) => setEstudianteSeleccionado(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">-- Selecciona un estudiante --</option>
                  {estudiantes.map((est) => (
                    <option key={est.id} value={est.id}>
                      {est.nombreCompleto}
                    </option>
                  ))}
                </select>
              </div>

              {/* Botón Generar */}
              <button
                onClick={generarRetroalimentacion}
                disabled={loadingRetro || !evaluacionSeleccionada || !estudianteSeleccionado}
                className="w-full py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
              >
                {loadingRetro ? (
                  <>
                    <Loader2 className="w-5 h-5 animate-spin" />
                    Generando...
                  </>
                ) : (
                  <>
                    <Sparkles className="w-5 h-5" />
                    Generar Retroalimentación
                  </>
                )}
              </button>

              {/* Error */}
              {errorRetro && (
                <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
                  {errorRetro}
                </div>
              )}

              {/* Resultado */}
              {retroalimentacion && (
                <div className="mt-4 p-4 bg-white rounded-lg border border-blue-200">
                  <h4 className="font-semibold text-gray-900 mb-2">
                    Retroalimentación para {retroalimentacion.estudianteNombre}
                  </h4>
                  <p className="text-sm text-gray-500 mb-3">
                    Evaluación: {retroalimentacion.evaluacionTitulo}
                  </p>
                  <div className="prose prose-sm max-w-none text-gray-700 whitespace-pre-wrap">
                    {retroalimentacion.retroalimentacion}
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* ========== TAREA 21: Actividades Adaptadas ========== */}
          <div className="bg-gradient-to-br from-purple-50 to-pink-50 rounded-xl shadow-sm border border-purple-200 p-6">
            <div className="flex items-center gap-3 mb-6">
              <div className="w-12 h-12 bg-purple-600 rounded-xl flex items-center justify-center">
                <BookOpen className="w-6 h-6 text-white" />
              </div>
              <div>
                <h2 className="text-xl font-bold text-gray-900">Actividades Adaptadas</h2>
                <p className="text-sm text-gray-600">Genera actividades según el nivel del estudiante</p>
              </div>
            </div>

            <div className="space-y-4">
              {/* Selector de Estudiante para las actividades */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  <Users className="w-4 h-4 inline mr-1" />
                  Estudiante
                </label>
                <select
                  value={estudianteActividades}
                  onChange={(e) => setEstudianteActividades(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500"
                >
                  <option value="">-- Selecciona un estudiante --</option>
                  {estudiantes.map((est) => (
                    <option key={est.id} value={est.id}>
                      {est.nombreCompleto}
                    </option>
                  ))}
                </select>
              </div>

              {/* Tema (obligatorio) */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Tema específico
                </label>
                <input
                  type="text"
                  value={tema}
                  onChange={(e) => setTema(e.target.value)}
                  placeholder="Ej: Ecuaciones cuadráticas, Programación orientada a objetos..."
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500"
                />
              </div>

              {/* Cantidad de Preguntas */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Cantidad de preguntas
                </label>
                <select
                  value={cantidadPreguntas}
                  onChange={(e) => setCantidadPreguntas(Number(e.target.value))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500"
                >
                  <option value={3}>3 preguntas</option>
                  <option value={5}>5 preguntas</option>
                  <option value={10}>10 preguntas</option>
                  <option value={15}>15 preguntas</option>
                </select>
              </div>

              {/* Botón Generar */}
              <button
                onClick={generarActividadesAdaptadas}
                disabled={loadingActividades}
                className="w-full py-3 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
              >
                {loadingActividades ? (
                  <>
                    <Loader2 className="w-5 h-5 animate-spin" />
                    Generando...
                  </>
                ) : (
                  <>
                    <Sparkles className="w-5 h-5" />
                    Generar Actividades
                  </>
                )}
              </button>

              {/* Error */}
              {errorActividades && (
                <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
                  {errorActividades}
                </div>
              )}

              {/* Resultado */}
              {actividades && (
                <div className="mt-4 p-4 bg-white rounded-lg border border-purple-200">
                  <h4 className="font-semibold text-gray-900 mb-2">
                    Actividades para: {actividades.cursoNombre}
                  </h4>
                  <p className="text-sm text-purple-600 mb-3">
                    Nivel detectado: {actividades.nivelPromedioEstudiantes}
                  </p>
                  
                  {actividades.actividades.map((actividad, idx) => (
                    <div key={idx} className="mt-4 p-4 bg-purple-50 rounded-lg">
                      <h5 className="font-medium text-gray-900">{actividad.titulo}</h5>
                      <p className="text-sm text-gray-600 mt-1">{actividad.descripcion}</p>
                      
                      {actividad.preguntas && actividad.preguntas.length > 0 && (
                        <div className="mt-3 space-y-2">
                          <p className="text-sm font-medium text-gray-700">Preguntas generadas:</p>
                          {actividad.preguntas.map((pregunta, pIdx) => (
                            <div key={pIdx} className="p-3 bg-white rounded border border-gray-200">
                              <p className="text-sm text-gray-800">{pIdx + 1}. {pregunta.enunciado}</p>
                              {pregunta.opciones && (
                                <ul className="mt-2 space-y-1">
                                  {pregunta.opciones.map((op, oIdx) => (
                                    <li 
                                      key={oIdx} 
                                      className={`text-xs px-2 py-1 rounded ${
                                        oIdx === pregunta.indiceCorrecta 
                                          ? 'bg-green-100 text-green-700' 
                                          : 'bg-gray-50 text-gray-600'
                                      }`}
                                    >
                                      {String.fromCharCode(65 + oIdx)}) {op}
                                    </li>
                                  ))}
                                </ul>
                              )}
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {!cursoSeleccionado && (
        <div className="text-center py-12 bg-white rounded-xl border border-gray-200">
          <Sparkles className="w-16 h-16 text-gray-300 mx-auto mb-4" />
          <p className="text-gray-500">Selecciona un curso para comenzar a generar recursos con IA</p>
        </div>
      )}
    </div>
  );
};

export default RecursosPage;
