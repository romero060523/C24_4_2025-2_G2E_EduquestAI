import { useEffect, useState } from "react";
import { Loader } from "lucide-react";
import API from "../services/api";
import type { Curso, User } from "../types";

interface AsignarProfesorFormProps {
  open: boolean;
  curso: Curso;
  onClose: () => void;
  onSaved: () => void;
}

interface ProfesorAsignado {
  id: string;
  profesor: User;
  rol_profesor: "titular" | "asistente";
  fecha_asignacion: string;
}

export default function AsignarProfesorForm({
  open,
  curso,
  onClose,
  onSaved,
}: AsignarProfesorFormProps) {
  const [profesores, setProfesores] = useState<User[]>([]);
  const [profesoresAsignados, setProfesoresAsignados] = useState<
    ProfesorAsignado[]
  >([]);
  const [profesorSeleccionado, setProfesorSeleccionado] = useState("");
  const [rolProfesor, setRolProfesor] = useState<"titular" | "asistente">(
    "titular"
  );
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

      // Obtener profesores disponibles
      const profResponse = await API.get("/users/", {
        params: { rol: "profesor", activo: true },
      });
      setProfesores(profResponse.data);

      // Obtener profesores ya asignados
      const asignadosResponse = await API.get(
        `/cursos/${curso.id}/profesores/`
      );
      setProfesoresAsignados(asignadosResponse.data.data || []);
    } catch (err) {
      console.error(err);
      setError("Error al cargar datos");
    } finally {
      setLoadingData(false);
    }
  };

  const handleAsignar = async () => {
    if (!profesorSeleccionado) {
      setError("Debe seleccionar un profesor");
      return;
    }

    setLoading(true);
    setError(null);

    try {
      await API.post(`/cursos/${curso.id}/asignar_profesores/`, {
        profesores: [
          {
            profesor_id: profesorSeleccionado,
            rol_profesor: rolProfesor,
          },
        ],
      });

      onSaved();
      setProfesorSeleccionado("");
      setRolProfesor("titular");
      fetchData(); // Recargar la lista
    } catch (err: any) {
      const errorMsg =
        err.response?.data?.message || "Error al asignar profesor";
      setError(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  const handleEliminar = async (asignacionId: string) => {
    if (!confirm("¿Eliminar esta asignación?")) return;

    try {
      await API.delete(`/cursos-profesores/${asignacionId}/`);
      fetchData();
      onSaved();
    } catch (err) {
      console.error(err);
      setError("Error al eliminar asignación");
    }
  };

  if (!open) return null;

  // Filtrar profesores ya asignados
  const profesoresDisponibles = profesores.filter(
    (prof) =>
      !profesoresAsignados.some((asig) => asig?.profesor?.id === prof.id)
  );

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center backdrop-blur-sm p-4">
      <div className="w-full max-w-2xl rounded-lg bg-white p-6 shadow-lg max-h-[90vh] overflow-y-auto">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-xl font-semibold text-gray-800">
            Asignar profesor
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
            {/* Formulario para asignar nuevo profesor */}
            <div className="mb-6 p-4 bg-gray-50 rounded-lg">
              <h3 className="text-sm font-semibold text-gray-700 mb-3">
                Asignar nuevo profesor
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                <div className="md:col-span-2">
                  <select
                    value={profesorSeleccionado}
                    onChange={(e) => setProfesorSeleccionado(e.target.value)}
                    className="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
                  >
                    <option value="">Seleccionar profesor...</option>
                    {profesoresDisponibles.map((prof) => (
                      <option key={prof.id} value={prof.id}>
                        {prof.nombre_completo} ({prof.email})
                      </option>
                    ))}
                  </select>
                </div>
                <div>
                  <select
                    value={rolProfesor}
                    onChange={(e) =>
                      setRolProfesor(e.target.value as "titular" | "asistente")
                    }
                    className="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
                  >
                    <option value="titular">Titular</option>
                    <option value="asistente">Asistente</option>
                  </select>
                </div>
              </div>
              <button
                onClick={handleAsignar}
                disabled={loading || !profesorSeleccionado}
                className="mt-3 w-full rounded bg-blue-600 px-4 py-2 font-semibold text-white hover:bg-blue-700 disabled:opacity-50"
              >
                {loading ? "Asignando..." : "Asignar profesor"}
              </button>
            </div>

            {/* Lista de profesores asignados */}
            <div>
              <h3 className="text-sm font-semibold text-gray-700 mb-3">
                Profesores asignados ({profesoresAsignados.length})
              </h3>
              {profesoresAsignados.length === 0 ? (
                <p className="text-sm text-gray-500 text-center py-4">
                  No hay profesores asignados
                </p>
              ) : (
                <div className="space-y-2">
                  {profesoresAsignados.map((asignacion) => (
                    <div
                      key={asignacion.id}
                      className="flex items-center justify-between p-3 border rounded-lg hover:bg-gray-50"
                    >
                      <div className="flex-1">
                        <p className="font-medium text-gray-900">
                          {asignacion.profesor?.nombre_completo || "Sin nombre"}
                        </p>
                        <p className="text-sm text-gray-600">
                          {asignacion.profesor?.email || "Sin email"}
                        </p>
                      </div>
                      <div className="flex items-center gap-2">
                        <span
                          className={`px-3 py-1 text-xs font-semibold rounded-full ${
                            asignacion.rol_profesor === "titular"
                              ? "bg-purple-100 text-purple-800"
                              : "bg-blue-100 text-blue-800"
                          }`}
                        >
                          {asignacion.rol_profesor === "titular"
                            ? "Titular"
                            : "Asistente"}
                        </span>
                        <button
                          onClick={() => handleEliminar(asignacion.id)}
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
