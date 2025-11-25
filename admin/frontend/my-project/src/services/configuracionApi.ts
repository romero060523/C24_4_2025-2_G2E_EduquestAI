import API from "./api";

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

export const configuracionApi = {
  // Obtener configuración activa
  async obtenerActiva(): Promise<ConfiguracionVisual> {
    const res = await API.get("/configuracion-visual/activa/");
    return res.data;
  },

  // Listar todas las configuraciones
  async listar(): Promise<ConfiguracionVisual[]> {
    const res = await API.get("/configuracion-visual/");
    return res.data;
  },

  // Obtener por ID
  async obtener(id: string): Promise<ConfiguracionVisual> {
    const res = await API.get(`/configuracion-visual/${id}/`);
    return res.data;
  },

  // Crear nueva configuración
  async crear(data: Partial<ConfiguracionVisual>): Promise<ConfiguracionVisual> {
    const res = await API.post("/configuracion-visual/", data);
    return res.data;
  },

  // Actualizar configuración
  async actualizar(id: string, data: Partial<ConfiguracionVisual>): Promise<ConfiguracionVisual> {
    const res = await API.put(`/configuracion-visual/${id}/`, data);
    return res.data;
  },

  // Eliminar configuración
  async eliminar(id: string): Promise<void> {
    await API.delete(`/configuracion-visual/${id}/`);
  },
};

