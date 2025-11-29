import { useState, useEffect } from "react";
import { Save, RefreshCw, Palette, Image as ImageIcon, Building2 } from "lucide-react";
import { configuracionApi, type ConfiguracionVisual } from "../services/configuracionApi";

export default function ConfiguracionVisual() {
  const [config, setConfig] = useState<ConfiguracionVisual | null>(null);
  const [configOriginal, setConfigOriginal] = useState<ConfiguracionVisual | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    cargarConfiguracion();
  }, []);

  const cargarConfiguracion = async () => {
    try {
      setLoading(true);
      setError("");
      const data = await configuracionApi.obtenerActiva();
      setConfig(data);
      setConfigOriginal(JSON.parse(JSON.stringify(data))); // Guardar copia para restaurar
      aplicarTema(data);
    } catch (err: any) {
      console.error("Error cargando configuración:", err);
      // Si no existe configuración, crear una por defecto
      const configDefault = {
        logo_url: "",
        nombre_institucion: "EduQuest",
        color_primario: "#3B82F6",
        color_secundario: "#6366F1",
        color_acento: "#8B5CF6",
        color_fondo: "#F9FAFB",
        activo: true,
      };
      setConfig(configDefault);
      setConfigOriginal(JSON.parse(JSON.stringify(configDefault)));
      aplicarTema(configDefault);
    } finally {
      setLoading(false);
    }
  };

  const restaurarConfiguracion = () => {
    if (configOriginal) {
      setConfig(JSON.parse(JSON.stringify(configOriginal)));
      aplicarTema(configOriginal);
      setSuccess("Configuración restaurada");
      setTimeout(() => setSuccess(""), 3000);
    } else {
      cargarConfiguracion();
    }
  };

  const handleChange = (field: keyof ConfiguracionVisual, value: any) => {
    if (config) {
      setConfig({ ...config, [field]: value });
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!config) return;

    try {
      setSaving(true);
      setError("");
      setSuccess("");

      if (config.id) {
        await configuracionApi.actualizar(config.id, config);
      } else {
        await configuracionApi.crear(config);
      }

      setSuccess("Configuración guardada exitosamente");
      
      // Recargar configuración actualizada desde el servidor
      const configActualizada = await configuracionApi.obtenerActiva();
      setConfig(configActualizada);
      
      // Aplicar tema inmediatamente
      aplicarTema(configActualizada);
      
      // Actualizar configuración original
      setConfigOriginal(JSON.parse(JSON.stringify(configActualizada)));
      
      // Notificar a otros componentes que el tema cambió (con múltiples eventos para asegurar que se capture)
      window.dispatchEvent(new CustomEvent('temaActualizado', { detail: configActualizada }));
      window.dispatchEvent(new Event('storage')); // Disparar evento storage para sincronizar
      
      // Forzar re-render de componentes que usan el tema
      document.body.style.setProperty('--force-theme-update', Date.now().toString());
      
      setTimeout(() => setSuccess(""), 3000);
    } catch (err: any) {
      console.error("Error guardando configuración:", err);
      setError(err.response?.data?.message || "Error al guardar la configuración");
    } finally {
      setSaving(false);
    }
  };

  const aplicarTema = (configData: ConfiguracionVisual) => {
    // Aplicar colores como variables CSS
    const root = document.documentElement;
    root.style.setProperty("--color-primario", configData.color_primario);
    root.style.setProperty("--color-secundario", configData.color_secundario);
    root.style.setProperty("--color-acento", configData.color_acento);
    root.style.setProperty("--color-fondo", configData.color_fondo);
    
    // Aplicar logo y nombre de institución como atributos de datos para acceso global
    root.setAttribute("data-logo-url", configData.logo_url || "");
    root.setAttribute("data-nombre-institucion", configData.nombre_institucion || "EduQuest");
    
    // Convertir colores hex a RGB para usar con opacidad
    const hexToRgb = (hex: string) => {
      const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
      return result ? `${parseInt(result[1], 16)}, ${parseInt(result[2], 16)}, ${parseInt(result[3], 16)}` : null;
    };
    
    const rgbPrimario = hexToRgb(configData.color_primario);
    const rgbSecundario = hexToRgb(configData.color_secundario);
    const rgbAcento = hexToRgb(configData.color_acento);
    
    if (rgbPrimario) root.style.setProperty("--color-primario-rgb", rgbPrimario);
    if (rgbSecundario) root.style.setProperty("--color-secundario-rgb", rgbSecundario);
    if (rgbAcento) root.style.setProperty("--color-acento-rgb", rgbAcento);
    
    // Guardar en localStorage para persistencia
    localStorage.setItem("temaConfig", JSON.stringify(configData));
  };

  // Aplicar tema en tiempo real cuando cambian los colores, logo o nombre
  useEffect(() => {
    if (config && !loading) {
      aplicarTema(config);
      // Notificar cambios en tiempo real
      window.dispatchEvent(new CustomEvent('temaActualizado', { detail: config }));
    }
  }, [
    config?.color_primario, 
    config?.color_secundario, 
    config?.color_acento, 
    config?.color_fondo,
    config?.logo_url,
    config?.nombre_institucion,
    loading
  ]);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!config) {
    return <div>Error al cargar la configuración</div>;
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Configuración Visual</h1>
        <p className="text-gray-600 mt-2">
          Personaliza el aspecto visual del sistema (logo, colores, etc.)
        </p>
      </div>

      {/* Mensajes */}
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {error}
        </div>
      )}
      {success && (
        <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg">
          {success}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Identidad Visual */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="flex items-center gap-2 mb-4">
            <Building2 className="w-5 h-5 text-gray-600" />
            <h2 className="text-xl font-semibold text-gray-900">Identidad Visual</h2>
          </div>

          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Nombre de la Institución
              </label>
              <input
                type="text"
                value={config.nombre_institucion}
                onChange={(e) => handleChange("nombre_institucion", e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="EduQuest"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Logo de la Institución
              </label>
              <p className="text-xs text-gray-500 mb-3">
                Puedes subir una imagen desde tu computadora o ingresar una URL
              </p>
              
              {/* Opción 1: Subir imagen */}
              <div className="mb-4">
                <label className="block text-xs font-medium text-gray-600 mb-2">
                  Subir imagen desde tu computadora:
                </label>
                <input
                  type="file"
                  accept="image/*"
                  onChange={(e) => {
                    const file = e.target.files?.[0];
                    if (file) {
                      // Validar tamaño (máximo 5MB)
                      if (file.size > 5 * 1024 * 1024) {
                        setError("La imagen es demasiado grande. Máximo 5MB.");
                        setTimeout(() => setError(""), 5000);
                        return;
                      }
                      
                      // Validar tipo
                      if (!file.type.startsWith('image/')) {
                        setError("Por favor selecciona un archivo de imagen válido.");
                        setTimeout(() => setError(""), 5000);
                        return;
                      }
                      
                      // Convertir a base64
                      const reader = new FileReader();
                      reader.onloadend = () => {
                        const base64String = reader.result as string;
                        handleChange("logo_url", base64String);
                        setSuccess("Imagen cargada exitosamente");
                        setTimeout(() => setSuccess(""), 3000);
                      };
                      reader.onerror = () => {
                        setError("Error al leer la imagen");
                        setTimeout(() => setError(""), 5000);
                      };
                      reader.readAsDataURL(file);
                    }
                  }}
                  className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100 cursor-pointer"
                />
              </div>
              
              {/* Opción 2: URL */}
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-2">
                  O ingresa una URL de imagen:
                </label>
                <div className="flex items-center gap-3">
                  <input
                    type="text"
                    value={config.logo_url}
                    onChange={(e) => handleChange("logo_url", e.target.value)}
                    className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="https://ejemplo.com/logo.png"
                  />
                  <ImageIcon className="w-5 h-5 text-gray-400" />
                </div>
              </div>
              
              {/* Vista previa */}
              {config.logo_url && (
                <div className="mt-4 p-3 bg-gray-50 rounded-lg border border-gray-200">
                  <p className="text-xs font-medium text-gray-700 mb-2">Vista previa:</p>
                  <div className="flex items-center gap-3">
                    <img
                      src={config.logo_url}
                      alt="Logo preview"
                      className="h-16 object-contain max-w-full"
                      onError={(e) => {
                        (e.target as HTMLImageElement).style.display = "none";
                        const parent = (e.target as HTMLImageElement).parentElement;
                        if (parent) {
                          const errorMsg = document.createElement('p');
                          errorMsg.className = 'text-red-500 text-xs';
                          errorMsg.textContent = 'Error al cargar la imagen. Verifica la URL o intenta subir una imagen.';
                          if (!parent.querySelector('.text-red-500')) {
                            parent.appendChild(errorMsg);
                          }
                        }
                      }}
                    />
                    <button
                      type="button"
                      onClick={() => handleChange("logo_url", "")}
                      className="text-xs text-red-600 hover:text-red-700 font-medium"
                    >
                      Eliminar logo
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Colores del Tema */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="flex items-center gap-2 mb-4">
            <Palette className="w-5 h-5 text-gray-600" />
            <h2 className="text-xl font-semibold text-gray-900">Colores del Tema</h2>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Color Principal (Botones y Enlaces)
              </label>
              <p className="text-xs text-gray-500 mb-2">
                Color que se usa en botones principales, enlaces y elementos destacados
              </p>
              <div className="flex items-center gap-2">
                <input
                  type="color"
                  value={config.color_primario}
                  onChange={(e) => handleChange("color_primario", e.target.value)}
                  className="w-16 h-10 border border-gray-300 rounded cursor-pointer"
                />
                <input
                  type="text"
                  value={config.color_primario}
                  onChange={(e) => handleChange("color_primario", e.target.value)}
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="#3B82F6"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Color Secundario (Elementos de Apoyo)
              </label>
              <p className="text-xs text-gray-500 mb-2">
                Color para elementos secundarios como badges, iconos y complementos
              </p>
              <div className="flex items-center gap-2">
                <input
                  type="color"
                  value={config.color_secundario}
                  onChange={(e) => handleChange("color_secundario", e.target.value)}
                  className="w-16 h-10 border border-gray-300 rounded cursor-pointer"
                />
                <input
                  type="text"
                  value={config.color_secundario}
                  onChange={(e) => handleChange("color_secundario", e.target.value)}
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="#6366F1"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Color de Acento (Destacados y Alertas)
              </label>
              <p className="text-xs text-gray-500 mb-2">
                Color para elementos que necesitan destacar como notificaciones y alertas importantes
              </p>
              <div className="flex items-center gap-2">
                <input
                  type="color"
                  value={config.color_acento}
                  onChange={(e) => handleChange("color_acento", e.target.value)}
                  className="w-16 h-10 border border-gray-300 rounded cursor-pointer"
                />
                <input
                  type="text"
                  value={config.color_acento}
                  onChange={(e) => handleChange("color_acento", e.target.value)}
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="#8B5CF6"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Color de Fondo (Fondo de Páginas)
              </label>
              <p className="text-xs text-gray-500 mb-2">
                Color de fondo general de las páginas y áreas principales
              </p>
              <div className="flex items-center gap-2">
                <input
                  type="color"
                  value={config.color_fondo}
                  onChange={(e) => handleChange("color_fondo", e.target.value)}
                  className="w-16 h-10 border border-gray-300 rounded cursor-pointer"
                />
                <input
                  type="text"
                  value={config.color_fondo}
                  onChange={(e) => handleChange("color_fondo", e.target.value)}
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="#F9FAFB"
                />
              </div>
            </div>
          </div>

          {/* Vista Previa */}
          <div className="mt-6 p-4 bg-gray-50 rounded-lg">
            <p className="text-sm font-medium text-gray-700 mb-3">Vista Previa de Colores:</p>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
              <div className="text-center">
                <div
                  className="px-4 py-3 rounded text-white text-sm font-medium mb-1"
                  style={{ backgroundColor: config.color_primario }}
                >
                  Botón Principal
                </div>
                <p className="text-xs text-gray-600">Color Principal</p>
              </div>
              <div className="text-center">
                <div
                  className="px-4 py-3 rounded text-white text-sm font-medium mb-1"
                  style={{ backgroundColor: config.color_secundario }}
                >
                  Elemento Secundario
                </div>
                <p className="text-xs text-gray-600">Color Secundario</p>
              </div>
              <div className="text-center">
                <div
                  className="px-4 py-3 rounded text-white text-sm font-medium mb-1"
                  style={{ backgroundColor: config.color_acento }}
                >
                  Destacado
                </div>
                <p className="text-xs text-gray-600">Color de Acento</p>
              </div>
              <div className="text-center">
                <div
                  className="px-4 py-3 rounded text-sm font-medium border mb-1"
                  style={{
                    backgroundColor: config.color_fondo,
                    color: config.color_primario,
                    borderColor: config.color_primario,
                  }}
                >
                  Fondo
                </div>
                <p className="text-xs text-gray-600">Color de Fondo</p>
              </div>
            </div>
          </div>
        </div>

        {/* Botones de Acción */}
        <div className="flex items-center justify-between">
          <button
            type="button"
            onClick={restaurarConfiguracion}
            className="flex items-center gap-2 px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition"
          >
            <RefreshCw className="w-4 h-4" />
            Restaurar
          </button>

          <button
            type="submit"
            disabled={saving}
            className="flex items-center gap-2 px-6 py-2 text-white rounded-lg hover:opacity-90 transition disabled:opacity-50 disabled:cursor-not-allowed"
            style={{ backgroundColor: config.color_primario }}
          >
            {saving ? (
              <>
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                Guardando...
              </>
            ) : (
              <>
                <Save className="w-4 h-4" />
                Guardar Configuración
              </>
            )}
          </button>
        </div>
      </form>
    </div>
  );
}

