import axios from "axios";
import type { AxiosInstance, AxiosError } from "axios";
import type {
  Mision,
  MisionListResponse,
  MisionEstudianteResponse,
  CompletarMisionRequest,
  CrearMisionDTO,
  ActualizarMisionDTO,
  ApiError,
  LoginResponse,
  Curso,
  MisionProgresoResponse,
  RankingResponse,
  ConfiguracionAlertaRequest,
  AlertaRendimiento,
  ConfiguracionAlertaResponse,
  EvaluacionGamificadaResponse,
  CrearEvaluacionRequest,
  ResponderEvaluacionRequest,
  ResultadoEvaluacionResponse,
  OtorgarRecompensaRequest,
  RecompensaManualResponse,
  SugerenciaIAResponse,
} from "../types";

// Configuración base de axios
const API_BASE_URL =
  import.meta.env.VITE_API_URL || "http://localhost:8081/api/v1";

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        "Content-Type": "application/json",
      },
      timeout: 10000,
    });

    this.api.interceptors.request.use(
      (config) => {
        // Header temporal de profesor
        const profesorId = localStorage.getItem("profesorId");
        if (profesorId) {
          config.headers["X-Profesor-Id"] = profesorId;
        }
        // Header temporal de estudiante
        const estudianteId =
          localStorage.getItem("estudianteId") ||
          localStorage.getItem("userId");
        if (estudianteId) {
          config.headers["X-Estudiante-Id"] = estudianteId;
        }
        // JWT auth si la tienes
        const token =
          localStorage.getItem("access_token") ||
          localStorage.getItem("accessToken");
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    this.api.interceptors.response.use(
      (response) => response,
      (error: AxiosError<ApiError>) => {
        if (error.response) {
          const apiError: ApiError = {
            message: error.response.data?.message || "Error en la solicitud",
            status: error.response.status,
            errors: error.response.data?.errors,
          };
          return Promise.reject(apiError);
        }
        return Promise.reject({
          message: "Error de conexión con el servidor",
          status: 0,
        } as ApiError);
      }
    );
  }

  // ==================== AUTENTICACIÓN ====================

  async loginProfesor(email: string, password: string): Promise<LoginResponse> {
    const response = await this.api.post<{
      success: boolean;
      data: LoginResponse;
      message: string;
    }>("/auth/login", { email, password });
    return response.data.data;
  }

  async loginEstudiante(
    email: string,
    password: string
  ): Promise<LoginResponse> {
    const response = await this.api.post<{
      success: boolean;
      data: LoginResponse;
      message: string;
    }>("/auth/login", { email, password });
    return response.data.data;
  }

  // ==================== MISIONES ====================

  async crearMision(mision: CrearMisionDTO): Promise<Mision> {
    // El controlador espera /misiones
    const response = await this.api.post("/misiones", mision);
    return response.data?.data || response.data;
  }

  async obtenerMision(id: string): Promise<Mision> {
    const response = await this.api.get(`/misiones/${id}`);
    return response.data?.data || response.data;
  }

  async listarMisionesPorProfesor(
    profesorId: string,
    categoria?: string,
    dificultad?: string
  ): Promise<MisionListResponse[]> {
    const params: Record<string, string> = {};
    if (categoria) params.categoria = categoria;
    if (dificultad) params.dificultad = dificultad;

    const response = await this.api.get(`/misiones/profesor/${profesorId}`, {
      params,
    });
    const data = response.data?.data || response.data;
    return Array.isArray(data) ? data : [];
  }

  async listarMisionesPorCurso(
    cursoId: string,
    categoria?: string,
    dificultad?: string
  ): Promise<MisionListResponse[]> {
    // Query params soportados por tu endpoint
    const params: Record<string, string> = {};
    if (categoria) params.categoria = categoria;
    if (dificultad) params.dificultad = dificultad;

    const response = await this.api.get(`/misiones/curso/${cursoId}`, {
      params,
    });
    const data = response.data?.data || response.data;
    return Array.isArray(data) ? data : [];
  }

  async actualizarMision(
    id: string,
    mision: ActualizarMisionDTO
  ): Promise<Mision> {
    const response = await this.api.put(`/misiones/${id}`, mision);
    return response.data?.data || response.data;
  }

  async eliminarMision(id: string): Promise<void> {
    await this.api.delete(`/misiones/${id}`);
  }

  // ==================== CURSOS ====================

  async listarCursosPorProfesor(profesorId: string): Promise<Curso[]> {
    try {
      console.log("🔍 API: Solicitando cursos para profesorId:", profesorId);
      const response = await this.api.get(`/cursos/profesor/${profesorId}`);
      console.log("📥 API: Respuesta completa del backend:", response.data);
      // El backend devuelve: { success: true, data: [...], message: "..." }
      const data = response.data?.data || response.data;
      console.log("📦 API: Datos extraídos:", data);
      const cursos = Array.isArray(data) ? data : [];
      console.log("✅ API: Cursos procesados:", cursos);
      return cursos;
    } catch (error) {
      console.error("❌ API: Error en listarCursosPorProfesor:", error);
      throw error;
    }
  }

  // Alias para obtener cursos del profesor actual
  async getMisCursos(): Promise<Curso[]> {
    const profesorId = localStorage.getItem("profesorId");
    if (!profesorId) {
      throw new Error("No hay sesión de profesor activa");
    }
    return this.listarCursosPorProfesor(profesorId);
  }

  async crearCurso(curso: {
    codigoCurso: string;
    nombre: string;
    descripcion?: string;
    imagenPortada?: string;
    fechaInicio?: string;
    fechaFin?: string;
  }): Promise<Curso> {
    const response = await this.api.post(`/cursos`, curso);
    return response.data?.data || response.data;
  }

  async listarCursosPorEstudiante(estudianteId: string): Promise<Curso[]> {
    try {
      console.log(
        "🔍 API: Solicitando cursos para estudianteId:",
        estudianteId
      );
      const response = await this.api.get(
        `/cursos/por-estudiante/${estudianteId}`
      );
      const data = response.data?.data || response.data;
      const cursos = Array.isArray(data) ? data : [];
      console.log("✅ API: Cursos del estudiante:", cursos);
      return cursos;
    } catch (error) {
      console.error("❌ API: Error en listarCursosPorEstudiante:", error);
      throw error;
    }
  }

  // ==================== MISIONES ESTUDIANTE ====================

  async listarMisionesPorEstudiante(
    estudianteId: string
  ): Promise<MisionEstudianteResponse[]> {
    const response = await this.api.get(`/misiones/estudiante/${estudianteId}`);
    const data = response.data?.data || response.data;
    return Array.isArray(data) ? data : [];
  }

  async completarMision(
    misionId: string,
    request: CompletarMisionRequest
  ): Promise<MisionEstudianteResponse> {
    const response = await this.api.post(
      `/misiones/${misionId}/completar`,
      request
    );
    return response.data?.data || response.data;
  }

  async obtenerPuntosTotalesEstudiante(estudianteId: string): Promise<number> {
    const response = await this.api.get(
      `/misiones/estudiante/${estudianteId}/puntos`
    );
    return response.data?.data || 0;
  }

  // ==================== GAMIFICACIÓN ====================

  async obtenerPerfilGamificado(estudianteId: string) {
    const response = await this.api.get(
      `/gamificacion/estudiante/${estudianteId}/perfil`
    );
    return response.data?.data || response.data;
  }

  async obtenerRankingPorCurso(cursoId: string) {
    const response = await this.api.get(
      `/gamificacion/ranking/curso/${cursoId}`
    );
    return response.data?.data || response.data;
  }

  async obtenerRankingGlobal() {
    const response = await this.api.get(`/gamificacion/ranking/global`);
    return response.data?.data || response.data;
  }

  // ==================== CURSOS - ASIGNAR ESTUDIANTES ====================

  async asignarEstudiantesACurso(
    cursoId: string,
    estudiantesIds: string[]
  ): Promise<{
    asignados: number;
    yaInscritos: number;
    totalProcesados: number;
  }> {
    const response = await this.api.post(
      `/cursos/${cursoId}/asignar-estudiantes`,
      {
        estudiantesIds,
      }
    );
    return response.data?.data || response.data;
  }

  async obtenerEstudiantesPorCurso(cursoId: string) {
    const response = await this.api.get(`/cursos/${cursoId}/estudiantes`);
    return response.data?.data || response.data;
  }

  async listarTodosLosEstudiantes() {
    const response = await this.api.get(`/usuarios/estudiantes`);
    return response.data?.data || response.data;
  }

  async obtenerEstadisticasProfesor(profesorId: string): Promise<{
    totalCursos: number;
    totalEstudiantes: number;
  }> {
    const response = await this.api.get(
      `/cursos/profesor/${profesorId}/estadisticas`
    );
    return response.data?.data || { totalCursos: 0, totalEstudiantes: 0 };
  }

  // ==================== PROGRESO DE ESTUDIANTES ====================

  async obtenerProgresoMision(
    misionId: string
  ): Promise<MisionProgresoResponse> {
    const profesorId =
      localStorage.getItem("profesorId") ||
      localStorage.getItem("userId") ||
      "";
    const response = await this.api.get(`/misiones/${misionId}/progreso`, {
      headers: {
        "X-Profesor-Id": profesorId,
      },
    });
    return response.data?.data || response.data;
  }

  // ==================== RANKING PARA PROFESOR ====================

  async obtenerRankingPorCursoProfesor(
    cursoId: string
  ): Promise<RankingResponse> {
    const response = await this.api.get(
      `/gamificacion/ranking/curso/${cursoId}`
    );
    return response.data?.data || response.data;
  }

  // ==================== ALERTAS ====================

  async configurarAlertas(config: ConfiguracionAlertaRequest): Promise<string> {
    const response = await this.api.post<{
      success: boolean;
      data: string;
      message: string;
    }>("/alertas/configurar", config);
    return response.data.data;
  }

  async obtenerAlertasCurso(cursoId: string): Promise<AlertaRendimiento[]> {
    const response = await this.api.get(`/alertas/curso/${cursoId}`);
    return response.data?.data || [];
  }

  async obtenerAlertasEstudiante(
    estudianteId: string
  ): Promise<AlertaRendimiento[]> {
    const response = await this.api.get(`/alertas/estudiante/${estudianteId}`);
    return response.data?.data || [];
  }

  async resolverAlerta(alertaId: string): Promise<void> {
    await this.api.patch(`/alertas/${alertaId}/resolver`);
  }

  async ignorarAlerta(alertaId: string): Promise<void> {
    await this.api.patch(`/alertas/${alertaId}/ignorar`);
  }

  async evaluarCursoManualmente(cursoId: string): Promise<void> {
    await this.api.post(`/alertas/evaluar/${cursoId}`);
  }

  async obtenerConfiguracionAlerta(
    cursoId: string
  ): Promise<ConfiguracionAlertaResponse | null> {
    const response = await this.api.get(
      `/alertas/configuracion/curso/${cursoId}`
    );
    return response.data?.data || null;
  }

  // ==================== EVALUACIONES GAMIFICADAS ====================

  async crearEvaluacionGamificada(
    request: CrearEvaluacionRequest
  ): Promise<EvaluacionGamificadaResponse> {
    const response = await this.api.post("/evaluaciones-gamificadas", request);
    return response.data?.data || response.data;
  }

  async listarEvaluacionesPorCurso(
    cursoId: string
  ): Promise<EvaluacionGamificadaResponse[]> {
    const response = await this.api.get(
      `/evaluaciones-gamificadas/curso/${cursoId}`
    );
    const data = response.data?.data || response.data;
    return Array.isArray(data) ? data : [];
  }

  async listarEvaluacionesProfesor(): Promise<EvaluacionGamificadaResponse[]> {
    const response = await this.api.get("/evaluaciones-gamificadas/profesor");
    const data = response.data?.data || response.data;
    return Array.isArray(data) ? data : [];
  }

  async listarEvaluacionesEstudiante(): Promise<
    EvaluacionGamificadaResponse[]
  > {
    const response = await this.api.get("/evaluaciones-gamificadas/estudiante");
    const data = response.data?.data || response.data;
    return Array.isArray(data) ? data : [];
  }

  async obtenerEvaluacionPorMision(
    misionId: string
  ): Promise<EvaluacionGamificadaResponse> {
    const response = await this.api.get(
      `/evaluaciones-gamificadas/mision/${misionId}`
    );
    return response.data?.data || response.data;
  }

  async obtenerEvaluacionPorId(
    evaluacionId: string
  ): Promise<EvaluacionGamificadaResponse> {
    const response = await this.api.get(
      `/evaluaciones-gamificadas/${evaluacionId}`
    );
    return response.data?.data || response.data;
  }

  async responderEvaluacion(
    request: ResponderEvaluacionRequest
  ): Promise<ResultadoEvaluacionResponse> {
    const response = await this.api.post(
      "/evaluaciones-gamificadas/responder",
      request
    );
    return response.data?.data || response.data;
  }

  async obtenerIntentosRestantes(evaluacionId: string): Promise<number> {
    const response = await this.api.get(
      `/evaluaciones-gamificadas/${evaluacionId}/intentos-restantes`
    );
    return response.data?.data || response.data;
  }

  async obtenerResultadosEstudiante(
    evaluacionId: string
  ): Promise<ResultadoEvaluacionResponse[]> {
    const response = await this.api.get(
      `/evaluaciones-gamificadas/${evaluacionId}/resultados/estudiante`
    );
    return response.data?.data || response.data;
  }

  async eliminarEvaluacion(evaluacionId: string): Promise<void> {
    await this.api.delete(`/evaluaciones-gamificadas/${evaluacionId}`);
  }

  // ==================== RECOMPENSAS MANUALES ====================
  // Historia de Usuario #12: Recompensas manuales

  async otorgarRecompensaManual(
    request: OtorgarRecompensaRequest
  ): Promise<RecompensaManualResponse> {
    const response = await this.api.post<{
      success: boolean;
      data: RecompensaManualResponse;
      message: string;
    }>("/gamificacion/recompensas/manual", request);
    return response.data.data;
  }

  async obtenerRecompensasPorEstudiante(
    estudianteId: string
  ): Promise<RecompensaManualResponse[]> {
    const response = await this.api.get<{
      success: boolean;
      data: RecompensaManualResponse[];
      message: string;
    }>(`/gamificacion/recompensas/estudiante/${estudianteId}`);
    return response.data.data;
  }

  async obtenerRecompensasPorProfesor(
    profesorId: string
  ): Promise<RecompensaManualResponse[]> {
    const response = await this.api.get<{
      success: boolean;
      data: RecompensaManualResponse[];
      message: string;
    }>(`/gamificacion/recompensas/profesor/${profesorId}`);
    return response.data.data;
  }

  /**
   * Subir un archivo (PDF, imagen o video)
   */
  async subirArchivo(
    archivo: File,
    tipo: "PDF" | "IMAGEN" | "VIDEO"
  ): Promise<string> {
    const formData = new FormData();
    formData.append("archivo", archivo);
    formData.append("tipo", tipo);

    const response = await this.api.post<{
      success: boolean;
      data: string;
      message: string;
    }>("/archivos/subir", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    if (!response.data.success) {
      throw new Error(response.data.message || "Error al subir el archivo");
    }

    return response.data.data;
  }

  // ==================== CHAT CON IA ====================

  async chatConIA(mensaje: string): Promise<string> {
    const encodedMessage = encodeURIComponent(mensaje);
    const response = await this.api.get<string>(`/chat?mensaje=${encodedMessage}`, {
      responseType: 'text' as any,
    });
    // El backend devuelve texto plano directamente
    return typeof response.data === 'string' ? response.data : String(response.data);
  }

  // ==================== TAREA 19: Retroalimentación Automática ====================
  async generarRetroalimentacion(
    request: import("../types").GenerarRetroalimentacionRequest
  ): Promise<import("../types").RetroalimentacionResponse> {
    const response = await this.api.post<{
      success: boolean;
      data: import("../types").RetroalimentacionResponse;
      message: string;
    }>("/retroalimentacion-ai/generar", request);
    return response.data.data;
  }

  // ==================== TAREA 21: Actividades Adaptadas ====================
  async generarActividadesAdaptadas(
    request: import("../types").GenerarActividadesAdaptadasRequest
  ): Promise<import("../types").ActividadesAdaptadasResponse> {
    const response = await this.api.post<{
      success: boolean;
      data: import("../types").ActividadesAdaptadasResponse;
      message: string;
    }>("/actividades-adaptadas-ai/generar", request);
  // ==================== SUGERENCIAS DE IA ====================
  // Historia de Usuario #20: IA analiza progreso y sugiere metas

  async obtenerSugerenciasIA(estudianteId: string): Promise<SugerenciaIAResponse> {
    const response = await this.api.get<{
      success: boolean;
      data: SugerenciaIAResponse;
      message: string;
    }>(`/gamificacion/estudiante/${estudianteId}/sugerencias-ia`);
    return response.data.data;
  }
}

export const apiService = new ApiService();
