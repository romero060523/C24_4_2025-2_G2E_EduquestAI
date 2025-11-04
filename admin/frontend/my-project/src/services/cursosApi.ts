import API from "./api";
import type { CursoPayload } from "../types";

// ============= CURSOS =============

export const getCursos = async (activo?: boolean) => {
  const params = activo !== undefined ? { activo } : {};
  const response = await API.get("/cursos/", { params });
  return response.data;
};

export const getCursoById = async (id: string) => {
  const response = await API.get(`/cursos/${id}/`);
  return response.data;
};

export const createCurso = async (data: CursoPayload) => {
  const response = await API.post("/cursos/", data);
  return response.data;
};

export const updateCurso = async (id: string, data: CursoPayload) => {
  const response = await API.put(`/cursos/${id}/`, data);
  return response.data;
};

export const deleteCurso = async (id: string) => {
  const response = await API.delete(`/cursos/${id}/`);
  return response.data;
};

export const getEstudiantesByCurso = async (cursoId: string) => {
  const response = await API.get(`/cursos/${cursoId}/estudiantes/`);
  return response.data;
};

export const inscribirEstudiantes = async (
  cursoId: string,
  estudiantesIds: string[]
) => {
  const response = await API.post(`/cursos/${cursoId}/inscribir_estudiantes/`, {
    estudiantes_ids: estudiantesIds,
  });
  return response.data;
};
