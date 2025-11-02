import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

type LoginForm = { email: string; password: string };

const LoginProfesorPage: React.FC = () => {
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<LoginForm>();
  const { login } = useAuth();
  const navigate = useNavigate();
  const [loginError, setLoginError] = useState<string>('');

  const onSubmit = async (data: LoginForm) => {
    try {
      setLoginError('');
      await login(data.email, data.password);
      // Redirigir al dashboard después del login exitoso
      navigate('/profesor/inicio');
    } catch (error) {
      setLoginError('Credenciales inválidas. Por favor, intenta de nuevo.');
      console.error('Error en login:', error);
    }
  };

  return (
    <div className="min-h-screen flex flex-col justify-center items-center bg-gradient-to-br from-gray-100 to-gray-200">
      <div className="mb-8 text-3xl font-bold text-blue-800 flex items-center gap-3">
        <span>
          <svg className="inline w-8 h-8 text-blue-700" fill="none" viewBox="0 0 24 24">
            <path fill="currentColor" d="M12 1l8 5-8 5-8-5 8-5zm0 7.5l8 5-3.5 2-4.5 2.5-4.5-2.5L4 13.5l8-5zm0 10v3l6-3.42V13l-6 3.5z"/>
          </svg>
        </span>
        EduQuest
      </div>
      <div className="bg-white px-8 py-10 shadow-lg rounded-xl w-[95%] max-w-sm">
        <h2 className="text-xl font-semibold text-gray-900 mb-2 text-center">Bienvenido profesor</h2>
        <p className="text-gray-600 text-center text-sm mb-6">Ingresa tus credenciales para continuar</p>
        
        {loginError && (
          <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
            {loginError}
          </div>
        )}
        
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
          <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-800 mb-1">
              Correo electrónico
            </label>
            <input
              type="email"
              id="email"
              autoComplete="email"
              {...register('email', { required: 'El correo es obligatorio' })}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-blue-500"
              placeholder="profesor@email.com"
            />
            {errors.email && <div className="text-red-500 text-xs mt-1">{errors.email.message}</div>}
          </div>
          <div>
            <label htmlFor="password" className="block text-sm font-medium text-gray-800 mb-1">
              Contraseña
            </label>
            <input
              type="password"
              id="password"
              autoComplete="current-password"
              {...register('password', { required: 'La contraseña es obligatoria' })}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-blue-500"
              placeholder="••••••••"
            />
            {errors.password && <div className="text-red-500 text-xs mt-1">{errors.password.message}</div>}
          </div>
          <div className="flex items-center justify-between text-sm">
            <label className="flex items-center">
              <input
                type="checkbox"
                className="rounded border-gray-300"
                disabled
              />
              <span className="ml-2 text-gray-600 opacity-60">Recordarme</span>
            </label>
            <a href="#" className="text-blue-600 hover:underline text-xs font-medium">
              ¿Olvidaste tu contraseña?
            </a>
          </div>
          <button
            type="submit"
            className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 rounded-lg transition"
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Iniciando...' : 'Iniciar sesión'}
          </button>
        </form>
        <div className="mt-5 text-center text-sm text-gray-500">
          ¿No tienes una cuenta? <a href="#" className="text-blue-500 hover:underline">Regístrate aquí</a>
        </div>
        <div className="mt-2 text-center">
          <a href="/" className="text-gray-600 text-xs hover:underline">← Volver al inicio</a>
        </div>
      </div>
    </div>
  );
};

export default LoginProfesorPage;
