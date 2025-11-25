import axios from "axios";

export interface ConfiguracionVisual {
  id?: string;
  logo_url: string;
  nombre_institucion: string;
  color_primario: string;
  color_secundario: string;
  color_acento: string;
  color_fondo: string;
  activo: boolean;
  fecha_creacion?: string;
  fecha_actualizacion?: string;
}

const ADMIN_API_BASE_URL = "http://localhost:8000/api";

export const temaApi = {
  // Obtener configuración visual activa desde el backend de Django
  async obtenerTema(): Promise<ConfiguracionVisual> {
    try {
      const response = await axios.get(`${ADMIN_API_BASE_URL}/configuracion-visual/activa/`);
      return response.data;
    } catch (error) {
      console.error("Error obteniendo tema:", error);
      // Retornar tema por defecto si hay error
      return {
        logo_url: "",
        nombre_institucion: "EduQuest",
        color_primario: "#3B82F6",
        color_secundario: "#6366F1",
        color_acento: "#8B5CF6",
        color_fondo: "#F9FAFB",
        activo: true,
      };
    }
  },
};

