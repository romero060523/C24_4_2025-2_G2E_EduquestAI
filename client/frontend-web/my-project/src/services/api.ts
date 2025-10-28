import axios from 'axios';
import type { AxiosInstance, AxiosError } from 'axios';
import type { 
  Mision, 
  MisionListResponse, 
  CrearMisionDTO, 
  ActualizarMisionDTO, 
  ApiError,
  LoginResponse,
  Curso
} from '../types';

// Configuración base de axios
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
      timeout: 10000,
    });

    this.api.interceptors.request.use(
      (config) => {
        // Header temporal de profesor
        const profesorId = localStorage.getItem('profesorId');
        if (profesorId) {
          config.headers['X-Profesor-Id'] = profesorId;
        }
        // JWT auth si la tienes
        const token = localStorage.getItem('access_token');
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
            message: error.response.data?.message || 'Error en la solicitud',
            status: error.response.status,
            errors: error.response.data?.errors,
          };
          return Promise.reject(apiError);
        }
        return Promise.reject({
          message: 'Error de conexión con el servidor',
          status: 0,
        } as ApiError);
      }
    );
  }

  // ==================== AUTENTICACIÓN ====================

  async loginProfesor(email: string, password: string): Promise<LoginResponse> {
    const response = await this.api.post<{ success: boolean; data: LoginResponse; message: string }>(
      '/auth/login',
      { email, password }
    );
    return response.data.data;
  }

  // ==================== MISIONES ====================

  async crearMision(mision: CrearMisionDTO): Promise<Mision> {
    // El controlador espera /misiones
    const response = await this.api.post('/misiones', mision);
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

  async actualizarMision(id: string, mision: ActualizarMisionDTO): Promise<Mision> {
    const response = await this.api.put(`/misiones/${id}`, mision);
    return response.data?.data || response.data;
  }

  async eliminarMision(id: string): Promise<void> {
    await this.api.delete(`/misiones/${id}`);
  }

  // ==================== CURSOS ====================

  async listarCursosPorProfesor(profesorId: string): Promise<Curso[]> {
    const response = await this.api.get(`/cursos/profesor/${profesorId}`);
    const data = response.data?.data || response.data;
    return Array.isArray(data) ? data : [];
  }
}

export const apiService = new ApiService();
