import { useEffect, useState, useMemo } from "react";
import {
  Plus,
  Edit2,
  Trash2,
  Loader,
  AlertCircle,
  Search,
  X,
  UserPlus,
  Users,
} from "lucide-react";
import { getCursos, deleteCurso } from "../services/cursosApi";
import type { Curso } from "../types";
import CursoForm from "./CursoForm";
import AsignarProfesorForm from "./AsignarProfesorForm";
import AsignarEstudiantesForm from "./AsignarEstudiantesForm";

export default function CursoTable() {
  const [cursos, setCursos] = useState<Curso[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [open, setOpen] = useState(false);
  const [mode, setMode] = useState<"create" | "edit">("create");
  const [selected, setSelected] = useState<Curso | null>(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [filterStatus, setFilterStatus] = useState("all");

  // Estados para modales de asignación
  const [showAsignarProfesor, setShowAsignarProfesor] = useState(false);
  const [showAsignarEstudiantes, setShowAsignarEstudiantes] = useState(false);
  const [cursoSeleccionado, setCursoSeleccionado] = useState<Curso | null>(
    null
  );

  const fetchCursos = async () => {
    try {
      const data = await getCursos();
      setCursos(data);
    } catch (err) {
      console.error(err);
      setError("Error al obtener cursos");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCursos();
  }, []);

  const onCreate = () => {
    setMode("create");
    setSelected(null);
    setOpen(true);
  };

  const onEdit = (curso: Curso) => {
    setMode("edit");
    setSelected(curso);
    setOpen(true);
  };

  const onDelete = async (curso: Curso) => {
    if (!confirm(`¿Eliminar curso ${curso.nombre}?`)) return;
    try {
      await deleteCurso(curso.id);
      fetchCursos();
    } catch (err) {
      console.error(err);
      setError("No se pudo eliminar el curso");
    }
  };

  const onAsignarProfesor = (curso: Curso) => {
    setCursoSeleccionado(curso);
    setShowAsignarProfesor(true);
  };

  const onAsignarEstudiantes = (curso: Curso) => {
    setCursoSeleccionado(curso);
    setShowAsignarEstudiantes(true);
  };

  const getStatusBadge = (isActive: boolean) => {
    return isActive
      ? { bg: "bg-emerald-100", text: "text-emerald-700", label: "Activo" }
      : { bg: "bg-red-100", text: "text-red-700", label: "Inactivo" };
  };

  // Filtrar cursos según búsqueda y filtros
  const filteredCursos = useMemo(() => {
    return cursos.filter((curso) => {
      // Filtro de búsqueda por código o nombre
      const matchesSearch =
        searchQuery === "" ||
        curso.nombre.toLowerCase().includes(searchQuery.toLowerCase()) ||
        curso.codigo_curso.toLowerCase().includes(searchQuery.toLowerCase());

      // Filtro por estado
      const matchesStatus =
        filterStatus === "all" ||
        (filterStatus === "active" && curso.activo) ||
        (filterStatus === "inactive" && !curso.activo);

      return matchesSearch && matchesStatus;
    });
  }, [cursos, searchQuery, filterStatus]);

  const clearFilters = () => {
    setSearchQuery("");
    setFilterStatus("all");
  };

  const hasActiveFilters = searchQuery !== "" || filterStatus !== "all";

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <Loader className="w-8 h-8 text-blue-600 animate-spin" />
        <span className="ml-2 text-gray-600 font-medium">
          Cargando cursos...
        </span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-lg bg-red-50 border border-red-200 p-4 flex items-center gap-3">
        <AlertCircle className="w-5 h-5 text-red-600 shrink-0" />
        <p className="text-red-700 font-medium">{error}</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Búsqueda y Filtros */}
      <div className="bg-white rounded-lg border border-gray-200 p-4">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          {/* Barra de búsqueda */}
          <div className="relative md:col-span-2">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por código o nombre..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          {/* Filtro por estado */}
          <div>
            <select
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="all">Todos los estados</option>
              <option value="active">Activos</option>
              <option value="inactive">Inactivos</option>
            </select>
          </div>

          {/* Limpiar filtros */}
          <div className="flex items-center">
            {hasActiveFilters && (
              <button
                onClick={clearFilters}
                className="inline-flex items-center gap-2 px-3 py-2 text-sm font-medium text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
              >
                <X className="w-4 h-4" />
                Limpiar
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Contador y Botón Nuevo Curso */}
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm text-gray-500 mt-1">
            {filteredCursos.length} de {cursos.length}{" "}
            {cursos.length === 1 ? "curso" : "cursos"}
            {hasActiveFilters && " (filtrados)"}
          </p>
        </div>
        <button
          onClick={onCreate}
          className="inline-flex items-center gap-2 rounded-lg bg-linear-to-r from-blue-600 to-blue-700 px-4 py-2.5 text-white font-semibold hover:from-blue-700 hover:to-blue-800 transition-all duration-200 shadow-md hover:shadow-lg active:scale-95"
        >
          <Plus className="w-5 h-5" />
          Nuevo curso
        </button>
      </div>

      {/* Table Container */}
      <div className="rounded-xl border border-gray-200 bg-white shadow-sm overflow-hidden">
        {filteredCursos.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-16">
            <div className="text-gray-300 text-6xl mb-4">
              {hasActiveFilters ? "🔍" : "📚"}
            </div>
            <p className="text-gray-600 font-semibold text-lg">
              {hasActiveFilters ? "No se encontraron cursos" : "No hay cursos"}
            </p>
            <p className="text-gray-500 text-sm mt-1">
              {hasActiveFilters
                ? "Intenta ajustar los filtros de búsqueda"
                : 'Crea el primero haciendo clic en "Nuevo curso"'}
            </p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-200 bg-linear-to-r from-gray-50 to-gray-100">
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">
                    Código
                  </th>
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">
                    Nombre
                  </th>
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">
                    Descripción
                  </th>
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">
                    Fecha Inicio
                  </th>
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">
                    Fecha Fin
                  </th>
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">
                    Estado
                  </th>
                  <th className="px-6 py-4 text-right font-semibold text-gray-900">
                    Acciones
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {filteredCursos.map((curso) => {
                  const statusBadge = getStatusBadge(curso.activo);
                  return (
                    <tr
                      key={curso.id}
                      className="hover:bg-blue-50 transition-colors duration-150 group"
                    >
                      <td className="px-6 py-4">
                        <span className="font-mono text-sm font-semibold text-blue-600 bg-blue-50 px-2 py-1 rounded">
                          {curso.codigo_curso}
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-3">
                          {curso.imagen_portada ? (
                            <img
                              src={curso.imagen_portada}
                              alt={curso.nombre}
                              className="w-10 h-10 rounded object-cover"
                            />
                          ) : (
                            <div className="shrink-0 w-10 h-10 rounded bg-linear-to-br from-blue-400 to-purple-500 flex items-center justify-center">
                              <span className="text-white font-bold text-sm">
                                {curso.nombre.charAt(0).toUpperCase()}
                              </span>
                            </div>
                          )}
                          <span className="font-semibold text-gray-900">
                            {curso.nombre}
                          </span>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-gray-600 max-w-xs truncate">
                        {curso.descripcion || "-"}
                      </td>
                      <td className="px-6 py-4 text-gray-600">
                        {curso.fecha_inicio
                          ? new Date(curso.fecha_inicio).toLocaleDateString(
                              "es-ES",
                              {
                                year: "numeric",
                                month: "short",
                                day: "numeric",
                              }
                            )
                          : "-"}
                      </td>
                      <td className="px-6 py-4 text-gray-600">
                        {curso.fecha_fin
                          ? new Date(curso.fecha_fin).toLocaleDateString(
                              "es-ES",
                              {
                                year: "numeric",
                                month: "short",
                                day: "numeric",
                              }
                            )
                          : "-"}
                      </td>
                      <td className="px-6 py-4">
                        <span
                          className={`inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold ${statusBadge.bg} ${statusBadge.text}`}
                        >
                          {statusBadge.label}
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity duration-150">
                          <button
                            onClick={() => onAsignarProfesor(curso)}
                            className="inline-flex items-center gap-1.5 rounded-lg bg-purple-50 px-3 py-2 text-purple-700 hover:bg-purple-100 transition-colors duration-150 font-medium text-sm"
                            title="Asignar profesor"
                          >
                            <UserPlus className="w-4 h-4" />
                            Profesor
                          </button>
                          <button
                            onClick={() => onAsignarEstudiantes(curso)}
                            className="inline-flex items-center gap-1.5 rounded-lg bg-green-50 px-3 py-2 text-green-700 hover:bg-green-100 transition-colors duration-150 font-medium text-sm"
                            title="Asignar estudiantes"
                          >
                            <Users className="w-4 h-4" />
                            Estudiantes
                          </button>
                          <button
                            onClick={() => onEdit(curso)}
                            className="inline-flex items-center gap-1.5 rounded-lg bg-blue-50 px-3 py-2 text-blue-700 hover:bg-blue-100 transition-colors duration-150 font-medium text-sm"
                            title="Editar curso"
                          >
                            <Edit2 className="w-4 h-4" />
                            Editar
                          </button>
                          <button
                            onClick={() => onDelete(curso)}
                            className="inline-flex items-center gap-1.5 rounded-lg bg-red-50 px-3 py-2 text-red-700 hover:bg-red-100 transition-colors duration-150 font-medium text-sm"
                            title="Eliminar curso"
                          >
                            <Trash2 className="w-4 h-4" />
                            Eliminar
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <CursoForm
        open={open}
        mode={mode}
        initial={selected}
        onClose={() => setOpen(false)}
        onSaved={fetchCursos}
      />

      {/* Modal Asignar Profesor */}
      {showAsignarProfesor && cursoSeleccionado && (
        <AsignarProfesorForm
          open={showAsignarProfesor}
          curso={cursoSeleccionado}
          onClose={() => {
            setShowAsignarProfesor(false);
            setCursoSeleccionado(null);
          }}
          onSaved={fetchCursos}
        />
      )}

      {/* Modal Asignar Estudiantes */}
      {showAsignarEstudiantes && cursoSeleccionado && (
        <AsignarEstudiantesForm
          open={showAsignarEstudiantes}
          curso={cursoSeleccionado}
          onClose={() => {
            setShowAsignarEstudiantes(false);
            setCursoSeleccionado(null);
          }}
          onSaved={fetchCursos}
        />
      )}
    </div>
  );
}
