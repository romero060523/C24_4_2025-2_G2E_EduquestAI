import React, { useEffect, useState } from 'react';
import { apiService } from '../services/api';
import type { MisionListResponse } from '../types';
import MisionList from '../components/MisionList';
import CrearMisionModal from '../components/CrearMisionModal';
import { Plus } from 'lucide-react';

const MisionesPage: React.FC = () => {
  const [misiones, setMisiones] = useState<MisionListResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  useEffect(() => {
    loadMisiones();
  }, []);

  const loadMisiones = async () => {
    setLoading(true);
    setError(null);
    try {
      const profesorId = localStorage.getItem('profesorId') || '709be72f-3f2c-4a51-8f2f-815fece92bda';
      const result = await apiService.listarMisionesPorProfesor(profesorId);
      setMisiones(result);
    } catch (e: unknown) {
      const errorMessage = e instanceof Error ? e.message : 'Error cargando misiones';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleNuevaMision = () => {
    setIsModalOpen(true);
  };

  const handleMisionCreated = () => {
    loadMisiones();
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-gray-600">Cargando misiones...</div>
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
          <h1 className="text-3xl font-bold text-gray-900">Misiones</h1>
          <p className="text-gray-500 mt-1">Crea y gestiona misiones gamificadas</p>
        </div>
        <button
          onClick={handleNuevaMision}
          className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg font-medium transition-colors"
        >
          <Plus className="w-5 h-5" />
          Nueva Misión
        </button>
      </div>

      {/* Lista de misiones */}
      <MisionList misiones={misiones} onMisionUpdated={loadMisiones} />

      {/* Modal de creación */}
      <CrearMisionModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSuccess={handleMisionCreated}
      />
    </div>
  );
};

export default MisionesPage;
