import { useEffect, useState } from "react";
import { apiService } from "../../services/api";
import type { EvaluacionGamificadaResponse, Curso } from "../../types";
import RetroalimentacionModal from "../../components/profesor/RetroalimentacionModal";

const EvaluacionesPage = () => {
  const [cursos, setCursos] = useState<Curso[]>([]);
  const [evaluaciones, setEvaluaciones] = useState<EvaluacionGamificadaResponse[]>([]);
  const [, setResultados] = useState<{ [key: string]: unknown }>({});
  const [cursoSeleccionado, setCursoSeleccionado] = useState<string | null>(null);
  const [evaluacionSeleccionada, setEvaluacionSeleccionada] = useState<string | null>(null);
  const [isLoading, setLoading] = useState(false);
  const [errorMsg, setError] = useState<string | null>(null);
  const [retroalimentacionModal, setRetroalimentacionModal] = useState<{
    isOpen: boolean;
    estudianteId: string;
    estudianteNombre: string;
    evaluacionId: string;
    evaluacionTitulo: string;
  }>({
    isOpen: false,
    estudianteId: "",
    estudianteNombre: "",
    evaluacionId: "",
    evaluacionTitulo: "",
  });

  useEffect(() => {
    loadCursos();
  }, []);

  useEffect(() => {
    if (cursoSeleccionado) {
      loadEvaluacionesPorCurso();
    }
  }, [cursoSeleccionado]);

  useEffect(() => {
    if (evaluacionSeleccionada) {
      loadResultados();
    }
  }, [evaluacionSeleccionada]);

  const loadCursos = async () => {
    try {
      const profesorId = localStorage.getItem("profesorId") || localStorage.getItem("userId") || "";
      if (profesorId) {
        const cursosData = await apiService.listarCursosPorProfesor(profesorId);
        setCursos(cursosData);
        if (cursosData.length > 0 && !cursoSeleccionado) {
          setCursoSeleccionado(cursosData[0].id);
        }
      }
    } catch (e) {
      console.error("Error cargando cursos:", e);
      setError("Error al cargar los cursos");
    }
  };

  const loadEvaluacionesPorCurso = async () => {
    if (!cursoSeleccionado) return;
    try {
      setLoading(true);
      const evaluacionesData = await apiService.listarEvaluacionesPorCurso(cursoSeleccionado);
      setEvaluaciones(evaluacionesData);
      if (evaluacionesData.length > 0 && !evaluacionSeleccionada) {
        setEvaluacionSeleccionada(evaluacionesData[0].id);
      }
    } catch (e) {
      console.error("Error cargando evaluaciones:", e);
      setError("Error al cargar las evaluaciones");
    } finally {
      setLoading(false);
    }
  };

  const loadResultados = async () => {
    if (!evaluacionSeleccionada) return;
    try {
      setLoading(true);
      setError(null);
      // Obtener resultados de todos los estudiantes para esta evaluación
      // Nota: Necesitaríamos un endpoint que devuelva todos los resultados de una evaluación
      // Por ahora, usamos el endpoint existente que requiere estudianteId
      // Esto es una limitación que debería mejorarse en el futuro
      setResultados({});
    } catch (e: unknown) {
      const errorMessage = e instanceof Error ? e.message : "Error al cargar los resultados";
      setError(errorMessage);
      console.error("Error:", e);
    } finally {
      setLoading(false);
    }
  };

  const handleAbrirRetroalimentacion = (estudianteId: string, estudianteNombre: string) => {
    if (!evaluacionSeleccionada) return;
    const evaluacion = evaluaciones.find((e) => e.id === evaluacionSeleccionada);
    if (!evaluacion) return;

    setRetroalimentacionModal({
      isOpen: true,
      estudianteId,
      estudianteNombre,
      evaluacionId: evaluacionSeleccionada,
      evaluacionTitulo: evaluacion.titulo,
    });
  };

  // Mostrar estado de carga o error si aplica
  if (isLoading) {
    console.log("Cargando...");
  }
  if (errorMsg) {
    console.log("Error:", errorMsg);
  }

  // Exponer función para uso futuro
  void handleAbrirRetroalimentacion;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Evaluaciones y Retroalimentación</h1>
          <p className="text-gray-600 mt-1">
            Gestiona evaluaciones y genera retroalimentación automática para tus estudiantes
          </p>
        </div>
      </div>

      {/* Selectores */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Curso
            </label>
            <select
              value={cursoSeleccionado || ""}
              onChange={(e) => {
                setCursoSeleccionado(e.target.value);
                setEvaluacionSeleccionada(null);
                setEvaluaciones([]);
                setResultados({});
              }}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500"
            >
              <option value="">Selecciona un curso</option>
              {cursos.map((curso) => (
                <option key={curso.id} value={curso.id}>
                  {curso.nombre}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Evaluación
            </label>
            <select
              value={evaluacionSeleccionada || ""}
              onChange={(e) => setEvaluacionSeleccionada(e.target.value)}
              disabled={!cursoSeleccionado || evaluaciones.length === 0}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
            >
              <option value="">Selecciona una evaluación</option>
              {evaluaciones.map((evaluacion) => (
                <option key={evaluacion.id} value={evaluacion.id}>
                  {evaluacion.titulo}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Información de la evaluación */}
      {evaluacionSeleccionada && (
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-semibold text-gray-900">
              {evaluaciones.find((e) => e.id === evaluacionSeleccionada)?.titulo}
            </h2>
          </div>
          <p className="text-gray-600">
            {evaluaciones.find((e) => e.id === evaluacionSeleccionada)?.descripcion || "Sin descripción"}
          </p>
          <div className="mt-4 text-sm text-gray-500">
            <p>
              Para generar retroalimentación automática, selecciona un estudiante que haya completado esta evaluación.
            </p>
            <p className="mt-2">
              <strong>Nota:</strong> Esta funcionalidad requiere que el estudiante haya completado al menos un intento de la evaluación.
            </p>
          </div>
        </div>
      )}

      {/* Modal de Retroalimentación */}
      <RetroalimentacionModal
        isOpen={retroalimentacionModal.isOpen}
        onClose={() => setRetroalimentacionModal({ ...retroalimentacionModal, isOpen: false })}
        estudianteId={retroalimentacionModal.estudianteId}
        estudianteNombre={retroalimentacionModal.estudianteNombre}
        evaluacionId={retroalimentacionModal.evaluacionId}
        evaluacionTitulo={retroalimentacionModal.evaluacionTitulo}
      />
    </div>
  );
};

export default EvaluacionesPage;

