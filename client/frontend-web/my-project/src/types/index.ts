// Tipos y enums para entidades y DTOs

export type TipoMision = "INDIVIDUAL" | "GRUPAL";
export type CategoriaMision =
  | "LECTURA"
  | "EJERCICIO"
  | "PROYECTO"
  | "QUIZ"
  | "DESAFIO";
export type DificultadMision = "FACIL" | "MEDIO" | "DIFICIL" | "EXPERTO";

export type TipoAlerta =
  | "INACTIVIDAD"
  | "BAJO_RENDIMIENTO"
  | "MISIONES_PENDIENTES"
  | "DEBAJO_PROMEDIO";
export type EstadoAlerta = "ACTIVA" | "RESUELTA" | "IGNORADA";

export interface Usuario {
  id: string;
  username: string;
  email: string;
  nombreCompleto: string;
  rol: "ADMINISTRADOR" | "PROFESOR" | "ESTUDIANTE";
  avatarUrl?: string;
  activo: boolean;
  fechaCreacion: string;
  fechaActualizacion: string;
  ultimoAcceso?: string;
}

export interface Curso {
  id: string;
  codigoCurso: string;
  nombre: string;
  descripcion?: string;
  imagenPortada?: string;
  activo: boolean;
  fechaCreacion: string;
  fechaActualizacion: string;
}

export interface Mision {
  id: string;
  titulo: string;
  descripcion: string;
  tipoMision: TipoMision;
  categoria: CategoriaMision;
  dificultad: DificultadMision;
  puntosRecompensa: number;
  experienciaRecompensa: number;
  fechaInicio: string;
  fechaLimite: string;
  activo: boolean;
  fechaCreacion: string;
  fechaActualizacion: string;
  requisitosPrevios?: string;
  profesor?: Usuario;
  curso?: Curso;
}

export interface MisionListResponse {
  id: string;
  titulo: string;
  descripcionResumida: string;
  categoria: CategoriaMision;
  dificultad: DificultadMision;
  puntosRecompensa: number;
  fechaLimite: string;
  activo: boolean;
  cursoNombre: string;
  estudiantesCompletados: number;
  totalEstudiantes: number;
}

// DTOs con enums en mayúscula y nombres/campos correctos
export interface CrearMisionDTO {
  titulo: string;
  descripcion: string;
  tipoMision: TipoMision;
  categoria: CategoriaMision;
  dificultad: DificultadMision;
  puntosRecompensa: number;
  experienciaRecompensa: number;
  fechaInicio: string;
  fechaLimite: string;
  cursoId: string;
  requisitosPrevios?: string;
  // activo?: boolean; // Opcional si tu backend lo soporta
}

export interface ActualizarMisionDTO {
  titulo?: string;
  descripcion?: string;
  tipoMision?: TipoMision;
  categoria?: CategoriaMision;
  dificultad?: DificultadMision;
  puntosRecompensa?: number;
  experienciaRecompensa?: number;
  fechaInicio?: string;
  fechaLimite?: string;
  requisitosPrevios?: string;
  activo?: boolean;
}

export interface ApiResponse<T> {
  data: T;
  message?: string;
  status: number;
}

export interface ApiError {
  message: string;
  status: number;
  errors?: Record<string, string[]>;
}

// ==================== AUTH ====================

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  id: string;
  username: string;
  email: string;
  nombreCompleto: string;
  rol: string;
  avatarUrl?: string;
  token: string;
  message?: string;
}

// ==================== MISIONES ESTUDIANTE ====================

export interface MisionEstudianteResponse {
  id: string;
  titulo: string;
  descripcion: string;
  categoria: CategoriaMision;
  dificultad: DificultadMision;
  puntosRecompensa: number;
  experienciaRecompensa: number;
  fechaInicio: string;
  fechaLimite: string;
  activo: boolean;
  cursoNombre: string;
  porcentajeCompletado: number;
  completada: boolean;
  fechaCompletado?: string;
  estadoEntrega:
    | "PENDIENTE"
    | "ENVIADA"
    | "REVISANDO"
    | "CALIFICADA"
    | "RECHAZADA";
  puntosObtenidos: number;
  ultimaActividad: string;
}

export interface CompletarMisionRequest {
  contenidoEntrega: string;
  archivoUrl?: string;
  comentariosEstudiante?: string;
}

// ==================== GAMIFICACIÓN ====================

export interface LogroResponse {
  id: string;
  nombre: string;
  descripcion: string;
  icono?: string;
  puntosRequeridos: number;
  fechaObtenido?: string;
  obtenido: boolean;
}

export interface PerfilGamificadoResponse {
  puntosTotales: number;
  nivel: number;
  nombreNivel: string;
  puntosParaSiguienteNivel: number;
  misionesCompletadas: number;
  logrosObtenidos: number;
  logros: LogroResponse[];
  posicionRanking?: number;
}

export interface RankingEstudianteResponse {
  estudianteId: string;
  nombreEstudiante: string;
  puntosTotales: number;
  nivel: number;
  nombreNivel: string;
  misionesCompletadas: number;
  posicion: number;
}

export interface RankingResponse {
  cursoId?: string;
  cursoNombre: string;
  estudiantes: RankingEstudianteResponse[];
  totalEstudiantes: number;
}

export interface EstudianteSimple {
  id: string;
  nombreCompleto: string;
  email: string;
  username: string;
  avatarUrl?: string;
  fechaInscripcion?: string;
}

// ==================== PROGRESO DE ESTUDIANTES ====================

export interface EstudianteProgresoResponse {
  estudianteId: string;
  nombreCompleto: string;
  avatarUrl?: string;
  porcentajeCompletado: number;
  estado: string; // 'completada' | 'en_progreso' | 'no_iniciada'
  ultimaActividad: string;
}

export interface MisionProgresoResponse {
  misionId: string;
  titulo: string;
  totalEstudiantes: number;
  completados: number;
  enProgreso: number;
  noIniciados: number;
  estudiantes: EstudianteProgresoResponse[];
}

// ==================== ALERTAS ====================

export interface ConfiguracionAlertaRequest {
  cursoId: string;
  diasInactividad?: number;
  porcentajeCompletitudMinimo?: number;
  puntosDebajoPromedio?: boolean;
  misionesPendientesMinimo?: number;
}

export interface ConfiguracionAlertaResponse {
  id: string;
  cursoId: string;
  cursoNombre: string;
  diasInactividad?: number;
  porcentajeCompletitudMinimo?: number;
  puntosDebajoPromedio?: boolean;
  misionesPendientesMinimo?: number;
  activo: boolean;
  fechaCreacion: string;
}

export interface AlertaRendimiento {
  id: string;
  estudianteId: string;
  estudianteNombre: string;
  estudianteEmail: string;
  cursoId: string;
  cursoNombre: string;
  tipo: TipoAlerta;
  descripcion: string;
  datosContexto: string;
  estado: EstadoAlerta;
  fechaCreacion: string;
}

export interface DatosContextoAlerta {
  diasInactivo?: number;
  ultimaActividad?: string;
  porcentajeCompletitud?: number;
  misionesCompletadas?: number;
  totalMisiones?: number;
  puntosEstudiante?: number;
  promedioGrupo?: number;
  misionesPendientes?: number;
}
