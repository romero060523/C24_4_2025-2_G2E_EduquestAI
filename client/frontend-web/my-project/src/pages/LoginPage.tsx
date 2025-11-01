import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { apiService } from "../services/api";

type LoginForm = { email: string; password: string };

const LoginPage: React.FC = () => {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginForm>();
  const navigate = useNavigate();
  const { login } = useAuth();
  const [loginError, setLoginError] = useState<string>("");

  const onSubmit = async (data: LoginForm) => {
    try {
      setLoginError("");

      // Primero, hacer login directo para obtener el rol del backend
      const response = await apiService.loginProfesor(
        data.email,
        data.password
      );

      // Determinar rol del usuario desde el backend
      const rol = response.rol.toLowerCase();

      if (rol !== "profesor" && rol !== "estudiante") {
        setLoginError("Rol de usuario no válido. Contacta al administrador.");
        return;
      }

      // Ahora ejecutar login del contexto con el rol correcto
      await login(data.email, data.password, rol as "profesor" | "estudiante");

      // Redirigir según el rol detectado del backend
      if (rol === "profesor") {
        navigate("/profesor/inicio");
      } else if (rol === "estudiante") {
        navigate("/estudiante/inicio");
      }
    } catch (error: unknown) {
      setLoginError(
        error instanceof Error
          ? error.message
          : "Credenciales inválidas. Por favor, intenta de nuevo."
      );
      console.error("Error en login:", error);
    }
  };

  return (
    <div className="min-h-screen flex flex-col justify-center items-center bg-linear-to-br from-blue-50 via-indigo-50 to-purple-50">
      {/* Logo y Título */}
      <div className="mb-8 text-center">
        <div className="flex items-center justify-center gap-3 mb-2">
          <div className="w-12 h-12 bg-gradient-to-br from-blue-600 to-indigo-600 rounded-xl flex items-center justify-center shadow-lg">
            <svg
              className="w-7 h-7 text-white"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
              />
            </svg>
          </div>
          <h1 className="text-4xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
            EduQuest
          </h1>
        </div>
        <p className="text-gray-600 text-sm">
          Plataforma de gamificación educativa
        </p>
      </div>

      {/* Formulario de Login */}
      <div className="bg-white px-8 py-10 shadow-xl rounded-2xl w-[95%] max-w-md border border-gray-100">
        <h2 className="text-2xl font-bold text-gray-900 mb-2 text-center">
          Iniciar sesión
        </h2>
        <p className="text-gray-600 text-center text-sm mb-6">
          Ingresa tus credenciales para continuar
        </p>

        {loginError && (
          <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
            {loginError}
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
          {/* Email */}
          <div>
            <label
              htmlFor="email"
              className="block text-sm font-medium text-gray-800 mb-1"
            >
              Correo electrónico
            </label>
            <input
              type="email"
              id="email"
              autoComplete="email"
              {...register("email", { required: "El correo es obligatorio" })}
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
              placeholder="tu-correo@email.com"
            />
            {errors.email && (
              <div className="text-red-500 text-xs mt-1">
                {errors.email.message}
              </div>
            )}
          </div>

          {/* Password */}
          <div>
            <label
              htmlFor="password"
              className="block text-sm font-medium text-gray-800 mb-1"
            >
              Contraseña
            </label>
            <input
              type="password"
              id="password"
              autoComplete="current-password"
              {...register("password", {
                required: "La contraseña es obligatoria",
              })}
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
              placeholder="••••••••"
            />
            {errors.password && (
              <div className="text-red-500 text-xs mt-1">
                {errors.password.message}
              </div>
            )}
          </div>

          {/* Recordarme y Olvidaste contraseña */}
          <div className="flex items-center justify-between text-sm">
            <label className="flex items-center cursor-pointer">
              <input
                type="checkbox"
                className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              />
              <span className="ml-2 text-gray-600">Recordarme</span>
            </label>
            <a
              href="#"
              className="text-blue-600 hover:text-blue-700 hover:underline font-medium"
            >
              ¿Olvidaste tu contraseña?
            </a>
          </div>

          {/* Botón Submit */}
          <button
            type="submit"
            className="w-full bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white font-semibold py-3 rounded-lg transition-all shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
            disabled={isSubmitting}
          >
            {isSubmitting ? (
              <span className="flex items-center justify-center gap-2">
                <svg
                  className="animate-spin h-5 w-5 text-white"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                >
                  <circle
                    className="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    strokeWidth="4"
                  ></circle>
                  <path
                    className="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                  ></path>
                </svg>
                Iniciando sesión...
              </span>
            ) : (
              "Iniciar sesión"
            )}
          </button>
        </form>

        {/* Footer */}
        <div className="mt-6 text-center text-sm text-gray-500">
          ¿Necesitas ayuda?{" "}
          <a
            href="#"
            className="text-blue-600 hover:text-blue-700 hover:underline font-medium"
          >
            Contacta al administrador
          </a>
        </div>
      </div>

      {/* Información adicional */}
      <div className="mt-8 text-center text-xs text-gray-500">
        <p>© 2025 EduQuest. Todos los derechos reservados.</p>
      </div>
    </div>
  );
};

export default LoginPage;
