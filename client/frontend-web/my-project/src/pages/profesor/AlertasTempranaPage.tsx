import React, { useState, useEffect } from "react";
import { AlertTriangle, Plus, Search, Trash2, CheckCircle, Clock, Archive } from "lucide-react";
import { apiService } from "../../services/api";
import type { AlertaTempranaResponse, CrearAlertaRequest, Curso, EstadoAlerta } from "../../types";

const AlertasTempranaPage = () => {
  const [alertas, setAlertas] = useState<AlertaTempranaResponse[]>([]);
  const [cursos, setCursos] = useState<Curso[]>([]);
  const [loading, setLoading] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [formData, setFormData] = useState<CrearAlertaRequest>({
    estudianteId: "",
    cursoId: "",
    titulo: "",
    mensaje: "",
  });

  useEffect(() => {
    loadCursos();
    loadAlertas();
  }, []);

  const loadCursos = async () => {
    try {
      const profesorId = localStorage.getItem("profesorId") || "";
      const cursosData = await apiService.listarCursosPorProfesor(profesorId);
      setCursos(cursosData);
    } catch (error) {
      console.error("Error cargando cursos:", error);
    }
  };

  const loadAlertas = async () => {
    setLoading(true);
    try {
      const profesorId = localStorage.getItem("profesorId") || "";
      const alertasData = await apiService.obtenerAlertasPorProfesor(profesorId);
      setAlertas(alertasData);
    } catch (error) {
      console.error("Error cargando alertas:", error);
    } finally {
      setLoading(false);
    }
  };


  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await apiService.crearAlertaTemprana(formData);
      setShowModal(false);
      setFormData({ estudianteId: "", cursoId: "", titulo: "", mensaje: "" });
      await loadAlertas();
    } catch (error: any) {
      alert(error?.response?.data?.message || "Error al crear la alerta");
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateEstado = async (alertaId: string, nuevoEstado: EstadoAlerta) => {
    try {
      await apiService.actualizarAlertaTemprana(alertaId, { estado: nuevoEstado });
      await loadAlertas();
    } catch (error: any) {
      alert(error?.response?.data?.message || "Error al actualizar la alerta");
    }
  };

  const handleDelete = async (alertaId: string) => {
    if (!confirm("¿Estás seguro de eliminar esta alerta?")) return;
    try {
      await apiService.eliminarAlertaTemprana(alertaId);
      await loadAlertas();
    } catch (error: any) {
      alert(error?.response?.data?.message || "Error al eliminar la alerta");
    }
  };

  const getEstadoColor = (estado: EstadoAlerta) => {
    switch (estado) {
      case "ACTIVA": return "bg-red-100 text-red-800";
      case "EN_SEGUIMIENTO": return "bg-yellow-100 text-yellow-800";
      case "RESUELTA": return "bg-green-100 text-green-800";
      case "ARCHIVADA": return "bg-gray-100 text-gray-800";
      default: return "bg-gray-100 text-gray-800";
    }
  };

  const getEstadoIcon = (estado: EstadoAlerta) => {
    switch (estado) {
      case "ACTIVA": return <AlertTriangle className="w-4 h-4" />;
      case "EN_SEGUIMIENTO": return <Clock className="w-4 h-4" />;
      case "RESUELTA": return <CheckCircle className="w-4 h-4" />;
      case "ARCHIVADA": return <Archive className="w-4 h-4" />;
      default: return <AlertTriangle className="w-4 h-4" />;
    }
  };

  const filteredAlertas = alertas.filter(alerta =>
    alerta.estudianteNombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
    alerta.titulo.toLowerCase().includes(searchTerm.toLowerCase()) ||
    alerta.cursoNombre.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Alertas Tempranas</h1>
          <p className="text-gray-600 mt-1">Gestiona alertas para estudiantes con bajo rendimiento</p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="flex items-center gap-2 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
        >
          <Plus className="w-5 h-5" />
          Nueva Alerta
        </button>
      </div>

      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="mb-4 flex gap-4">
          <div className="flex-1">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
              <input
                type="text"
                placeholder="Buscar por estudiante, título o curso..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
              />
            </div>
          </div>
        </div>

        {loading ? (
          <div className="text-center py-8">Cargando...</div>
        ) : filteredAlertas.length === 0 ? (
          <div className="text-center py-8 text-gray-500">No hay alertas registradas</div>
        ) : (
          <div className="space-y-4">
            {filteredAlertas.map((alerta) => (
              <div key={alerta.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                <div className="flex justify-between items-start mb-2">
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-2">
                      <h3 className="text-lg font-semibold text-gray-900">{alerta.titulo}</h3>
                      <span className={`px-2 py-1 rounded-full text-xs font-medium flex items-center gap-1 ${getEstadoColor(alerta.estado)}`}>
                        {getEstadoIcon(alerta.estado)}
                        {alerta.estado.replace("_", " ")}
                      </span>
                    </div>
                    <p className="text-sm text-gray-600 mb-2">{alerta.mensaje}</p>
                    <div className="flex gap-4 text-sm text-gray-500">
                      <span><strong>Estudiante:</strong> {alerta.estudianteNombre}</span>
                      <span><strong>Curso:</strong> {alerta.cursoNombre}</span>
                      <span><strong>Fecha:</strong> {new Date(alerta.fechaCreacion).toLocaleDateString()}</span>
                    </div>
                  </div>
                  <div className="flex gap-2">
                    {alerta.estado === "ACTIVA" && (
                      <button
                        onClick={() => handleUpdateEstado(alerta.id, "EN_SEGUIMIENTO")}
                        className="p-2 text-yellow-600 hover:bg-yellow-50 rounded"
                        title="Marcar en seguimiento"
                      >
                        <Clock className="w-5 h-5" />
                      </button>
                    )}
                    {alerta.estado !== "RESUELTA" && (
                      <button
                        onClick={() => handleUpdateEstado(alerta.id, "RESUELTA")}
                        className="p-2 text-green-600 hover:bg-green-50 rounded"
                        title="Marcar como resuelta"
                      >
                        <CheckCircle className="w-5 h-5" />
                      </button>
                    )}
                    <button
                      onClick={() => handleDelete(alerta.id)}
                      className="p-2 text-red-600 hover:bg-red-50 rounded"
                      title="Eliminar"
                    >
                      <Trash2 className="w-5 h-5" />
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between p-6 border-b">
              <h2 className="text-2xl font-bold">Nueva Alerta Temprana</h2>
              <button onClick={() => setShowModal(false)} className="text-gray-400 hover:text-gray-600">✕</button>
            </div>
            <form onSubmit={handleSubmit} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Curso *</label>
                <select
                  value={formData.cursoId}
                  onChange={(e) => setFormData({ ...formData, cursoId: e.target.value })}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                >
                  <option value="">Selecciona un curso</option>
                  {cursos.map((curso) => (
                    <option key={curso.id} value={curso.id}>{curso.nombre}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Estudiante *</label>
                <input
                  type="text"
                  value={formData.estudianteId}
                  onChange={(e) => setFormData({ ...formData, estudianteId: e.target.value })}
                  placeholder="ID del estudiante (por ahora manual)"
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Título *</label>
                <input
                  type="text"
                  value={formData.titulo}
                  onChange={(e) => setFormData({ ...formData, titulo: e.target.value })}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                  placeholder="Ej: Bajo rendimiento en misiones"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Mensaje *</label>
                <textarea
                  value={formData.mensaje}
                  onChange={(e) => setFormData({ ...formData, mensaje: e.target.value })}
                  required
                  rows={4}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                  placeholder="Describe el problema académico del estudiante..."
                />
              </div>
              <div className="flex gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="flex-1 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 disabled:opacity-50"
                >
                  {loading ? "Creando..." : "Crear Alerta"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AlertasTempranaPage;

