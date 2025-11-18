import { useState, useEffect } from "react";
import {
  reglasGamificacionApi,
  configuracionNivelesApi,
} from "../services/gamificacionApi";
import type {
  ReglaGamificacion,
  ConfiguracionNivel,
} from "../services/gamificacionApi";
import { Settings, Plus, Edit, Trash2, Save, X } from "lucide-react";

const TIPOS_REGLA = [
  { value: "puntos_completar_mision", label: "Puntos por Completar Misión" },
  { value: "puntos_entrega_tardia", label: "Puntos por Entrega Tardía" },
  { value: "puntos_entrega_anticipada", label: "Puntos por Entrega Anticipada" },
  { value: "multiplicador_dificultad_facil", label: "Multiplicador Dificultad Fácil" },
  { value: "multiplicador_dificultad_medio", label: "Multiplicador Dificultad Medio" },
  { value: "multiplicador_dificultad_dificil", label: "Multiplicador Dificultad Difícil" },
  { value: "puntos_bonificacion_primera_vez", label: "Bonificación Primera Vez" },
  { value: "puntos_bonificacion_racha", label: "Bonificación por Racha" },
];

export default function ReglasGamificacion() {
  const [reglas, setReglas] = useState<ReglaGamificacion[]>([]);
  const [niveles, setNiveles] = useState<ConfiguracionNivel[]>([]);
  const [loading, setLoading] = useState(true);
  const [editingRegla, setEditingRegla] = useState<string | null>(null);
  const [editingNivel, setEditingNivel] = useState<string | null>(null);
  const [showReglaForm, setShowReglaForm] = useState(false);
  const [showNivelForm, setShowNivelForm] = useState(false);
  const [formData, setFormData] = useState<Partial<ReglaGamificacion>>({});
  const [nivelFormData, setNivelFormData] = useState<Partial<ConfiguracionNivel>>({});

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      setLoading(true);
      const [reglasData, nivelesData] = await Promise.all([
        reglasGamificacionApi.listar(),
        configuracionNivelesApi.listar(),
      ]);
      setReglas(reglasData);
      setNiveles(nivelesData);
    } catch (error) {
      console.error("Error cargando datos:", error);
      alert("Error al cargar los datos");
    } finally {
      setLoading(false);
    }
  };

  const handleSaveRegla = async () => {
    try {
      if (editingRegla) {
        await reglasGamificacionApi.actualizar(editingRegla, formData);
      } else {
        await reglasGamificacionApi.crear(formData);
      }
      await cargarDatos();
      setEditingRegla(null);
      setShowReglaForm(false);
      setFormData({});
    } catch (error) {
      console.error("Error guardando regla:", error);
      alert("Error al guardar la regla");
    }
  };

  const handleSaveNivel = async () => {
    try {
      if (editingNivel) {
        await configuracionNivelesApi.actualizar(editingNivel, nivelFormData);
      } else {
        await configuracionNivelesApi.crear(nivelFormData);
      }
      await cargarDatos();
      setEditingNivel(null);
      setShowNivelForm(false);
      setNivelFormData({});
    } catch (error) {
      console.error("Error guardando nivel:", error);
      alert("Error al guardar el nivel");
    }
  };

  const handleDeleteRegla = async (id: string) => {
    if (!confirm("¿Estás seguro de eliminar esta regla?")) return;
    try {
      await reglasGamificacionApi.eliminar(id);
      await cargarDatos();
    } catch (error) {
      console.error("Error eliminando regla:", error);
      alert("Error al eliminar la regla");
    }
  };

  const handleDeleteNivel = async (id: string) => {
    if (!confirm("¿Estás seguro de eliminar este nivel?")) return;
    try {
      await configuracionNivelesApi.eliminar(id);
      await cargarDatos();
    } catch (error) {
      console.error("Error eliminando nivel:", error);
      alert("Error al eliminar el nivel");
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-gray-500">Cargando...</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Reglas */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-bold text-gray-900 flex items-center gap-2">
            <Settings className="w-6 h-6" />
            Reglas
          </h2>
          <button
            onClick={() => {
              setShowReglaForm(true);
              setEditingRegla(null);
              setFormData({});
            }}
            className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
          >
            <Plus className="w-4 h-4" />
            Nueva Regla
          </button>
        </div>

        {showReglaForm && (
          <div className="mb-6 p-4 bg-gray-50 rounded-lg border border-gray-200">
            <h3 className="font-semibold mb-4">
              {editingRegla ? "Editar Regla" : "Nueva Regla"}
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium mb-1">Tipo de Regla</label>
                <select
                  value={formData.tipo_regla || ""}
                  onChange={(e) =>
                    setFormData({ ...formData, tipo_regla: e.target.value })
                  }
                  className="w-full px-3 py-2 border rounded-lg"
                  disabled={!!editingRegla}
                >
                  <option value="">Seleccionar...</option>
                  {TIPOS_REGLA.map((tipo) => (
                    <option key={tipo.value} value={tipo.value}>
                      {tipo.label}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Valor</label>
                <input
                  type="number"
                  step="0.01"
                  value={formData.valor || ""}
                  onChange={(e) =>
                    setFormData({ ...formData, valor: parseFloat(e.target.value) })
                  }
                  className="w-full px-3 py-2 border rounded-lg"
                />
              </div>
              <div className="md:col-span-2">
                <label className="block text-sm font-medium mb-1">Descripción</label>
                <textarea
                  value={formData.descripcion || ""}
                  onChange={(e) =>
                    setFormData({ ...formData, descripcion: e.target.value })
                  }
                  className="w-full px-3 py-2 border rounded-lg"
                  rows={3}
                />
              </div>
              <div>
                <label className="flex items-center gap-2">
                  <input
                    type="checkbox"
                    checked={formData.activo ?? true}
                    onChange={(e) =>
                      setFormData({ ...formData, activo: e.target.checked })
                    }
                  />
                  <span className="text-sm">Activo</span>
                </label>
              </div>
            </div>
            <div className="flex gap-2 mt-4">
              <button
                onClick={handleSaveRegla}
                className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
              >
                <Save className="w-4 h-4" />
                Guardar
              </button>
              <button
                onClick={() => {
                  setShowReglaForm(false);
                  setFormData({});
                  setEditingRegla(null);
                }}
                className="flex items-center gap-2 px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400"
              >
                <X className="w-4 h-4" />
                Cancelar
              </button>
            </div>
          </div>
        )}

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                  Tipo
                </th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                  Valor
                </th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                  Descripción
                </th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                  Estado
                </th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                  Acciones
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {reglas.map((regla) => (
                <tr key={regla.id}>
                  <td className="px-4 py-3 text-sm">{regla.tipo_regla_display}</td>
                  <td className="px-4 py-3 text-sm font-medium">{regla.valor}</td>
                  <td className="px-4 py-3 text-sm text-gray-600">
                    {regla.descripcion || "-"}
                  </td>
                  <td className="px-4 py-3">
                    <span
                      className={`px-2 py-1 rounded-full text-xs font-medium ${
                        regla.activo
                          ? "bg-green-100 text-green-800"
                          : "bg-gray-100 text-gray-800"
                      }`}
                    >
                      {regla.activo ? "Activo" : "Inactivo"}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex gap-2">
                      <button
                        onClick={() => {
                          setEditingRegla(regla.id);
                          setFormData(regla);
                          setShowReglaForm(true);
                        }}
                        className="p-1 text-blue-600 hover:bg-blue-50 rounded"
                      >
                        <Edit className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => handleDeleteRegla(regla.id)}
                        className="p-1 text-red-600 hover:bg-red-50 rounded"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Configuración de Niveles */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-bold text-gray-900 flex items-center gap-2">
            <Settings className="w-6 h-6" />
            Configuración de Niveles
          </h2>
          <button
            onClick={() => {
              setShowNivelForm(true);
              setEditingNivel(null);
              setNivelFormData({});
            }}
            className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
          >
            <Plus className="w-4 h-4" />
            Nuevo Nivel
          </button>
        </div>

        {showNivelForm && (
          <div className="mb-6 p-4 bg-gray-50 rounded-lg border border-gray-200">
            <h3 className="font-semibold mb-4">
              {editingNivel ? "Editar Nivel" : "Nuevo Nivel"}
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium mb-1">Nivel</label>
                <input
                  type="number"
                  min="1"
                  max="20"
                  value={nivelFormData.nivel || ""}
                  onChange={(e) =>
                    setNivelFormData({
                      ...nivelFormData,
                      nivel: parseInt(e.target.value),
                    })
                  }
                  className="w-full px-3 py-2 border rounded-lg"
                  disabled={!!editingNivel}
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Nombre</label>
                <input
                  type="text"
                  value={nivelFormData.nombre || ""}
                  onChange={(e) =>
                    setNivelFormData({ ...nivelFormData, nombre: e.target.value })
                  }
                  className="w-full px-3 py-2 border rounded-lg"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">
                  Puntos Mínimos
                </label>
                <input
                  type="number"
                  min="0"
                  value={nivelFormData.puntos_minimos || ""}
                  onChange={(e) =>
                    setNivelFormData({
                      ...nivelFormData,
                      puntos_minimos: parseInt(e.target.value),
                    })
                  }
                  className="w-full px-3 py-2 border rounded-lg"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">
                  Puntos Máximos (opcional)
                </label>
                <input
                  type="number"
                  min="0"
                  value={nivelFormData.puntos_maximos || ""}
                  onChange={(e) =>
                    setNivelFormData({
                      ...nivelFormData,
                      puntos_maximos: e.target.value
                        ? parseInt(e.target.value)
                        : null,
                    })
                  }
                  className="w-full px-3 py-2 border rounded-lg"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Icono</label>
                <input
                  type="text"
                  value={nivelFormData.icono || ""}
                  onChange={(e) =>
                    setNivelFormData({ ...nivelFormData, icono: e.target.value })
                  }
                  className="w-full px-3 py-2 border rounded-lg"
                  placeholder="🌱"
                />
              </div>
              <div className="md:col-span-2">
                <label className="block text-sm font-medium mb-1">Descripción</label>
                <textarea
                  value={nivelFormData.descripcion || ""}
                  onChange={(e) =>
                    setNivelFormData({ ...nivelFormData, descripcion: e.target.value })
                  }
                  className="w-full px-3 py-2 border rounded-lg"
                  rows={2}
                />
              </div>
            </div>
            <div className="flex gap-2 mt-4">
              <button
                onClick={handleSaveNivel}
                className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
              >
                <Save className="w-4 h-4" />
                Guardar
              </button>
              <button
                onClick={() => {
                  setShowNivelForm(false);
                  setNivelFormData({});
                  setEditingNivel(null);
                }}
                className="flex items-center gap-2 px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400"
              >
                <X className="w-4 h-4" />
                Cancelar
              </button>
            </div>
          </div>
        )}

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                  Nivel
                </th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                  Nombre
                </th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                  Puntos Mínimos
                </th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                  Puntos Máximos
                </th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-900">
                  Acciones
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {niveles
                .sort((a, b) => a.nivel - b.nivel)
                .map((nivel) => (
                  <tr key={nivel.id}>
                    <td className="px-4 py-3 text-sm font-medium">
                      {nivel.icono} {nivel.nivel}
                    </td>
                    <td className="px-4 py-3 text-sm">{nivel.nombre}</td>
                    <td className="px-4 py-3 text-sm">{nivel.puntos_minimos}</td>
                    <td className="px-4 py-3 text-sm">
                      {nivel.puntos_maximos || "∞"}
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex gap-2">
                        <button
                          onClick={() => {
                            setEditingNivel(nivel.id);
                            setNivelFormData(nivel);
                            setShowNivelForm(true);
                          }}
                          className="p-1 text-blue-600 hover:bg-blue-50 rounded"
                        >
                          <Edit className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => handleDeleteNivel(nivel.id)}
                          className="p-1 text-red-600 hover:bg-red-50 rounded"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

