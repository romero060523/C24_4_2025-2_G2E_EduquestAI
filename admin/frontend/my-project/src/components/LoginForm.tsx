import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Mail, Lock } from "lucide-react";
import API from "../services/api";

export default function LoginForm() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [rememberMe, setRememberMe] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const res = await API.post("/token/", { email, password });
      localStorage.setItem("accessToken", res.data.access);
      localStorage.setItem("refreshToken", res.data.refresh);
      navigate("/admin/dashboard");
    } catch (err) {
      console.error("Login error:", err);
      setError("Credenciales incorrectas, intente de nuevo.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form className="space-y-5" onSubmit={handleSubmit}>
      {/* Email Field */}
      <div>
        <label className="block text-gray-700 text-sm font-medium mb-2">
          Correo electrónico
        </label>
        <div className="relative">
          <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="tu@email.com"
            className="w-full pl-10 pr-4 py-3 bg-gray-50 border border-gray-200 rounded-lg text-gray-800 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition"
            required
          />
        </div>
      </div>

      {/* Password Field */}
      <div>
        <label className="block text-gray-700 text-sm font-medium mb-2">
          Contraseña
        </label>
        <div className="relative">
          <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••••"
            className="w-full pl-10 pr-4 py-3 bg-gray-50 border border-gray-200 rounded-lg text-gray-800 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition"
            required
          />
        </div>
      </div>

      {/* Remember Me & Forgot Password */}
      <div className="flex items-center justify-between">
        <label className="flex items-center cursor-pointer">
          <input
            type="checkbox"
            checked={rememberMe}
            onChange={(e) => setRememberMe(e.target.checked)}
            className="w-4 h-4 text-tema-primario border-gray-300 rounded focus:ring-2"
            style={{ 
              '--tw-ring-color': 'var(--color-primario)',
              color: 'var(--color-primario)'
            } as React.CSSProperties}
          />
          <span className="ml-2 text-sm text-gray-600">Recordarme</span>
        </label>
        <a
          href="#"
          className="text-sm text-tema-primario hover:opacity-80 font-medium"
          style={{ color: 'var(--color-primario)' }}
        >
          ¿Olvidaste tu contraseña?
        </a>
      </div>

      {/* Error Message */}
      {error && (
        <div className="p-3 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-red-600 text-sm">{error}</p>
        </div>
      )}

      {/* Submit Button */}
      <button
        type="submit"
        className="w-full py-3 bg-tema-primario hover:opacity-90 text-white font-semibold rounded-lg transition shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
        style={{ backgroundColor: 'var(--color-primario)' }}
        disabled={loading}
      >
        {loading ? "Ingresando..." : "Iniciar sesión"}
      </button>
    </form>
  );
}