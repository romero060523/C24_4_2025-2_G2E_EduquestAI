import { useEffect, useState } from "react";
import { configuracionApi, type ConfiguracionVisual } from "../services/configuracionApi";

export function useTema() {
  const [tema, setTema] = useState<ConfiguracionVisual | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    cargarTema();
    
    // Cargar tema desde localStorage si existe
    const temaGuardado = localStorage.getItem("temaConfig");
    if (temaGuardado) {
      try {
        const temaData = JSON.parse(temaGuardado);
        aplicarTema(temaData);
        setTema(temaData);
      } catch (e) {
        console.error("Error cargando tema desde localStorage:", e);
      }
    }
  }, []);

  const cargarTema = async () => {
    try {
      const data = await configuracionApi.obtenerActiva();
      aplicarTema(data);
      setTema(data);
      localStorage.setItem("temaConfig", JSON.stringify(data));
    } catch (err) {
      console.error("Error cargando tema:", err);
      // Usar tema por defecto
      const temaDefault: ConfiguracionVisual = {
        logo_url: "",
        nombre_institucion: "EduQuest",
        color_primario: "#3B82F6",
        color_secundario: "#6366F1",
        color_acento: "#8B5CF6",
        color_fondo: "#F9FAFB",
        activo: true,
      };
      aplicarTema(temaDefault);
      setTema(temaDefault);
    } finally {
      setLoading(false);
    }
  };

  const aplicarTema = (config: ConfiguracionVisual) => {
    const root = document.documentElement;
    root.style.setProperty("--color-primario", config.color_primario);
    root.style.setProperty("--color-secundario", config.color_secundario);
    root.style.setProperty("--color-acento", config.color_acento);
    root.style.setProperty("--color-fondo", config.color_fondo);
  };

  return { tema, loading, aplicarTema, recargarTema: cargarTema };
}

