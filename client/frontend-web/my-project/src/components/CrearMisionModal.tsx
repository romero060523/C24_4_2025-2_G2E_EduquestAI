import React, { useState, useEffect } from "react";
import { X, Plus, Trash2, Upload, CheckCircle2, Image, Video, FileText } from "lucide-react";
import { apiService } from "../services/api";
import type { CrearMisionDTO, Curso, ContenidoMision, TipoContenido } from "../types";

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
    titulo: "",
    descripcion: "",
    tipoMision: "INDIVIDUAL",
    categoria: "LECTURA",
    dificultad: "FACIL",
    puntosRecompensa: 100,
    experienciaRecompensa: 50,
    fechaInicio: "",
    fechaLimite: "",
    cursoId: "",
    requisitosPrevios: "",
    contenidos: [],
  });

  const [contenidos, setContenidos] = useState<ContenidoMision[]>([]);
  const [tabActiva, setTabActiva] = useState<{ [key: number]: "archivo" | "url" }>({});
  const [archivosSubidos, setArchivosSubidos] = useState<{ [key: number]: { nombre: string; tamaño: number } }>({});
  const [subiendoArchivo, setSubiendoArchivo] = useState<{ [key: number]: boolean }>({});

  useEffect(() => {
    if (isOpen) {
      loadCursos();
      // Establecer fecha de inicio por defecto (hoy)
      const today = new Date().toISOString().split("T")[0];
      setFormData((prev) => ({ ...prev, fechaInicio: today }));
    } else {
      // Limpiar error cuando se cierra el modal
      setError(null);
      // Limpiar estados de archivos
      setArchivosSubidos({});
      setSubiendoArchivo({});
    }
  }, [isOpen]);

  // Función para formatear el tamaño del archivo
  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return "0 Bytes";
    const k = 1024;
    const sizes = ["Bytes", "KB", "MB", "GB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + " " + sizes[i];
  };

  const loadCursos = async () => {
    setError(null);
    try {
      const profesorId = localStorage.getItem("profesorId") || "";
      console.log("Cargando cursos para profesor:", profesorId);
      const cursosData = await apiService.listarCursosPorProfesor(profesorId);
      console.log("Cursos recibidos:", cursosData);
      setCursos(cursosData);
      if (cursosData.length > 0) {
        setFormData((prev) => ({ ...prev, cursoId: cursosData[0].id }));
      }
    } catch (e: unknown) {
      console.error("Error cargando cursos:", e);
      const errorMessage =
        e instanceof Error ? e.message : "No se pudieron cargar los cursos";
      setError(errorMessage);
    }
  };

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]:
        name === "puntosRecompensa" || name === "experienciaRecompensa"
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
        fechaInicio: formData.fechaInicio
          ? `${formData.fechaInicio}T00:00:00`
          : "",
        fechaLimite: formData.fechaLimite
          ? `${formData.fechaLimite}T23:59:59`
          : "",
      };

      const misionDataWithContenidos = {
        ...misionData,
        contenidos: contenidos.map((c, index) => ({
          ...c,
          orden: index + 1,
        })),
      };

      await apiService.crearMision(misionDataWithContenidos);
      onSuccess();
      onClose();
      // Limpiar formulario
      setFormData({
        titulo: "",
        descripcion: "",
        tipoMision: "INDIVIDUAL",
        categoria: "LECTURA",
        dificultad: "FACIL",
        puntosRecompensa: 100,
        experienciaRecompensa: 50,
        fechaInicio: "",
        fechaLimite: "",
        cursoId: "",
        requisitosPrevios: "",
        contenidos: [],
      });
      setContenidos([]);
      setTabActiva({});
    } catch (e: unknown) {
      const errorMessage =
        e instanceof Error ? e.message : "Error al crear la misión";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 backdrop-blur-sm flex items-center justify-center z-50 p-4">
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
            <label
              htmlFor="titulo"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
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
            <label
              htmlFor="descripcion"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
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
            <label
              htmlFor="cursoId"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
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
              {cursos.map((curso) => (
                <option key={curso.id} value={curso.id}>
                  {curso.nombre}
                </option>
              ))}
            </select>
          </div>

          {/* Tipo y Categoría */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label
                htmlFor="tipoMision"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
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
              <label
                htmlFor="categoria"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
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
            <label
              htmlFor="dificultad"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
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
              <label
                htmlFor="puntosRecompensa"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
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
              <label
                htmlFor="experienciaRecompensa"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
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
              <label
                htmlFor="fechaInicio"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
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
              <label
                htmlFor="fechaLimite"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
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
            <label
              htmlFor="requisitosPrevios"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
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

          {/* Contenido Externo */}
          <div>
            <div className="flex items-center justify-between mb-2">
              <label className="block text-sm font-medium text-gray-700">
                Contenido Externo (Videos, PDFs, Links)
              </label>
              <button
                type="button"
                onClick={() => {
                  const nuevoIndex = contenidos.length;
                  setContenidos([
                    ...contenidos,
                    {
                      tipoContenido: "VIDEO",
                      titulo: "",
                      contenidoUrl: "",
                      orden: nuevoIndex + 1,
                    },
                  ]);
                  // Por defecto, activar la pestaña de archivo para PDF, IMAGEN, VIDEO
                  setTabActiva({ ...tabActiva, [nuevoIndex]: "archivo" });
                }}
                className="flex items-center gap-1 text-sm text-blue-600 hover:text-blue-700 font-medium"
              >
                <Plus className="w-4 h-4" />
                Agregar Contenido
              </button>
            </div>

            {contenidos.length > 0 && (
              <div className="space-y-3 border border-gray-200 rounded-lg p-4 bg-gray-50">
                {contenidos.map((contenido, index) => (
                  <div
                    key={index}
                    className="bg-white p-4 rounded-lg border border-gray-200"
                  >
                    <div className="flex items-start justify-between mb-3">
                      <h4 className="text-sm font-medium text-gray-700">
                        Contenido #{index + 1}
                      </h4>
                      <button
                        type="button"
                        onClick={() => {
                          setContenidos(contenidos.filter((_, i) => i !== index));
                          // Limpiar la pestaña activa de este índice
                          const nuevasTabs = { ...tabActiva };
                          delete nuevasTabs[index];
                          setTabActiva(nuevasTabs);
                          // Limpiar información del archivo subido
                          const nuevosArchivos = { ...archivosSubidos };
                          delete nuevosArchivos[index];
                          setArchivosSubidos(nuevosArchivos);
                          // Limpiar estado de subida
                          const nuevasSubidas = { ...subiendoArchivo };
                          delete nuevasSubidas[index];
                          setSubiendoArchivo(nuevasSubidas);
                        }}
                        className="text-red-600 hover:text-red-700"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>

                    <div className="space-y-3">
                      {/* Tipo de Contenido */}
                      <div>
                        <label className="block text-xs font-medium text-gray-600 mb-1">
                          Tipo de Contenido
                        </label>
                        <select
                          value={contenido.tipoContenido}
                          onChange={(e) => {
                            const nuevosContenidos = [...contenidos];
                            const nuevoTipo = e.target.value as TipoContenido;
                            nuevosContenidos[index].tipoContenido = nuevoTipo;
                            setContenidos(nuevosContenidos);
                            // Si cambia a PDF, IMAGEN o VIDEO, activar pestaña de archivo por defecto
                            if (nuevoTipo === "PDF" || nuevoTipo === "IMAGEN" || nuevoTipo === "VIDEO") {
                              setTabActiva({ ...tabActiva, [index]: "archivo" });
                            }
                          }}
                          className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                          <option value="VIDEO">Video</option>
                          <option value="PDF">PDF</option>
                          <option value="LINK">Link</option>
                          <option value="IMAGEN">Imagen</option>
                          <option value="TEXTO">Texto</option>
                        </select>
                      </div>

                      {/* Título */}
                      <div>
                        <label className="block text-xs font-medium text-gray-600 mb-1">
                          Título (opcional)
                        </label>
                        <input
                          type="text"
                          value={contenido.titulo || ""}
                          onChange={(e) => {
                            const nuevosContenidos = [...contenidos];
                            nuevosContenidos[index].titulo = e.target.value;
                            setContenidos(nuevosContenidos);
                          }}
                          className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                          placeholder="Ej: Video explicativo de álgebra"
                        />
                      </div>

                      {/* URL, Archivo o Texto según el tipo */}
                      {contenido.tipoContenido === "TEXTO" ? (
                        <div>
                          <label className="block text-xs font-medium text-gray-600 mb-1">
                            Contenido de Texto
                          </label>
                          <textarea
                            value={contenido.contenidoTexto || ""}
                            onChange={(e) => {
                              const nuevosContenidos = [...contenidos];
                              nuevosContenidos[index].contenidoTexto =
                                e.target.value;
                              setContenidos(nuevosContenidos);
                            }}
                            rows={3}
                            className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="Escribe el contenido de texto aquí..."
                          />
                        </div>
                      ) : contenido.tipoContenido === "PDF" || 
                            contenido.tipoContenido === "IMAGEN" || 
                            contenido.tipoContenido === "VIDEO" ? (
                        <div>
                          <label className="block text-xs font-medium text-gray-600 mb-1">
                            {contenido.tipoContenido === "PDF"
                              ? "Seleccionar Archivo PDF"
                              : contenido.tipoContenido === "IMAGEN"
                              ? "Seleccionar Imagen"
                              : "Seleccionar Video"}
                          </label>
                          <div className="space-y-2">
                            <div className="flex items-center gap-3">
                              <input
                                type="file"
                                id={`archivo-${index}`}
                                accept={
                                  contenido.tipoContenido === "PDF"
                                    ? ".pdf"
                                    : contenido.tipoContenido === "IMAGEN"
                                    ? "image/*"
                                    : "video/*"
                                }
                                onChange={async (e) => {
                                  const file = e.target.files?.[0];
                                  if (file) {
                                    // Validar tamaño antes de subir (50MB máximo)
                                    const maxSize = 50 * 1024 * 1024; // 50MB
                                    if (file.size > maxSize) {
                                      setError(`El archivo excede el tamaño máximo permitido (50MB). Tamaño actual: ${formatFileSize(file.size)}`);
                                      return;
                                    }

                                    try {
                                      setSubiendoArchivo((prev) => ({ ...prev, [index]: true }));
                                      setError(null);
                                      const url = await apiService.subirArchivo(
                                        file,
                                        contenido.tipoContenido as "PDF" | "IMAGEN" | "VIDEO"
                                      );
                                      const nuevosContenidos = [...contenidos];
                                      nuevosContenidos[index].contenidoUrl = url;
                                      setContenidos(nuevosContenidos);
                                      
                                      // Guardar información del archivo subido
                                      setArchivosSubidos((prev) => ({
                                        ...prev,
                                        [index]: {
                                          nombre: file.name,
                                          tamaño: file.size
                                        }
                                      }));
                                    } catch (error) {
                                      setError(
                                        error instanceof Error
                                          ? error.message
                                          : "Error al subir el archivo"
                                      );
                                      // Limpiar el archivo si falla
                                      const nuevosContenidos = [...contenidos];
                                      nuevosContenidos[index].contenidoUrl = "";
                                      setContenidos(nuevosContenidos);
                                      setArchivosSubidos((prev) => {
                                        const nuevos = { ...prev };
                                        delete nuevos[index];
                                        return nuevos;
                                      });
                                    } finally {
                                      setSubiendoArchivo((prev) => ({ ...prev, [index]: false }));
                                    }
                                  }
                                }}
                                className="hidden"
                              />
                              <label
                                htmlFor={`archivo-${index}`}
                                className={`flex items-center gap-2 px-4 py-2 rounded-lg cursor-pointer transition-colors text-sm font-medium ${
                                  subiendoArchivo[index] || contenido.contenidoUrl
                                    ? "bg-gray-400 text-white cursor-not-allowed"
                                    : "bg-blue-600 text-white hover:bg-blue-700"
                                }`}
                              >
                                <Upload className="w-4 h-4" />
                                {contenido.tipoContenido === "PDF"
                                  ? "Seleccionar PDF"
                                  : contenido.tipoContenido === "IMAGEN"
                                  ? "Seleccionar Imagen"
                                  : "Seleccionar Video"}
                              </label>
                              {subiendoArchivo[index] && (
                                <div className="flex items-center gap-2 text-xs text-blue-600">
                                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
                                  <span>Subiendo archivo...</span>
                                </div>
                              )}
                            </div>
                            {contenido.contenidoUrl && archivosSubidos[index] && !subiendoArchivo[index] && (
                              <div className="flex items-start gap-2 px-3 py-2 bg-green-50 border border-green-200 rounded-lg w-full">
                                <CheckCircle2 className="w-5 h-5 text-green-600 flex-shrink-0 mt-0.5" />
                                <div className="flex-1 min-w-0">
                                  <div className="flex items-center gap-2 mb-1">
                                    {contenido.tipoContenido === "PDF" && <FileText className="w-4 h-4 text-green-600 flex-shrink-0" />}
                                    {contenido.tipoContenido === "IMAGEN" && <Image className="w-4 h-4 text-green-600 flex-shrink-0" />}
                                    {contenido.tipoContenido === "VIDEO" && <Video className="w-4 h-4 text-green-600 flex-shrink-0" />}
                                    <span className="text-sm font-medium text-green-800 truncate">
                                      {archivosSubidos[index].nombre}
                                    </span>
                                  </div>
                                  <span className="text-xs text-green-600">
                                    {formatFileSize(archivosSubidos[index].tamaño)} • Subido correctamente
                                  </span>
                                </div>
                              </div>
                            )}
                          </div>
                          <p className="text-xs text-gray-500 mt-1">
                            Tamaño máximo: 50MB. Formatos permitidos: {
                              contenido.tipoContenido === "PDF"
                                ? "PDF"
                                : contenido.tipoContenido === "IMAGEN"
                                ? "JPG, PNG, GIF, WEBP"
                                : "MP4, AVI, MOV, WMV, FLV, WEBM"
                            }
                          </p>
                        </div>
                      ) : (
                        <div>
                          <label className="block text-xs font-medium text-gray-600 mb-1">
                            URL del Contenido
                          </label>
                          <input
                            type="url"
                            value={contenido.contenidoUrl || ""}
                            onChange={(e) => {
                              const nuevosContenidos = [...contenidos];
                              nuevosContenidos[index].contenidoUrl =
                                e.target.value;
                              setContenidos(nuevosContenidos);
                            }}
                            className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="https://ejemplo.com"
                          />
                        </div>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
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
              {loading ? "Creando..." : "Crear Misión"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CrearMisionModal;
