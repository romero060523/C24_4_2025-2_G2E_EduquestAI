// Tipos y enums para entidades y DTOs

export type TipoMision = 'INDIVIDUAL' | 'GRUPAL';
export type CategoriaMision = 'LECTURA' | 'EJERCICIO' | 'PROYECTO' | 'QUIZ' | 'DESAFIO';
export type DificultadMision = 'FACIL' | 'MEDIO' | 'DIFICIL' | 'EXPERTO';
export type TemaVisual = 'MEDIEVAL' | 'ANIME' | 'ESPACIAL' | 'FANTASIA' | 'CIENCIA' | 'NATURALEZA' | 'URBANO' | 'OCEANO' | 'DEFAULT';

export interface Usuario {
  id: string;
  username: string;
  email: string;
  nombreCompleto: string;
  rol: 'ADMINISTRADOR' | 'PROFESOR' | 'ESTUDIANTE';
  avatarUrl?: string;
  activo: boolean;
  fechaCreacion: string;
  fechaActualizacion: string;
  ultimoAcceso?: string;
}

export interface Curso {
  id: string;
  codigoCurso: string;
  nombre: string;
  descripcion?: string;
  imagenPortada?: string;
  activo: boolean;
  fechaCreacion: string;
  fechaActualizacion: string;
}

export interface Mision {
  id: string;
  titulo: string;
  descripcion: string;
  tipoMision: TipoMision;
  categoria: CategoriaMision;
  dificultad: DificultadMision;
  puntosRecompensa: number;
  experienciaRecompensa: number;
  monedasRecompensa?: number;
  semanaClase?: number;
  fechaInicio: string;
  fechaLimite: string;
  activo: boolean;
  fechaCreacion: string;
  fechaActualizacion: string;
  requisitosPrevios?: string;
  profesor?: Usuario;
  curso?: Curso;
}

export interface MisionListResponse {
  id: string;
  titulo: string;
  descripcionResumida: string;
  categoria: CategoriaMision;
  dificultad: DificultadMision;
  temaVisual?: TemaVisual;
  puntosRecompensa: number;
  fechaLimite: string;
  activo: boolean;
  cursoId: string; // ID del curso al que pertenece
  cursoNombre: string;
  estudiantesCompletados: number;
  totalEstudiantes: number;
}

// DTOs con enums en mayúscula y nombres/campos correctos
export interface CrearMisionDTO {
  titulo: string;
  descripcion: string;
  tipoMision: TipoMision;
  categoria: CategoriaMision;
  dificultad: DificultadMision;
  puntosRecompensa: number;
  experienciaRecompensa: number;
  monedasRecompensa: number;
  semanaClase?: number;
  temaVisual?: TemaVisual;
  fechaInicio: string;
  fechaLimite: string;
  cursoId: string;
  requisitosPrevios?: string;
}

export interface ActualizarMisionDTO {
  titulo?: string;
  descripcion?: string;
  tipoMision?: TipoMision;
  categoria?: CategoriaMision;
  dificultad?: DificultadMision;
  puntosRecompensa?: number;
  experienciaRecompensa?: number;
  fechaInicio?: string;
  fechaLimite?: string;
  requisitosPrevios?: string;
  activo?: boolean;
}

export interface ApiResponse<T> {
  data: T;
  message?: string;
  status: number;
}

export interface ApiError {
  message: string;
  status: number;
  errors?: Record<string, string[]>;
}

// ==================== AUTH ====================

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  id: string;
  username: string;
  email: string;
  nombreCompleto: string;
  rol: string;
  avatarUrl?: string;
  token: string;
  message?: string;
}

// ==================== MISIONES ESTUDIANTE ====================

export interface MisionEstudianteResponse {
  id: string;
  titulo: string;
  descripcion: string;
  categoria: CategoriaMision;
  dificultad: DificultadMision;
  puntosRecompensa: number;
  experienciaRecompensa: number;
  monedasRecompensa?: number;
  semanaClase?: number;
  temaVisual?: TemaVisual;
  fechaInicio: string;
  fechaLimite: string;
  activo: boolean;
  cursoNombre: string;
  porcentajeCompletado: number;
  completada: boolean;
  fechaCompletado?: string;
  estadoEntrega: 'PENDIENTE' | 'ENVIADA' | 'REVISANDO' | 'CALIFICADA' | 'RECHAZADA';
  puntosObtenidos: number;
  ultimaActividad: string;
}

export interface CompletarMisionRequest {
  contenidoEntrega: string;
  archivoUrl?: string;
  comentariosEstudiante?: string;
}

// ==================== GAMIFICACIÓN ====================

export interface LogroResponse {
  id: string;
  nombre: string;
  descripcion: string;
  icono?: string;
  puntosRequeridos: number;
  fechaObtenido?: string;
  obtenido: boolean;
}

export interface PerfilGamificadoResponse {
  puntosTotales: number;
  nivel: number;
  nombreNivel: string;
  puntosParaSiguienteNivel: number;
  misionesCompletadas: number;
  logrosObtenidos: number;
  logros: LogroResponse[];
  posicionRanking?: number;
}

export interface RankingEstudianteResponse {
  estudianteId: string;
  nombreEstudiante: string;
  puntosTotales: number;
  nivel: number;
  nombreNivel: string;
  misionesCompletadas: number;
  posicion: number;
}

export interface RankingResponse {
  cursoId?: string;
  cursoNombre: string;
  estudiantes: RankingEstudianteResponse[];
  totalEstudiantes: number;
}

export interface EstudianteSimple {
  id: string;
  nombreCompleto: string;
  email: string;
  username: string;
  avatarUrl?: string;
  fechaInscripcion?: string;
}

// ==================== PROGRESO DE ESTUDIANTES ====================

export interface EstudianteProgresoResponse {
  estudianteId: string;
  nombreCompleto: string;
  avatarUrl?: string;
  porcentajeCompletado: number;
  estado: string; // 'completada' | 'en_progreso' | 'no_iniciada'
  ultimaActividad: string;
}

export interface MisionProgresoResponse {
  misionId: string;
  titulo: string;
  totalEstudiantes: number;
  completados: number;
  enProgreso: number;
  noIniciados: number;
  estudiantes: EstudianteProgresoResponse[];
}


// Evaluaciones Gamificadas
export type TipoPregunta = 
  | 'OPCION_MULTIPLE' 
  | 'VERDADERO_FALSO' 
  | 'ARRASTRAR_SOLTAR' 
  | 'COMPLETAR_ESPACIOS' 
  | 'ORDENAR' 
  | 'EMPAREJAR' 
  | 'SELECCION_MULTIPLE';

export interface OpcionRespuestaResponse {
  id: string;
  texto: string;
  esCorrecta: boolean;
  orden: number;
  imagenUrl?: string;
  feedback?: string;
}

export interface PreguntaResponse {
  id: string;
  enunciado: string;
  tipoPregunta: TipoPregunta;
  puntos: number;
  orden: number;
  imagenUrl?: string;
  explicacion?: string;
  opciones: OpcionRespuestaResponse[];
}

export interface EvaluacionGamificadaResponse {
  id: string;
  misionId: string;
  misionTitulo: string;
  cursoId: string;
  cursoNombre: string;
  titulo: string;
  descripcion?: string;
  tiempoLimiteMinutos?: number;
  intentosPermitidos: number;
  mostrarResultadosInmediato: boolean;
  puntosPorPregunta: number;
  puntosBonusTiempo: number;
  activo: boolean;
  preguntas: PreguntaResponse[];
  fechaCreacion: string;
  fechaActualizacion?: string;
  // Campos específicos del estudiante (solo cuando se consulta para un estudiante)
  completada?: boolean; // Si el estudiante ya completó la evaluación
  intentosUsados?: number; // Cuántos intentos ha usado el estudiante
  mejorPuntuacion?: number; // Mejor puntuación obtenida
  mejorPorcentaje?: number; // Mejor porcentaje obtenido
  fechaCompletado?: string; // Fecha en que completó (si ya la completó)
}

export interface ResultadoEvaluacionResponse {
  id: string;
  evaluacionId: string;
  estudianteId: string;
  estudianteNombre: string;
  puntosTotales: number;
  puntosMaximos: number;
  puntosBonus: number;
  porcentaje: number;
  preguntasCorrectas: number;
  preguntasTotales: number;
  tiempoTotalSegundos?: number;
  intentoNumero: number;
  completada: boolean;
  fechaCompletado: string;
}

export interface CrearOpcionRequest {
  texto: string;
  esCorrecta?: boolean;
  orden?: number;
  imagenUrl?: string;
  feedback?: string;
}

export interface CrearPreguntaRequest {
  enunciado: string;
  tipoPregunta: TipoPregunta;
  puntos?: number;
  orden?: number;
  imagenUrl?: string;
  explicacion?: string;
  opciones: CrearOpcionRequest[];
}

export interface CrearEvaluacionRequest {
  misionId?: string; // Ahora OPCIONAL: solo si quieres asociar a una misión específica
  cursoId: string; // Ahora REQUERIDO: toda evaluación pertenece a un curso
  titulo: string;
  descripcion?: string;
  tiempoLimiteMinutos?: number;
  intentosPermitidos?: number;
  mostrarResultadosInmediato?: boolean;
  puntosPorPregunta?: number;
  puntosBonusTiempo?: number;
  preguntas: CrearPreguntaRequest[];
}

export interface RespuestaRequest {
  preguntaId: string;
  opcionId?: string;
  respuestaTexto?: string;
  tiempoRespuestaSegundos?: number;
}

export interface ResponderEvaluacionRequest {
  evaluacionId: string;
  respuestas: RespuestaRequest[];
  tiempoTotalSegundos?: number;
}

