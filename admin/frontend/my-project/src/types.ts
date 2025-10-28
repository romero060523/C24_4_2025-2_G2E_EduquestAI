export type UserRole = 'administrador' | 'profesor' | 'estudiante';

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