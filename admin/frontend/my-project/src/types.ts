export type UserRole = "administrador" | "profesor" | "estudiante";

export interface User {
  id: string; // UUID
  username: string;
  email: string;
  rol: UserRole;
  nombre_completo: string;
  avatar_url: string | null;
  activo: boolean;
  fecha_creacion: string;
  fecha_actualizacion: string;
  ultimo_acceso: string | null;
}

export interface UserPayload {
  username: string;
  email: string;
  rol: UserRole;
  nombre_completo: string;
  avatar_url?: string | null;
  activo: boolean;
  password?: string; // requerido en create, opcional en edit
}

// ============= CURSOS =============
export interface Curso {
  id: string; // UUID
  codigo_curso: string;
  nombre: string;
  descripcion: string | null;
  imagen_portada: string | null;
  fecha_inicio: string | null; // formato YYYY-MM-DD
  fecha_fin: string | null; // formato YYYY-MM-DD
  activo: boolean;
  fecha_creacion: string;
  fecha_actualizacion: string;
  total_estudiantes?: number; // campo calculado
}

export interface CursoPayload {
  codigo_curso: string;
  nombre: string;
  descripcion?: string | null;
  imagen_portada?: string | null;
  fecha_inicio?: string | null;
  fecha_fin?: string | null;
  activo: boolean;
}

// ============= INSCRIPCIONES =============
export type EstadoInscripcion = "activo" | "completado" | "retirado";

export interface Inscripcion {
  id: string; // UUID
  estudiante: string; // UUID
  estudiante_nombre: string;
  estudiante_email: string;
  curso: string; // UUID
  curso_nombre: string;
  curso_codigo: string;
  fecha_inscripcion: string;
  estado: EstadoInscripcion;
  fecha_completado: string | null;
  fecha_actualizacion: string;
}

export interface InscripcionPayload {
  estudiante: string; // UUID
  curso: string; // UUID
}

export interface InscripcionMasivaPayload {
  curso_id: string; // UUID
  estudiantes_ids: string[]; // Array de UUIDs
}
