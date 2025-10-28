import LoginForm from "../components/LoginForm";
import { GraduationCap } from "lucide-react";

export default function LoginPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100">
      <div className="w-full max-w-md px-6">
        {/* Logo and Title */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center gap-2 mb-4">
            <GraduationCap className="w-8 h-8 text-blue-600" />
            <h1 className="text-3xl font-bold text-gray-800">EduQuest</h1>
          </div>
          <p className="text-gray-500 text-sm">Inicia sesión en tu cuenta</p>
        </div>

        {/* Login Card */}
        <div className="bg-white rounded-2xl shadow-lg p-8">
          <div className="mb-6">
            <h2 className="text-xl font-semibold text-gray-800 mb-1">
              Bienvenido de nuevo
            </h2>
            <p className="text-gray-500 text-sm">
              Ingresa tus credenciales para continuar
            </p>
          </div>

          {/* Login Form */}
          <LoginForm />

          {/* Role Selection */}
          <div className="mt-6">
            <p className="text-center text-gray-500 text-sm mb-3">
              O prueba los diferentes roles
            </p>
            <div className="flex gap-2">
              <button className="flex-1 py-2 px-4 border-2 border-blue-500 text-blue-600 rounded-lg hover:bg-blue-50 transition text-sm font-medium">
                Estudiante
              </button>
              <button className="flex-1 py-2 px-4 border-2 border-green-500 text-green-600 rounded-lg hover:bg-green-50 transition text-sm font-medium">
                Profesor
              </button>
              <button className="flex-1 py-2 px-4 border-2 border-purple-500 text-purple-600 rounded-lg hover:bg-purple-50 transition text-sm font-medium">
                Admin
              </button>
            </div>
          </div>
        </div>

        {/* Footer Links */}
        <div className="mt-6 text-center space-y-2">
          <p className="text-gray-600 text-sm">
            ¿No tienes una cuenta?{" "}
            
            <a  href="#"
              className="text-blue-600 hover:text-blue-700 font-medium"
            >
              Regístrate aquí
            </a>
          </p>
          <a
            href="#"
            className="text-gray-500 hover:text-gray-700 text-sm inline-block"
          >
            ← Volver al inicio
          </a>
        </div>
      </div>
    </div>
  );
}