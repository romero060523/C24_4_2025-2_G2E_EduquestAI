import API from "./api";
import type {
  InscripcionPayload,
  InscripcionMasivaPayload,
  EstadoInscripcion,
} from "../types";

// ============= INSCRIPCIONES =============

export const getInscripciones = async (params?: {
  curso_id?: string;
  estudiante_id?: string;
  estado?: EstadoInscripcion;
}) => {
  const response = await API.get("/inscripciones/", { params });
  return response.data;
};

export const getInscripcionById = async (id: string) => {
  const response = await API.get(`/inscripciones/${id}/`);
  return response.data;
};

export const createInscripcion = async (data: InscripcionPayload) => {
  const response = await API.post("/inscripciones/", data);
  return response.data;
};

export const updateInscripcion = async (
  id: string,
  data: InscripcionPayload
) => {
  const response = await API.put(`/inscripciones/${id}/`, data);
  return response.data;
};

export const deleteInscripcion = async (id: string) => {
  const response = await API.delete(`/inscripciones/${id}/`);
  return response.data;
};

export const cambiarEstadoInscripcion = async (
  id: string,
  estado: EstadoInscripcion
) => {
  const response = await API.patch(`/inscripciones/${id}/cambiar_estado/`, {
    estado,
  });
  return response.data;
};

export const inscripcionMasiva = async (data: InscripcionMasivaPayload) => {
  const response = await API.post("/inscripciones/inscripcion_masiva/", data);
  return response.data;
};
