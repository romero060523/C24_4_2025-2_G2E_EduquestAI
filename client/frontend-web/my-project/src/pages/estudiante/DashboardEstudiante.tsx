import React from "react";
import { useAuth } from "../../hooks/useAuth";

const DashboardEstudiante: React.FC = () => {
  const { usuario } = useAuth();

  return (
    <div>
      <h1 className="text-3xl font-bold mb-2">
        ¡Bienvenido, {usuario?.nombre || "Estudiante"}! 🎓
      </h1>
      <p className="text-gray-600 mb-6">
        Aquí podrás ver tu progreso y misiones disponibles
      </p>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {/* Tarjeta de Puntos */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-medium text-gray-600">
              Puntos Totales
            </h3>
            <span className="text-2xl">⭐</span>
          </div>
          <p className="text-3xl font-bold text-gray-900">0</p>
          <p className="text-xs text-gray-500 mt-1">Nivel: Principiante</p>
        </div>

        {/* Tarjeta de Misiones Completadas */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-medium text-gray-600">Completadas</h3>
            <span className="text-2xl">✅</span>
          </div>
          <p className="text-3xl font-bold text-gray-900">0</p>
          <p className="text-xs text-gray-500 mt-1">de 0 misiones</p>
        </div>

        {/* Tarjeta de Racha */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-medium text-gray-600">Racha Actual</h3>
            <span className="text-2xl">🔥</span>
          </div>
          <p className="text-3xl font-bold text-gray-900">0</p>
          <p className="text-xs text-gray-500 mt-1">días consecutivos</p>
        </div>

        {/* Tarjeta de Posición */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-medium text-gray-600">Tu Posición</h3>
            <span className="text-2xl">🏆</span>
          </div>
          <p className="text-3xl font-bold text-gray-900">-</p>
          <p className="text-xs text-gray-500 mt-1">en el ranking</p>
        </div>
      </div>

      {/* Misiones Disponibles */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 className="text-xl font-bold mb-4">Misiones Disponibles</h2>
        <div className="text-center py-12">
          <p className="text-gray-500 text-lg mb-2">
            No hay misiones disponibles aún
          </p>
          <p className="text-gray-400 text-sm">
            Tus profesores pronto crearán misiones para ti
          </p>
        </div>
      </div>
    </div>
  );
};

export default DashboardEstudiante;
