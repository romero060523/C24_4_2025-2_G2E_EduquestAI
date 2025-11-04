import { useEffect, useState } from "react";
import { createCurso, updateCurso } from "../services/cursosApi";
import type { Curso, CursoPayload } from "../types";

type Mode = "create" | "edit";

interface CursoFormProps {
  open: boolean;
  mode: Mode;
  initial?: Curso | null;
  onClose: () => void;
  onSaved: () => void;
}

export default function CursoForm({
  open,
  mode,
  initial,
  onClose,
  onSaved,
}: CursoFormProps) {
  const [form, setForm] = useState<CursoPayload>({
    codigo_curso: "",
    nombre: "",
    descripcion: "",
    imagen_portada: "",
    fecha_inicio: "",
    fecha_fin: "",
    activo: true,
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (mode === "edit" && initial) {
      setForm({
        codigo_curso: initial.codigo_curso,
        nombre: initial.nombre,
        descripcion: initial.descripcion || "",
        imagen_portada: initial.imagen_portada || "",
        fecha_inicio: initial.fecha_inicio || "",
        fecha_fin: initial.fecha_fin || "",
        activo: initial.activo,
      });
    } else {
      setForm({
        codigo_curso: "",
        nombre: "",
        descripcion: "",
        imagen_portada: "",
        fecha_inicio: "",
        fecha_fin: "",
        activo: true,
      });
    }
    setError(null);
  }, [mode, initial, open]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const target = e.target;
    const name = target.name as keyof CursoPayload;
    const value =
      (target as HTMLInputElement).type === "checkbox"
        ? (target as HTMLInputElement).checked
        : target.value;
    setForm((prev) => ({
      ...prev,
      [name]: value as CursoPayload[typeof name],
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      // Limpiar campos vacíos
      const payload: CursoPayload = {
        ...form,
        descripcion: form.descripcion || "",
        imagen_portada: form.imagen_portada || "",
        fecha_inicio: form.fecha_inicio || "",
        fecha_fin: form.fecha_fin || "",
      };

      if (mode === "create") {
        await createCurso(payload);
      } else if (mode === "edit" && initial) {
        await updateCurso(initial.id, payload);
      }
      onSaved();
      onClose();
    } catch (err) {
      const axiosErr = err as { response?: { data?: unknown } };
      const msg = axiosErr.response?.data
        ? JSON.stringify(axiosErr.response.data)
        : "Error al guardar curso";
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center backdrop-blur-sm p-4">
      <div className="w-full max-w-2xl rounded-lg bg-white p-6 shadow-lg max-h-[90vh] overflow-y-auto">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-xl font-semibold text-gray-800">
            {mode === "create" ? "Nuevo curso" : "Editar curso"}
          </h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700"
          >
            ✕
          </button>
        </div>

        {error && (
          <div className="mb-4 rounded border border-red-200 bg-red-50 p-3 text-red-700 text-sm">
            {error}
          </div>
        )}

        <form className="space-y-4" onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">
                Código del Curso *
              </label>
              <input
                name="codigo_curso"
                value={form.codigo_curso}
                onChange={handleChange}
                className="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
                placeholder="CS101"
                required
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">
                Nombre *
              </label>
              <input
                name="nombre"
                value={form.nombre}
                onChange={handleChange}
                className="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
                placeholder="Introducción a la Programación"
                required
              />
            </div>
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">
              Descripción
            </label>
            <textarea
              name="descripcion"
              value={form.descripcion || ""}
              onChange={handleChange}
              rows={3}
              className="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none resize-none"
              placeholder="Descripción del curso..."
            />
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">
              URL Imagen de Portada
            </label>
            <input
              name="imagen_portada"
              value={form.imagen_portada || ""}
              onChange={handleChange}
              className="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
              placeholder="https://..."
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">
                Fecha Inicio
              </label>
              <input
                type="date"
                name="fecha_inicio"
                value={form.fecha_inicio || ""}
                onChange={handleChange}
                className="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">
                Fecha Fin
              </label>
              <input
                type="date"
                name="fecha_fin"
                value={form.fecha_fin || ""}
                onChange={handleChange}
                className="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
              />
            </div>
          </div>

          <div className="flex items-center gap-2">
            <input
              type="checkbox"
              name="activo"
              checked={form.activo}
              onChange={handleChange}
            />
            <label className="text-sm text-gray-700">Curso Activo</label>
          </div>

          <div className="mt-6 flex items-center justify-end gap-3">
            <button
              type="button"
              onClick={onClose}
              className="rounded border px-4 py-2 text-gray-700 hover:bg-gray-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loading}
              className="rounded bg-blue-600 px-4 py-2 font-semibold text-white hover:bg-blue-700 disabled:opacity-60"
            >
              {loading
                ? "Guardando..."
                : mode === "create"
                ? "Crear"
                : "Guardar"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
