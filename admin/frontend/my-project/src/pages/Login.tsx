import LoginForm from "../components/LoginForm";

export default function LoginPage() {
  return (
    <div className="flex min-h-screen flex-col md:flex-row bg-gray-900">
      {/* Left Section */}
      <div className="flex-1 flex flex-col justify-center px-8 py-12 md:px-12 lg:px-20 bg-slate-900">
        <div className="max-w-md w-full mx-auto">
          {/* Logo */}
          <div className="mb-10">
            <div className="w-10 h-10 bg-gradient-to-br from-blue-400 to-purple-600 rounded-lg flex items-center justify-center shadow-lg">
              <span className="text-white font-bold text-lg">~</span>
            </div>
          </div>

          {/* Heading */}
          <h1 className="text-4xl font-bold text-white mb-4">
            Inicia Sesión como Administrador
          </h1>
          <p className="text-gray-400 mb-8">
            <a
              href="#"
              className="text-blue-400 hover:text-blue-300 font-medium transition-colors"
            >
              Ingresa tus credenciales para continuar.
            </a>
          </p>

          {/* Login Form */}
          <LoginForm />
        </div>
      </div>

      {/* Right Section - Image */}
      <div className="flex-1 relative hidden md:flex items-center justify-center bg-gradient-to-br from-gray-100 to-gray-200 overflow-hidden">
        <div
          className="absolute inset-0 bg-cover bg-center opacity-100"
          style={{
            backgroundImage:
              'url("https://www1.tecsup.edu.pe/sites/default/files/branches/image_mini/lima_0.png")',
          }}
        />
        <div className="absolute inset-0 bg-white/30 backdrop-blur-sm"></div>

        {/* Decorative Elements */}
        <div className="relative z-10 flex flex-col items-center justify-center text-center p-6">
          <h2 className="text-3xl font-light text-gray-800 mb-2">
            EDUQUESTIA
          </h2>
          <p className="text-gray-600">Aprende jugando, progresa aprendiendo</p>
        </div>
      </div>
    </div>
  );
}