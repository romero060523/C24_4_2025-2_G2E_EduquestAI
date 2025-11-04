import { useEffect, useState } from "react";
import { Loader, Search } from "lucide-react";
import API from "../services/api";
import type { Curso, User } from "../types";

interface AsignarEstudiantesFormProps {
  open: boolean;
  curso: Curso;
  onClose: () => void;
  onSaved: () => void;
}

interface Inscripcion {
  id: string;
  estudiante: User;
  estado: string;
  fecha_inscripcion: string;
}

export default function AsignarEstudiantesForm({
  open,
  curso,
  onClose,
  onSaved,
}: AsignarEstudiantesFormProps) {
  const [estudiantes, setEstudiantes] = useState<User[]>([]);
  const [estudiantesInscritos, setEstudiantesInscritos] = useState<
    Inscripcion[]
  >([]);
  const [estudiantesSeleccionados, setEstudiantesSeleccionados] = useState<
    string[]
  >([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (open && curso?.id) {
      fetchData();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open]);

  const fetchData = async () => {
    try {
      setLoadingData(true);

      // Obtener estudiantes disponibles
      const estResponse = await API.get("/users/", {
        params: { rol: "estudiante", activo: true },
      });
      setEstudiantes(estResponse.data);

      // Obtener estudiantes ya inscritos
      const inscritosResponse = await API.get(
        `/cursos/${curso.id}/estudiantes/`
      );
      setEstudiantesInscritos(inscritosResponse.data.data || []);
    } catch (err) {
      console.error(err);
      setError("Error al cargar datos");
    } finally {
      setLoadingData(false);
    }
  };

  const handleToggleEstudiante = (estudianteId: string) => {
    setEstudiantesSeleccionados((prev) =>
      prev.includes(estudianteId)
        ? prev.filter((id) => id !== estudianteId)
        : [...prev, estudianteId]
    );
  };

  const handleInscribir = async () => {
    if (estudiantesSeleccionados.length === 0) {
      setError("Debe seleccionar al menos un estudiante");
      return;
    }

    setLoading(true);
    setError(null);

    try {
      await API.post(`/cursos/${curso.id}/inscribir_estudiantes/`, {
        estudiantes_ids: estudiantesSeleccionados,
      });

      onSaved();
      setEstudiantesSeleccionados([]);
      fetchData(); // Recargar la lista
    } catch (err: any) {
      const errorMsg =
        err.response?.data?.message || "Error al inscribir estudiantes";
      setError(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  const handleEliminar = async (inscripcionId: string) => {
    if (!confirm("¿Eliminar esta inscripción?")) return;

    try {
      await API.delete(`/inscripciones/${inscripcionId}/`);
      fetchData();
      onSaved();
    } catch (err) {
      console.error(err);
      setError("Error al eliminar inscripción");
    }
  };

  if (!open) return null;

  // Filtrar estudiantes ya inscritos
  const estudiantesDisponibles = estudiantes.filter(
    (est) => !estudiantesInscritos.some((ins) => ins?.estudiante?.id === est.id)
  );

  // Filtrar por búsqueda
  const estudiantesFiltrados = estudiantesDisponibles.filter(
    (est) =>
      est.nombre_completo.toLowerCase().includes(searchQuery.toLowerCase()) ||
      est.email.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center backdrop-blur-sm p-4">
      <div className="w-full max-w-3xl rounded-lg bg-white p-6 shadow-lg max-h-[90vh] overflow-y-auto">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-xl font-semibold text-gray-800">
            Asignar estudiantes
          </h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700"
          >
            ✕
          </button>
        </div>

        <p className="text-sm text-gray-600 mb-6">{curso.nombre}</p>

        {error && (
          <div className="mb-4 rounded border border-red-200 bg-red-50 p-3 text-red-700 text-sm">
            {error}
          </div>
        )}

        {loadingData ? (
          <div className="flex items-center justify-center py-8">
            <Loader className="w-6 h-6 text-blue-600 animate-spin" />
            <span className="ml-2 text-gray-600">Cargando...</span>
          </div>
        ) : (
          <>
            {/* Selección de estudiantes */}
            <div className="mb-6 p-4 bg-gray-50 rounded-lg">
              <div className="flex items-center justify-between mb-3">
                <h3 className="text-sm font-semibold text-gray-700">
                  Estudiantes disponibles ({estudiantesDisponibles.length})
                </h3>
                <span className="text-sm text-gray-600">
                  {estudiantesSeleccionados.length} seleccionados
                </span>
              </div>

              {/* Búsqueda */}
              <div className="relative mb-3">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
                <input
                  type="text"
                  placeholder="Buscar por nombre o email..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>

              {/* Lista de estudiantes */}
              <div className="max-h-64 overflow-y-auto space-y-2 mb-3">
                {estudiantesFiltrados.length === 0 ? (
                  <p className="text-sm text-gray-500 text-center py-4">
                    {searchQuery
                      ? "No se encontraron estudiantes"
                      : "No hay estudiantes disponibles"}
                  </p>
                ) : (
                  estudiantesFiltrados.map((estudiante) => (
                    <label
                      key={estudiante.id}
                      className="flex items-center p-3 border rounded-lg hover:bg-white cursor-pointer"
                    >
                      <input
                        type="checkbox"
                        checked={estudiantesSeleccionados.includes(
                          estudiante.id
                        )}
                        onChange={() => handleToggleEstudiante(estudiante.id)}
                        className="mr-3"
                      />
                      <div className="flex-1">
                        <p className="font-medium text-gray-900">
                          {estudiante.nombre_completo}
                        </p>
                        <p className="text-sm text-gray-600">
                          {estudiante.email}
                        </p>
                      </div>
                    </label>
                  ))
                )}
              </div>

              <button
                onClick={handleInscribir}
                disabled={loading || estudiantesSeleccionados.length === 0}
                className="w-full rounded bg-green-600 px-4 py-2 font-semibold text-white hover:bg-green-700 disabled:opacity-50"
              >
                {loading
                  ? "Inscribiendo..."
                  : `Inscribir ${estudiantesSeleccionados.length} estudiante(s)`}
              </button>
            </div>

            {/* Lista de estudiantes inscritos */}
            <div>
              <h3 className="text-sm font-semibold text-gray-700 mb-3">
                Estudiantes inscritos ({estudiantesInscritos.length})
              </h3>
              {estudiantesInscritos.length === 0 ? (
                <p className="text-sm text-gray-500 text-center py-4">
                  No hay estudiantes inscritos
                </p>
              ) : (
                <div className="space-y-2 max-h-64 overflow-y-auto">
                  {estudiantesInscritos.map((inscripcion) => (
                    <div
                      key={inscripcion.id}
                      className="flex items-center justify-between p-3 border rounded-lg hover:bg-gray-50"
                    >
                      <div className="flex-1">
                        <p className="font-medium text-gray-900">
                          {inscripcion.estudiante?.nombre_completo ||
                            "Sin nombre"}
                        </p>
                        <p className="text-sm text-gray-600">
                          {inscripcion.estudiante?.email || "Sin email"}
                        </p>
                      </div>
                      <div className="flex items-center gap-2">
                        <span className="px-3 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">
                          {inscripcion.estado}
                        </span>
                        <button
                          onClick={() => handleEliminar(inscripcion.id)}
                          className="text-red-600 hover:text-red-800 text-sm font-medium"
                        >
                          Eliminar
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </>
        )}

        <div className="mt-6 flex justify-end">
          <button
            onClick={onClose}
            className="rounded border px-4 py-2 text-gray-700 hover:bg-gray-50"
          >
            Cerrar
          </button>
        </div>
      </div>
    </div>
  );
}
