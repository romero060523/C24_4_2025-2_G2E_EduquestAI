import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import {
  Home,
  BookOpen,
  Target,
  Users,
  BarChart3,
  Sparkles,
  LogOut,
} from "lucide-react";

const SidebarEstudiante = () => {
  const { usuario, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const getInitials = (nombre: string) => {
    return nombre
      .split(" ")
      .map((word) => word[0])
      .join("")
      .toUpperCase()
      .substring(0, 2);
  };
  return (
    <aside className="bg-white border-r border-gray-200 w-64 min-h-screen flex flex-col">
      {/* Header */}
      <div className="p-6 border-b border-gray-100">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 bg-blue-500 rounded-lg flex items-center justify-center text-white">
            🎓
          </div>
          <span className="text-xl font-bold text-gray-800">EduQuest</span>
          <span className="ml-auto bg-green-50 text-blue-600 px-2 py-1 text-xs font-medium rounded">
            Estudiante
          </span>
        </div>
      </div>
      {/* Profile Section */}
      <div className="p-6">
        <div className="bg-white rounded-xl border border-gray-200 p-4 shadow-sm">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 bg-blue-500 rounded-full flex items-center justify-center text-white font-semibold text-lg flex-shrink-0">
              {usuario ? getInitials(usuario.nombre) : "MG"}
            </div>
            <div className="flex-1 min-w-0">
              <div className="font-semibold text-gray-900 text-sm truncate">
                {usuario ? usuario.nombre : "Prof. María González"}
              </div>
              <div className="text-xs text-gray-500 mt-0.5">4 cursos</div>
            </div>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-4">
        <NavLink
          to="inicio"
          className={({ isActive }) =>
            `flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-colors ${
              isActive
                ? "bg-green-50 text-blue-700"
                : "text-gray-600 hover:bg-gray-50"
            }`
          }
        >
          <Home size={20} />
          <span>Inicio</span>
        </NavLink>

        <NavLink
          to="cursos"
          className={({ isActive }) =>
            `flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-colors ${
              isActive
                ? "bg-green-50 text-blue-700"
                : "text-gray-600 hover:bg-gray-50"
            }`
          }
        >
          <BookOpen size={20} />
          <span>Mis Cursos</span>
        </NavLink>

        <NavLink
          to="misiones"
          className={({ isActive }) =>
            `flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-colors ${
              isActive
                ? "bg-green-50 text-blue-700"
                : "text-gray-600 hover:bg-gray-50"
            }`
          }
        >
          <Target size={20} />
          <span>Misiones</span>
        </NavLink>

        <NavLink
          to="recompensas"
          className={({ isActive }) =>
            `flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-colors ${
              isActive
                ? "bg-green-50 text-blue-700"
                : "text-gray-600 hover:bg-gray-50"
            }`
          }
        >
          <Users size={20} />
          <span>Recompensas</span>
        </NavLink>

        <NavLink
          to="ranking"
          className={({ isActive }) =>
            `flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-colors ${
              isActive
                ? "bg-green-50 text-blue-700"
                : "text-gray-600 hover:bg-gray-50"
            }`
          }
        >
          <BarChart3 size={20} />
          <span>Ranking</span>
        </NavLink>

        <NavLink
          to="perfil"
          className={({ isActive }) =>
            `flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-colors ${
              isActive
                ? "bg-green-50 text-blue-700"
                : "text-gray-600 hover:bg-gray-50"
            }`
          }
        >
          <Sparkles size={20} />
          <span>Perfil</span>
        </NavLink>
      </nav>

      {/* Logout Button */}
      <div className="p-4 border-t border-gray-100">
        <button
          onClick={handleLogout}
          className="flex items-center gap-3 px-4 py-3 text-sm font-medium text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors w-full"
        >
          <LogOut size={20} />
          <span>Cerrar sesión</span>
        </button>
      </div>
    </aside>
  );
};

export default SidebarEstudiante;
