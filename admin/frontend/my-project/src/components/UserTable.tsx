import { useEffect, useState, useMemo } from "react";
import { Plus, Edit2, Trash2, Loader, AlertCircle, Search, X } from "lucide-react";
import API from "../services/api";
import type { User } from "../types";
import UserForm from "./UserForm";

export default function UserTable() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [open, setOpen] = useState(false);
  const [mode, setMode] = useState<"create" | "edit">("create");
  const [selected, setSelected] = useState<User | null>(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [filterRole, setFilterRole] = useState("all");
  const [filterStatus, setFilterStatus] = useState("all");


  const fetchUsers = async () => {
    try {
      const res = await API.get("/users/");
      setUsers(res.data);
    } catch (err) {
      console.error(err);
      setError("Error al obtener usuarios");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const onCreate = () => {
    setMode("create");
    setSelected(null);
    setOpen(true);
  };

  const onEdit = (u: User) => {
    setMode("edit");
    setSelected(u);
    setOpen(true);
  };

  const onDelete = async (u: User) => {
    if (!confirm(`¿Eliminar usuario ${u.nombre_completo}?`)) return;
    try {
      await API.delete(`/users/${u.id}/`);
      fetchUsers();
    } catch (err) {
      console.error(err);
      setError("No se pudo eliminar el usuario");
    }
  };

  const getRoleBadge = (role: string) => {
    const colors: Record<string, { bg: string; text: string; label: string }> = {
      administrador: { bg: "bg-purple-100", text: "text-purple-800", label: "Administrador" },
      profesor: { bg: "bg-blue-100", text: "text-blue-800", label: "Profesor" },
      estudiante: { bg: "bg-green-100", text: "text-green-800", label: "Estudiante" },
    };
    const color = colors[role.toLowerCase()] || { bg: "bg-gray-100", text: "text-gray-800", label: role };
    return color;
  };

  const getStatusBadge = (isActive: boolean) => {
    return isActive
      ? { bg: "bg-emerald-100", text: "text-emerald-700", label: "Activo" }
      : { bg: "bg-red-100", text: "text-red-700", label: "Inactivo" };
  };

  // Filtrar usuarios según búsqueda y filtros
  const filteredUsers = useMemo(() => {
    return users.filter(user => {
      // Filtro de búsqueda por nombre o email
      const matchesSearch = searchQuery === "" || 
        user.nombre_completo.toLowerCase().includes(searchQuery.toLowerCase()) ||
        user.username.toLowerCase().includes(searchQuery.toLowerCase()) ||
        user.email.toLowerCase().includes(searchQuery.toLowerCase());

      // Filtro por rol
      const matchesRole = filterRole === "all" || user.rol === filterRole;

      // Filtro por estado
      const matchesStatus = filterStatus === "all" || 
        (filterStatus === "active" && user.activo) ||
        (filterStatus === "inactive" && !user.activo);

      return matchesSearch && matchesRole && matchesStatus;
    });
  }, [users, searchQuery, filterRole, filterStatus]);

  const clearFilters = () => {
    setSearchQuery("");
    setFilterRole("all");
    setFilterStatus("all");
  };

  const hasActiveFilters = searchQuery !== "" || filterRole !== "all" || filterStatus !== "all";

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <Loader className="w-8 h-8 text-blue-600 animate-spin" />
        <span className="ml-2 text-gray-600 font-medium">Cargando usuarios...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-lg bg-red-50 border border-red-200 p-4 flex items-center gap-3">
        <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0" />
        <p className="text-red-700 font-medium">{error}</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      {/* Búsqueda y Filtros */}
      <div className="bg-white rounded-lg border border-gray-200 p-4">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          {/* Barra de búsqueda */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por nombre o email..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          {/* Filtro por rol */}
          <div>
            <select
              value={filterRole}
              onChange={(e) => setFilterRole(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="all">Todos los roles</option>
              <option value="administrador">Administrador</option>
              <option value="profesor">Profesor</option>
              <option value="estudiante">Estudiante</option>
            </select>
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
      
      {/* Contador y Botón Nuevo Usuario */}
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm text-gray-500 mt-1">
            {filteredUsers.length} de {users.length} {users.length === 1 ? "usuario" : "usuarios"}
            {hasActiveFilters && " (filtrados)"}
          </p>
        </div>
        <button
          onClick={onCreate}
          className="inline-flex items-center gap-2 rounded-lg bg-gradient-to-r from-blue-600 to-blue-700 px-4 py-2.5 text-white font-semibold hover:from-blue-700 hover:to-blue-800 transition-all duration-200 shadow-md hover:shadow-lg active:scale-95"
        >
          <Plus className="w-5 h-5" />
          Nuevo usuario
        </button>
      </div>

      {/* Table Container */}
      <div className="rounded-xl border border-gray-200 bg-white shadow-sm overflow-hidden">
        {filteredUsers.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-16">
            <div className="text-gray-300 text-6xl mb-4">
              {hasActiveFilters ? "�" : "�👥"}
            </div>
            <p className="text-gray-600 font-semibold text-lg">
              {hasActiveFilters ? "No se encontraron usuarios" : "No hay usuarios"}
            </p>
            <p className="text-gray-500 text-sm mt-1">
              {hasActiveFilters 
                ? "Intenta ajustar los filtros de búsqueda" 
                : "Crea el primero haciendo clic en \"Nuevo usuario\""
              }
            </p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-200 bg-gradient-to-r from-gray-50 to-gray-100">
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">Usuario</th>
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">Nombre</th>
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">Correo</th>
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">Rol</th>
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">Estado</th>
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">Fecha creación</th>
                  <th className="px-6 py-4 text-right font-semibold text-gray-900">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {filteredUsers.map((u) => {
                  const roleBadge = getRoleBadge(u.rol);
                  const statusBadge = getStatusBadge(u.activo);
                  return (
                    <tr
                      key={u.id}
                      className="hover:bg-blue-50 transition-colors duration-150 group"
                    >
                      <td className="px-6 py-4 text-gray-600 font-medium">{u.username}</td>
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-3">
                          <div className="flex-shrink-0 w-9 h-9 rounded-full bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center">
                            <span className="text-white font-bold text-sm">
                              {u.nombre_completo.charAt(0).toUpperCase()}
                            </span>
                          </div>
                          <span className="font-semibold text-gray-900">{u.nombre_completo}</span>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-gray-600">{u.email}</td>
                      <td className="px-6 py-4">
                        <span className={`inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold ${roleBadge.bg} ${roleBadge.text}`}>
                          {roleBadge.label}
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <span className={`inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold ${statusBadge.bg} ${statusBadge.text}`}>
                          {statusBadge.label}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-gray-600">
                        {new Date(u.fecha_creacion).toLocaleDateString("es-ES", {
                          year: "numeric",
                          month: "short",
                          day: "numeric",
                        })}
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity duration-150">
                          <button
                            onClick={() => onEdit(u)}
                            className="inline-flex items-center gap-1.5 rounded-lg bg-blue-50 px-3 py-2 text-blue-700 hover:bg-blue-100 transition-colors duration-150 font-medium text-sm"
                            title="Editar usuario"
                          >
                            <Edit2 className="w-4 h-4" />
                            Editar
                          </button>
                          <button
                            onClick={() => onDelete(u)}
                            className="inline-flex items-center gap-1.5 rounded-lg bg-red-50 px-3 py-2 text-red-700 hover:bg-red-100 transition-colors duration-150 font-medium text-sm"
                            title="Eliminar usuario"
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

      <UserForm
        open={open}
        mode={mode}
        initial={selected}
        onClose={() => setOpen(false)}
        onSaved={fetchUsers}
      />
    </div>
  );
}
