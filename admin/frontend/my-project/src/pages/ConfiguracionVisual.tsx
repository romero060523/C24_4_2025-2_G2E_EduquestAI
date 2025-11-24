import { useState, useEffect } from "react";
import { Save, RefreshCw, Palette, Image as ImageIcon, Building2 } from "lucide-react";
import { configuracionApi, type ConfiguracionVisual } from "../services/configuracionApi";

export default function ConfiguracionVisual() {
  const [config, setConfig] = useState<ConfiguracionVisual | null>(null);
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
    } catch (err: any) {
      console.error("Error cargando configuración:", err);
      // Si no existe configuración, crear una por defecto
      setConfig({
        logo_url: "",
        nombre_institucion: "EduQuest",
        color_primario: "#3B82F6",
        color_secundario: "#6366F1",
        color_acento: "#8B5CF6",
        color_fondo: "#F9FAFB",
        activo: true,
      });
    } finally {
      setLoading(false);
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
      
      // Aplicar tema inmediatamente
      aplicarTema(config);
      
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
    
    // Guardar en localStorage para persistencia
    localStorage.setItem("temaConfig", JSON.stringify(configData));
  };

  useEffect(() => {
    if (config) {
      aplicarTema(config);
    }
  }, [config]);

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
                URL del Logo
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
              {config.logo_url && (
                <div className="mt-2">
                  <img
                    src={config.logo_url}
                    alt="Logo preview"
                    className="h-16 object-contain"
                    onError={(e) => {
                      (e.target as HTMLImageElement).style.display = "none";
                    }}
                  />
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
                Color Primario
              </label>
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
                Color Secundario
              </label>
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
                Color de Acento
              </label>
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
                Color de Fondo
              </label>
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
            <p className="text-sm font-medium text-gray-700 mb-3">Vista Previa:</p>
            <div className="flex gap-2">
              <div
                className="px-4 py-2 rounded text-white text-sm font-medium"
                style={{ backgroundColor: config.color_primario }}
              >
                Primario
              </div>
              <div
                className="px-4 py-2 rounded text-white text-sm font-medium"
                style={{ backgroundColor: config.color_secundario }}
              >
                Secundario
              </div>
              <div
                className="px-4 py-2 rounded text-white text-sm font-medium"
                style={{ backgroundColor: config.color_acento }}
              >
                Acento
              </div>
              <div
                className="px-4 py-2 rounded text-sm font-medium border"
                style={{
                  backgroundColor: config.color_fondo,
                  color: config.color_primario,
                  borderColor: config.color_primario,
                }}
              >
                Fondo
              </div>
            </div>
          </div>
        </div>

        {/* Botones de Acción */}
        <div className="flex items-center justify-between">
          <button
            type="button"
            onClick={cargarConfiguracion}
            className="flex items-center gap-2 px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition"
          >
            <RefreshCw className="w-4 h-4" />
            Restaurar
          </button>

          <button
            type="submit"
            disabled={saving}
            className="flex items-center gap-2 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
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

