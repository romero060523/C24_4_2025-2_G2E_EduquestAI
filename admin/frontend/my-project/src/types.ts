export type UserRole = 'administrador' | 'profesor' | 'estudiante';

export interface User {
  id: string; // UUID
  username: string;
  email: string;
  role: UserRole;
  full_name: string;
  avatar_url: string | null;
  active: boolean;
  created_at: string;
  updated_at: string;
  last_access: string | null;
}

export interface UserPayload {
  username: string;
  email: string;
  role: UserRole;
  full_name: string;
  avatar_url?: string | null;
  active: boolean;
  password?: string; // requerido en create, opcional en edit
}