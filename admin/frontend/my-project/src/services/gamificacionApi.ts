import API from "./api";

export interface ReglaGamificacion {
  id: string;
  tipo_regla: string;
  tipo_regla_display: string;
  valor: number;
  descripcion: string;
  activo: boolean;
  fecha_creacion: string;
  fecha_actualizacion: string;
}

export interface ConfiguracionNivel {
  id: string;
  nivel: number;
  nombre: string;
  puntos_minimos: number;
  puntos_maximos: number | null;
  icono: string;
  descripcion: string;
  activo: boolean;
  fecha_creacion: string;
  fecha_actualizacion: string;
}

export interface EstadisticasGamificacion {
  total_estudiantes: number;
  total_profesores: number;
  total_cursos: number;
  total_misiones: number;
  total_puntos_otorgados: number;
  promedio_puntos_por_estudiante: number;
  total_logros_obtenidos: number;
  estudiantes_activos_mes: number;
  misiones_completadas_mes: number;
  cursos_mas_activos: Array<{
    id: string;
    nombre: string;
    codigo_curso: string;
    total_estudiantes: number;
  }>;
  estudiantes_top_puntos: Array<{
    estudiante_id: string;
    nombre: string;
    puntos_totales: number;
  }>;
}

export interface ReporteEstudiante {
  estudiante_id: string;
  nombre: string;
  email: string;
  puntos_totales: number;
  nivel_actual: number;
  nombre_nivel: string;
  misiones_completadas: number;
  logros_obtenidos: number;
  cursos_inscritos: number;
  ultima_actividad: string | null;
}

export interface ReporteCurso {
  curso_id: string;
  nombre: string;
  codigo: string;
  profesor_nombre: string;
  total_estudiantes: number;
  misiones_activas: number;
  misiones_completadas: number;
  promedio_puntos_curso: number;
  tasa_completacion: number;
}

// Reglas de Gamificación
export const reglasGamificacionApi = {
  listar: (activo?: boolean): Promise<ReglaGamificacion[]> => {
    const params = activo !== undefined ? { activo: activo.toString() } : {};
    return API.get("/gamificacion/reglas/", { params }).then((res) => res.data);
  },

  obtener: (id: string): Promise<ReglaGamificacion> => {
    return API.get(`/gamificacion/reglas/${id}/`).then((res) => res.data);
  },

  crear: (data: Partial<ReglaGamificacion>): Promise<ReglaGamificacion> => {
    return API.post("/gamificacion/reglas/", data).then((res) => res.data);
  },

  actualizar: (
    id: string,
    data: Partial<ReglaGamificacion>
  ): Promise<ReglaGamificacion> => {
    return API.put(`/gamificacion/reglas/${id}/`, data).then((res) => res.data);
  },

  eliminar: (id: string): Promise<void> => {
    return API.delete(`/gamificacion/reglas/${id}/`).then(() => undefined);
  },
};

// Configuración de Niveles
export const configuracionNivelesApi = {
  listar: (): Promise<ConfiguracionNivel[]> => {
    return API.get("/gamificacion/niveles/").then((res) => res.data);
  },

  obtener: (id: string): Promise<ConfiguracionNivel> => {
    return API.get(`/gamificacion/niveles/${id}/`).then((res) => res.data);
  },

  crear: (data: Partial<ConfiguracionNivel>): Promise<ConfiguracionNivel> => {
    return API.post("/gamificacion/niveles/", data).then((res) => res.data);
  },

  actualizar: (
    id: string,
    data: Partial<ConfiguracionNivel>
  ): Promise<ConfiguracionNivel> => {
    return API.put(`/gamificacion/niveles/${id}/`, data).then((res) => res.data);
  },

  eliminar: (id: string): Promise<void> => {
    return API.delete(`/gamificacion/niveles/${id}/`).then(() => undefined);
  },
};

// Reportes
export const reportesApi = {
  estadisticasGenerales: (): Promise<EstadisticasGamificacion> => {
    return API.get("/gamificacion/reportes/estadisticas_generales/").then(
      (res) => res.data
    );
  },

  reporteEstudiantes: (): Promise<ReporteEstudiante[]> => {
    return API.get("/gamificacion/reportes/reporte_estudiantes/").then(
      (res) => res.data
    );
  },

  reporteCursos: (): Promise<ReporteCurso[]> => {
    return API.get("/gamificacion/reportes/reporte_cursos/").then(
      (res) => res.data
    );
  },

  resumenMensual: (): Promise<{
    mes: number;
    año: number;
    nuevos_estudiantes: number;
    nuevos_cursos: number;
    estudiantes_activos: number;
  }> => {
    return API.get("/gamificacion/reportes/resumen_mensual/").then(
      (res) => res.data
    );
  },
};

