import { useEffect, useState } from "react";
import { apiService } from "../../services/api";
import type { Curso } from "../../types";

const CursosPage = () => {
  const [cursos, setCursos] = useState<Curso[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadCursos();
  }, []);

  const loadCursos = async () => {
    try {
      setLoading(true);
      setError(null);
      // Usamos el método getMisCursos que ya maneja la obtención del ID del localStorage
      const cursosData = await apiService.getMisCursos();
      setCursos(cursosData);
    } catch (e) {
      console.error("Error cargando cursos:", e);
      setError("Error al cargar los cursos. Por favor, intenta de nuevo.");
    } finally {
      setLoading(false);
    }
  };

  const formatFecha = (fecha?: string) => {
    if (!fecha) return "Sin fecha";
    return new Date(fecha).toLocaleDateString("es-ES", {
      year: "numeric",
      month: "long",
      day: "numeric",
    });
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-center">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mb-4"></div>
          <p className="text-gray-600">Cargando tus cursos...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 text-red-700 px-6 py-4 rounded-lg">
        <div className="flex items-center gap-3">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span>{error}</span>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Gestión de Cursos</h1>
          <p className="text-gray-500 mt-1">
            Cursos asignados ({cursos.length})
          </p>
        </div>
        {/* Aquí podríamos agregar un botón para crear curso si fuera necesario, 
            pero la gestión principal es desde el admin de Django */}
      </div>

      {/* Cursos Grid */}
      {cursos.length === 0 ? (
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
          <div className="max-w-md mx-auto">
            <svg className="w-16 h-16 mx-auto text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">
              No tienes cursos asignados
            </h3>
            <p className="text-gray-500">
              Contacta con el administrador para que te asigne a los cursos correspondientes.
            </p>
          </div>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {cursos.map((curso) => (
            <div
              key={curso.id}
              className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden hover:shadow-lg transition-shadow duration-300 group"
            >
              {/* Imagen de portada */}
              <div className="relative h-48 bg-gradient-to-br from-green-500 to-teal-600 overflow-hidden">
                {curso.imagenPortada ? (
                  <img
                    src={curso.imagenPortada}
                    alt={curso.nombre}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                  />
                ) : (
                  <div className="w-full h-full flex items-center justify-center">
                    <svg className="w-20 h-20 text-white opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                    </svg>
                  </div>
                )}
                {/* Badge de estado */}
                <div className="absolute top-3 right-3">
                  <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
                    curso.activo 
                      ? "bg-green-100 text-green-800" 
                      : "bg-gray-100 text-gray-800"
                  }`}>
                    {curso.activo ? "Activo" : "Inactivo"}
                  </span>
                </div>
              </div>

              {/* Contenido */}
              <div className="p-6">
                {/* Código del curso */}
                <div className="text-xs font-semibold text-green-600 mb-2 uppercase tracking-wide">
                  {curso.codigoCurso}
                </div>

                {/* Nombre del curso */}
                <h3 className="text-xl font-bold text-gray-900 mb-3 line-clamp-2 min-h-[3.5rem]">
                  {curso.nombre}
                </h3>

                {/* Descripción */}
                {curso.descripcion && (
                  <p className="text-gray-600 text-sm mb-4 line-clamp-3 min-h-[4rem]">
                    {curso.descripcion}
                  </p>
                )}

                {/* Información adicional */}
                <div className="space-y-2 text-sm text-gray-500 border-t border-gray-100 pt-4">
                  <div className="flex items-center gap-2">
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                    <span className="text-xs">
                      Creado: {formatFecha(curso.fechaCreacion)}
                    </span>
                  </div>
                </div>

                {/* Botón de acción */}
                <button className="mt-4 w-full bg-green-600 hover:bg-green-700 text-white font-semibold py-2.5 px-4 rounded-lg transition-colors duration-200 flex items-center justify-center gap-2">
                  <span>Gestionar Curso</span>
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                  </svg>
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default CursosPage;
