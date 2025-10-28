import React, { useState, useEffect } from 'react';
import { X } from 'lucide-react';
import { apiService } from '../services/api';
import type { CrearMisionDTO, Curso } from '../types';

interface Props {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

const CrearMisionModal: React.FC<Props> = ({ isOpen, onClose, onSuccess }) => {
  const [cursos, setCursos] = useState<Curso[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState<CrearMisionDTO>({
    titulo: '',
    descripcion: '',
    tipoMision: 'INDIVIDUAL',
    categoria: 'LECTURA',
    dificultad: 'FACIL',
    puntosRecompensa: 100,
    experienciaRecompensa: 50,
    fechaInicio: '',
    fechaLimite: '',
    cursoId: '',
    requisitosPrevios: '',
  });

  useEffect(() => {
    if (isOpen) {
      loadCursos();
      // Establecer fecha de inicio por defecto (hoy)
      const today = new Date().toISOString().split('T')[0];
      setFormData(prev => ({ ...prev, fechaInicio: today }));
    } else {
      // Limpiar error cuando se cierra el modal
      setError(null);
    }
  }, [isOpen]);

  const loadCursos = async () => {
    setError(null);
    try {
      const profesorId = localStorage.getItem('profesorId') || '';
      console.log('Cargando cursos para profesor:', profesorId);
      const cursosData = await apiService.listarCursosPorProfesor(profesorId);
      console.log('Cursos recibidos:', cursosData);
      setCursos(cursosData);
      if (cursosData.length > 0) {
        setFormData(prev => ({ ...prev, cursoId: cursosData[0].id }));
      }
    } catch (e: unknown) {
      console.error('Error cargando cursos:', e);
      const errorMessage = e instanceof Error ? e.message : 'No se pudieron cargar los cursos';
      setError(errorMessage);
    }
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'puntosRecompensa' || name === 'experienciaRecompensa' 
        ? parseInt(value) || 0 
        : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      // Convertir fechas a formato LocalDateTime (agregar hora 00:00:00)
      const misionData = {
        ...formData,
        fechaInicio: formData.fechaInicio ? `${formData.fechaInicio}T00:00:00` : '',
        fechaLimite: formData.fechaLimite ? `${formData.fechaLimite}T23:59:59` : '',
      };
      
      await apiService.crearMision(misionData);
      onSuccess();
      onClose();
      // Limpiar formulario
      setFormData({
        titulo: '',
        descripcion: '',
        tipoMision: 'INDIVIDUAL',
        categoria: 'LECTURA',
        dificultad: 'FACIL',
        puntosRecompensa: 100,
        experienciaRecompensa: 50,
        fechaInicio: '',
        fechaLimite: '',
        cursoId: '',
        requisitosPrevios: '',
      });
    } catch (e: unknown) {
      const errorMessage = e instanceof Error ? e.message : 'Error al crear la misión';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <h2 className="text-2xl font-bold text-gray-900">Nueva Misión</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
              {error}
            </div>
          )}

          {/* Título */}
          <div>
            <label htmlFor="titulo" className="block text-sm font-medium text-gray-700 mb-1">
              Título <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              id="titulo"
              name="titulo"
              value={formData.titulo}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Ej: Resolver problemas de álgebra lineal"
            />
          </div>

          {/* Descripción */}
          <div>
            <label htmlFor="descripcion" className="block text-sm font-medium text-gray-700 mb-1">
              Descripción <span className="text-red-500">*</span>
            </label>
            <textarea
              id="descripcion"
              name="descripcion"
              value={formData.descripcion}
              onChange={handleChange}
              required
              rows={3}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Describe la misión en detalle..."
            />
          </div>

          {/* Curso */}
          <div>
            <label htmlFor="cursoId" className="block text-sm font-medium text-gray-700 mb-1">
              Curso <span className="text-red-500">*</span>
            </label>
            <select
              id="cursoId"
              name="cursoId"
              value={formData.cursoId}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Selecciona un curso</option>
              {cursos.map(curso => (
                <option key={curso.id} value={curso.id}>
                  {curso.nombre}
                </option>
              ))}
            </select>
          </div>

          {/* Tipo y Categoría */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label htmlFor="tipoMision" className="block text-sm font-medium text-gray-700 mb-1">
                Tipo <span className="text-red-500">*</span>
              </label>
              <select
                id="tipoMision"
                name="tipoMision"
                value={formData.tipoMision}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="INDIVIDUAL">Individual</option>
                <option value="GRUPAL">Grupal</option>
              </select>
            </div>

            <div>
              <label htmlFor="categoria" className="block text-sm font-medium text-gray-700 mb-1">
                Categoría <span className="text-red-500">*</span>
              </label>
              <select
                id="categoria"
                name="categoria"
                value={formData.categoria}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="LECTURA">Lectura</option>
                <option value="EJERCICIO">Ejercicio</option>
                <option value="PROYECTO">Proyecto</option>
                <option value="QUIZ">Quiz</option>
                <option value="DESAFIO">Desafío</option>
              </select>
            </div>
          </div>

          {/* Dificultad */}
          <div>
            <label htmlFor="dificultad" className="block text-sm font-medium text-gray-700 mb-1">
              Dificultad <span className="text-red-500">*</span>
            </label>
            <select
              id="dificultad"
              name="dificultad"
              value={formData.dificultad}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="FACIL">Fácil</option>
              <option value="MEDIO">Medio</option>
              <option value="DIFICIL">Difícil</option>
              <option value="EXPERTO">Experto</option>
            </select>
          </div>

          {/* Puntos y Experiencia */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label htmlFor="puntosRecompensa" className="block text-sm font-medium text-gray-700 mb-1">
                Puntos <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                id="puntosRecompensa"
                name="puntosRecompensa"
                value={formData.puntosRecompensa}
                onChange={handleChange}
                required
                min="0"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label htmlFor="experienciaRecompensa" className="block text-sm font-medium text-gray-700 mb-1">
                Experiencia <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                id="experienciaRecompensa"
                name="experienciaRecompensa"
                value={formData.experienciaRecompensa}
                onChange={handleChange}
                required
                min="0"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          {/* Fechas */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label htmlFor="fechaInicio" className="block text-sm font-medium text-gray-700 mb-1">
                Fecha Inicio <span className="text-red-500">*</span>
              </label>
              <input
                type="date"
                id="fechaInicio"
                name="fechaInicio"
                value={formData.fechaInicio}
                onChange={handleChange}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label htmlFor="fechaLimite" className="block text-sm font-medium text-gray-700 mb-1">
                Fecha Límite <span className="text-red-500">*</span>
              </label>
              <input
                type="date"
                id="fechaLimite"
                name="fechaLimite"
                value={formData.fechaLimite}
                onChange={handleChange}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          {/* Requisitos Previos */}
          <div>
            <label htmlFor="requisitosPrevios" className="block text-sm font-medium text-gray-700 mb-1">
              Requisitos Previos (opcional)
            </label>
            <textarea
              id="requisitosPrevios"
              name="requisitosPrevios"
              value={formData.requisitosPrevios}
              onChange={handleChange}
              rows={2}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Ej: Haber completado la misión anterior..."
            />
          </div>

          {/* Botones */}
          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Creando...' : 'Crear Misión'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CrearMisionModal;
