import { Outlet, Link, useLocation, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import {
  LayoutDashboard,
  Users,
  BookOpen,
  LogOut,
  Settings,
  BarChart3,
  Palette,
} from "lucide-react";
import { useTema } from "../hooks/useTema";

export default function AdminLayout() {
  const location = useLocation();
  const navigate = useNavigate();
  const { tema } = useTema();
  const [logoUrl, setLogoUrl] = useState<string>("");
  const [nombreInstitucion, setNombreInstitucion] = useState<string>("EduQuest");

  useEffect(() => {
    // Cargar configuración desde localStorage o tema
    const cargarConfiguracion = () => {
      const temaGuardado = localStorage.getItem("temaConfig");
      if (temaGuardado) {
        try {
          const config = JSON.parse(temaGuardado);
          setLogoUrl(config.logo_url || "");
          setNombreInstitucion(config.nombre_institucion || "EduQuest");
        } catch (e) {
          console.error("Error cargando configuración:", e);
        }
      } else if (tema) {
        setLogoUrl(tema.logo_url || "");
        setNombreInstitucion(tema.nombre_institucion || "EduQuest");
      }
    };

    cargarConfiguracion();

    // Escuchar cambios en el tema
    const handleTemaActualizado = (event: CustomEvent) => {
      const nuevoTema = event.detail;
      if (nuevoTema) {
        setLogoUrl(nuevoTema.logo_url || "");
        setNombreInstitucion(nuevoTema.nombre_institucion || "EduQuest");
      }
    };

    // Escuchar cambios en localStorage
    const handleStorageChange = () => {
      cargarConfiguracion();
    };

    // Escuchar cambios en atributos de datos del root
    const observer = new MutationObserver(() => {
      const root = document.documentElement;
      const nuevoLogo = root.getAttribute("data-logo-url");
      const nuevoNombre = root.getAttribute("data-nombre-institucion");
      if (nuevoLogo !== null) setLogoUrl(nuevoLogo);
      if (nuevoNombre !== null) setNombreInstitucion(nuevoNombre);
    });

    observer.observe(document.documentElement, {
      attributes: true,
      attributeFilter: ['data-logo-url', 'data-nombre-institucion']
    });

    window.addEventListener('temaActualizado', handleTemaActualizado as EventListener);
    window.addEventListener('storage', handleStorageChange);

    return () => {
      window.removeEventListener('temaActualizado', handleTemaActualizado as EventListener);
      window.removeEventListener('storage', handleStorageChange);
      observer.disconnect();
    };
  }, [tema]);

  const isActive = (path: string) => location.pathname === path;

  const navLinks = [
    {
      path: "/admin/dashboard",
      label: "Dashboard",
      icon: LayoutDashboard,
    },
    {
      path: "/admin/users",
      label: "Usuarios",
      icon: Users,
    },
    {
      path: "/admin/cursos",
      label: "Cursos",
      icon: BookOpen,
    },
    {
      path: "/admin/reglas-gamificacion",
      label: "Reglas",
      icon: Settings,
    },
    {
      path: "/admin/reportes",
      label: "Reportes",
      icon: BarChart3,
    },
    {
      path: "/admin/configuracion-visual",
      label: "Configuración Visual",
      icon: Palette,
    },
  ];

  function handleLogout() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    if (window.confirm("¿Estás seguro que deseas cerrar sesión?")) {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      navigate("/admin/login");
    }
  }
  return (
    <div className="flex min-h-screen bg-gray-50">
      {/* Sidebar */}
      <aside className="w-64 bg-gradient-to-b from-slate-900 to-slate-800 text-white flex flex-col shadow-lg">
        {/* Logo */}
        <div className="p-6 border-b border-slate-700">
          <div className="flex items-center gap-3">
            {logoUrl ? (
              <img
                src={logoUrl}
                alt={nombreInstitucion}
                className="flex-shrink-0 w-10 h-10 object-contain rounded-lg"
                onError={(e) => {
                  // Si el logo falla, mostrar el icono por defecto
                  const target = e.target as HTMLImageElement;
                  target.style.display = "none";
                  const parent = target.parentElement;
                  if (parent && !parent.querySelector('.default-logo-icon')) {
                    const icon = document.createElement('div');
                    icon.className = 'default-logo-icon flex-shrink-0 w-10 h-10 bg-gradient-tema-primario rounded-lg flex items-center justify-center';
                    icon.innerHTML = '<span class="text-white font-bold text-lg">⚡</span>';
                    parent.insertBefore(icon, target);
                  }
                }}
              />
            ) : (
              <div className="flex-shrink-0 w-10 h-10 bg-gradient-tema-primario rounded-lg flex items-center justify-center">
                <span className="text-white font-bold text-lg">⚡</span>
              </div>
            )}
            <span className="text-2xl font-bold bg-gradient-tema-primario bg-clip-text text-transparent">
              {nombreInstitucion}
            </span>
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex-1 p-4 space-y-2">
          {navLinks.map(({ path, label, icon: Icon }) => {
            const active = isActive(path);
            return (
              <Link
                key={path}
                to={path}
                className={`flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-200 ${
                  active
                    ? "bg-gradient-tema-primario text-white shadow-lg"
                    : "text-slate-300 hover:bg-slate-700 hover:text-white"
                }`}
              >
                <Icon className="w-5 h-5 flex-shrink-0" />
                <span className="font-medium">{label}</span>
              </Link>
            );
          })}
        </nav>

        {/* Footer */}
        <div className="p-4 border-t border-slate-700">
          <button
            onClick={handleLogout}
            className="w-full flex items-center gap-3 px-4 py-3 rounded-lg bg-red-600/20 hover:bg-red-600/30 text-red-400 hover:text-red-300 transition-all duration-200 font-medium"
          >
            <LogOut className="w-5 h-5 flex-shrink-0" />
            <span>Cerrar Sesión</span>
          </button>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col">
        {/* Top Bar */}
        <div className="bg-white border-b border-gray-200 shadow-sm">
          <div className="px-8 py-6">
            <div className="flex items-baseline gap-3 mb-2">
              {(() => {
                const pathName = location.pathname;
                if (pathName === "/admin/dashboard") {
                  return (
                    <>
                      <h1 className="text-2xl font-bold text-gray-900">
                        📊 Panel de Administración
                      </h1>
                      <span className="text-xs font-semibold text-purple-600 bg-purple-100 px-2.5 py-1 rounded-full">
                        Admin
                      </span>
                    </>
                  );
                } else if (pathName === "/admin/users") {
                  return (
                    <>
                      <h1 className="text-2xl font-bold text-gray-900">
                        👥 Gestión de Usuarios
                      </h1>
                      <span className="text-xs font-semibold text-purple-600 bg-purple-100 px-2.5 py-1 rounded-full">
                        Admin
                      </span>
                    </>
                  );
                } else if (pathName === "/admin/cursos") {
                  return (
                    <>
                      <h1 className="text-2xl font-bold text-gray-900">
                        📚 Gestión de Cursos
                      </h1>
                      <span className="text-xs font-semibold text-purple-600 bg-purple-100 px-2.5 py-1 rounded-full">
                        Admin
                      </span>
                    </>
                  );
                } else if (pathName === "/admin/reglas-gamificacion") {
                  return (
                    <>
                      <h1 className="text-2xl font-bold text-gray-900">
                        ⚙️ Reglas
                      </h1>
                      <span className="text-xs font-semibold text-purple-600 bg-purple-100 px-2.5 py-1 rounded-full">
                        Admin
                      </span>
                    </>
                  );
                } else if (pathName === "/admin/reportes") {
                  return (
                    <>
                      <h1 className="text-2xl font-bold text-gray-900">
                        📊 Reportes Generales
                      </h1>
                      <span className="text-xs font-semibold text-purple-600 bg-purple-100 px-2.5 py-1 rounded-full">
                        Admin
                      </span>
                    </>
                  );
                }
                return (
                  <>
                    <h1 className="text-2xl font-bold text-gray-900">
                      📚 Gestión de Cursos
                    </h1>
                    <span className="text-xs font-semibold text-purple-600 bg-purple-100 px-2.5 py-1 rounded-full">
                      Admin
                    </span>
                  </>
                );
              })()}
            </div>
            <p className="text-sm text-gray-500">
              {(() => {
                const pathName = location.pathname;
                if (pathName === "/admin/dashboard") {
                  return "Vista general del sistema EduQuest";
                } else if (pathName === "/admin/users") {
                  return "Administra estudiantes y profesores";
                } else if (pathName === "/admin/cursos") {
                  return "Gestiona los cursos disponibles en la plataforma";
                } else if (pathName === "/admin/reglas-gamificacion") {
                  return "Configura las reglas del sistema";
                } else if (pathName === "/admin/reportes") {
                  return "Visualiza reportes y estadísticas generales del sistema";
                } else if (pathName === "/admin/configuracion-visual") {
                  return "Configura el aspecto visual del sistema (logo, colores, etc.)";
                }
                return "";
              })()}
            </p>
          </div>
        </div>

        {/* Page Content */}
        <div className="flex-1 p-8 overflow-auto">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
