import React, { createContext, useState, useEffect } from "react";
import type { ReactNode } from "react";
import { apiService } from "../services/api";

interface Usuario {
  id: string;
  nombre: string;
  email: string;
  rol: "profesor" | "estudiante";
}

interface AuthContextType {
  usuario: Usuario | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (
    email: string,
    password: string,
    rol: "profesor" | "estudiante"
  ) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const [usuario, setUsuario] = useState<Usuario | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // Al cargar, verificar si hay una sesión guardada
  useEffect(() => {
    const storedProfesor = localStorage.getItem("profesor");
    const storedEstudiante = localStorage.getItem("estudiante");
    const token = localStorage.getItem("access_token");

    if (token) {
      if (storedProfesor) {
        try {
          const data = JSON.parse(storedProfesor);
          setUsuario({ ...data, rol: "profesor" });
        } catch (error) {
          console.error("Error al parsear profesor:", error);
          localStorage.removeItem("profesor");
        }
      } else if (storedEstudiante) {
        try {
          const data = JSON.parse(storedEstudiante);
          setUsuario({ ...data, rol: "estudiante" });
        } catch (error) {
          console.error("Error al parsear estudiante:", error);
          localStorage.removeItem("estudiante");
        }
      }
    }
    setIsLoading(false);
  }, []);

  const login = async (
    email: string,
    password: string,
    rol: "profesor" | "estudiante"
  ) => {
    try {
      const response =
        rol === "profesor"
          ? await apiService.loginProfesor(email, password)
          : await apiService.loginEstudiante(email, password);

      const usuarioData: Usuario = {
        id: response.id,
        nombre: response.nombreCompleto,
        email: response.email,
        rol,
      };

      // Limpiar cualquier sesión previa del otro rol
      localStorage.removeItem("profesor");
      localStorage.removeItem("estudiante");
      localStorage.removeItem("profesorId");
      localStorage.removeItem("estudianteId");

      // Guardar en localStorage segun el rol
      const storageKey = rol === "profesor" ? "profesor" : "estudiante";
      const idKey = rol === "profesor" ? "profesorId" : "estudianteId";

      localStorage.setItem(storageKey, JSON.stringify(usuarioData));
      localStorage.setItem("access_token", response.token);
      localStorage.setItem(idKey, response.id);

      setUsuario(usuarioData);
    } catch (error: unknown) {
      console.error("Error en login:", error);
      const errorMessage =
        error instanceof Error ? error.message : "Credenciales inválidas";
      throw new Error(errorMessage);
    }
  };

  const logout = () => {
    localStorage.removeItem("profesor");
    localStorage.removeItem("estudiante");
    localStorage.removeItem("access_token");
    localStorage.removeItem("profesorId");
    localStorage.removeItem("estudianteId");
    setUsuario(null);
  };

  return (
    <AuthContext.Provider
      value={{
        usuario,
        isAuthenticated: !!usuario,
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
