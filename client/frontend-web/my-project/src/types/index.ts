// Tipos y enums para entidades y DTOs

export type TipoMision = 'INDIVIDUAL' | 'GRUPAL';
export type CategoriaMision = 'LECTURA' | 'EJERCICIO' | 'PROYECTO' | 'QUIZ' | 'DESAFIO';
export type DificultadMision = 'FACIL' | 'MEDIO' | 'DIFICIL' | 'EXPERTO';

export interface Usuario {
  id: string;
  username: string;
  email: string;
  nombreCompleto: string;
  rol: 'ADMINISTRADOR' | 'PROFESOR' | 'ESTUDIANTE';
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

