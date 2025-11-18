// src/pages/profesor/EstudiantesPage.tsx
import React, { useState, useEffect } from 'react';
import { Gift } from 'lucide-react';
import { apiService } from '../../services/api';
import OtorgarRecompensaModal from '../../components/OtorgarRecompensaModal';
import type { EstudianteSimple } from '../../types';

const EstudiantesPage: React.FC = () => {
  const [estudiantes, setEstudiantes] = useState<EstudianteSimple[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [estudianteSeleccionado, setEstudianteSeleccionado] = useState<EstudianteSimple | undefined>();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    setError(null);
    try {
      const estudiantesData = await apiService.listarTodosLosEstudiantes();
      setEstudiantes(estudiantesData);
    } catch (e: unknown) {
      const errorMessage = e instanceof Error ? e.message : 'Error cargando datos';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleOtorgarRecompensa = (estudiante: EstudianteSimple) => {
    setEstudianteSeleccionado(estudiante);
    setIsModalOpen(true);
  };

  const handleRecompensaOtorgada = () => {
    loadData();
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-gray-600">Cargando estudiantes...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
        Error: {error}
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-start justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Estudiantes</h1>
          <p className="text-gray-500 mt-1">Gestiona y otorga recompensas a tus estudiantes</p>
        </div>
        <button
          onClick={() => {
            setEstudianteSeleccionado(undefined);
            setIsModalOpen(true);
          }}
          className="flex items-center gap-2 bg-yellow-500 hover:bg-yellow-600 text-white px-4 py-2 rounded-lg font-medium transition-colors"
        >
          <Gift className="w-5 h-5" />
          Otorgar Recompensa
        </button>
      </div>

      {/* Lista de estudiantes */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Estudiante
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Email
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Username
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Acciones
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {estudiantes.length === 0 ? (
                <tr>
                  <td colSpan={4} className="px-6 py-4 text-center text-gray-500">
                    No hay estudiantes disponibles
                  </td>
                </tr>
              ) : (
                estudiantes.map((estudiante) => (
                  <tr key={estudiante.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        {estudiante.avatarUrl ? (
                          <img
                            src={estudiante.avatarUrl}
                            alt={estudiante.nombreCompleto}
                            className="h-10 w-10 rounded-full mr-3"
                          />
                        ) : (
                          <div className="h-10 w-10 rounded-full bg-blue-500 flex items-center justify-center text-white font-medium mr-3">
                            {estudiante.nombreCompleto.charAt(0).toUpperCase()}
                          </div>
                        )}
                        <div>
                          <div className="text-sm font-medium text-gray-900">
                            {estudiante.nombreCompleto}
                          </div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {estudiante.email}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {estudiante.username}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                      <button
                        onClick={() => handleOtorgarRecompensa(estudiante)}
                        className="inline-flex items-center gap-1 text-yellow-600 hover:text-yellow-800 font-medium"
                      >
                        <Gift className="w-4 h-4" />
                        Otorgar Recompensa
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal de recompensa */}
      <OtorgarRecompensaModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setEstudianteSeleccionado(undefined);
        }}
        onSuccess={handleRecompensaOtorgada}
        estudiante={estudianteSeleccionado}
      />
    </div>
  );
};

export default EstudiantesPage;
