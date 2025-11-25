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
        setLoading(false);
      } catch (e) {
        console.error("Error cargando tema desde localStorage:", e);
      }
    }
    
    // Escuchar eventos de actualización de tema
    const handleTemaActualizado = (event: CustomEvent) => {
      const nuevoTema = event.detail;
      aplicarTema(nuevoTema);
      setTema(nuevoTema);
      localStorage.setItem("temaConfig", JSON.stringify(nuevoTema));
    };
    
    window.addEventListener('temaActualizado', handleTemaActualizado as EventListener);
    
    return () => {
      window.removeEventListener('temaActualizado', handleTemaActualizado as EventListener);
    };
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
    
    // Aplicar logo y nombre de institución como atributos de datos para acceso global
    root.setAttribute("data-logo-url", config.logo_url || "");
    root.setAttribute("data-nombre-institucion", config.nombre_institucion || "EduQuest");
    
    // Convertir colores hex a RGB para usar con opacidad
    const hexToRgb = (hex: string) => {
      const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
      return result ? `${parseInt(result[1], 16)}, ${parseInt(result[2], 16)}, ${parseInt(result[3], 16)}` : null;
    };
    
    const rgbPrimario = hexToRgb(config.color_primario);
    const rgbSecundario = hexToRgb(config.color_secundario);
    const rgbAcento = hexToRgb(config.color_acento);
    
    if (rgbPrimario) root.style.setProperty("--color-primario-rgb", rgbPrimario);
    if (rgbSecundario) root.style.setProperty("--color-secundario-rgb", rgbSecundario);
    if (rgbAcento) root.style.setProperty("--color-acento-rgb", rgbAcento);
  };

  return { tema, loading, aplicarTema, recargarTema: cargarTema };
}

