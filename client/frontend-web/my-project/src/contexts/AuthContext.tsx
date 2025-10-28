import React, { createContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { apiService } from '../services/api';

interface Profesor {
  id: string;
  nombre: string;
  email: string;
}

interface AuthContextType {
  profesor: Profesor | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [profesor, setProfesor] = useState<Profesor | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // Al cargar, verificar si hay una sesión guardada
  useEffect(() => {
    const storedProfesor = localStorage.getItem('profesor');
    const token = localStorage.getItem('access_token');
    
    if (storedProfesor && token) {
      try {
        setProfesor(JSON.parse(storedProfesor));
      } catch (error) {
        console.error('Error al parsear profesor:', error);
        localStorage.removeItem('profesor');
        localStorage.removeItem('access_token');
      }
    }
    setIsLoading(false);
  }, []);

  const login = async (email: string, password: string) => {
    try {
      // Llamada real al backend
      const response = await apiService.loginProfesor(email, password);
      
      // Mapear la respuesta al formato del profesor
      const profesorData: Profesor = {
        id: response.id,
        nombre: response.nombreCompleto,
        email: response.email,
      };

      // Guardar en localStorage
      localStorage.setItem('profesor', JSON.stringify(profesorData));
      localStorage.setItem('access_token', response.token);
      localStorage.setItem('profesorId', response.id);

      setProfesor(profesorData);
    } catch (error: unknown) {
      console.error('Error en login:', error);
      const errorMessage = error instanceof Error ? error.message : 'Credenciales inválidas';
      throw new Error(errorMessage);
    }
  };

  const logout = () => {
    localStorage.removeItem('profesor');
    localStorage.removeItem('access_token');
    localStorage.removeItem('profesorId');
    setProfesor(null);
  };

  return (
    <AuthContext.Provider
      value={{
        profesor,
        isAuthenticated: !!profesor,
        isLoading,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

// Exportar el contexto para el hook
export { AuthContext };
