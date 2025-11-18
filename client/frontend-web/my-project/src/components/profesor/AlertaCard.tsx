import React, { useState } from "react";
import type { AlertaRendimiento, DatosContextoAlerta } from "../../types";

interface Props {
  alerta: AlertaRendimiento;
  onResolver: (alertaId: string) => void;
  onIgnorar: (alertaId: string) => void;
}

const AlertaCard: React.FC<Props> = ({ alerta, onResolver, onIgnorar }) => {
  const [expanded, setExpanded] = useState(false);

  const parseDatosContexto = (): DatosContextoAlerta => {
    try {
      return JSON.parse(alerta.datosContexto);
    } catch {
      return {};
    }
  };

  const datos = parseDatosContexto();

  const getTipoInfo = () => {
    switch (alerta.tipo) {
      case "INACTIVIDAD":
        return {
          color: "red",
          icon: "😴",
          bgColor: "bg-red-50",
          borderColor: "border-red-200",
        };
      case "BAJO_RENDIMIENTO":
        return {
          color: "orange",
          icon: "📉",
          bgColor: "bg-orange-50",
          borderColor: "border-orange-200",
        };
      case "MISIONES_PENDIENTES":
        return {
          color: "yellow",
          icon: "📝",
          bgColor: "bg-yellow-50",
          borderColor: "border-yellow-200",
        };
      case "DEBAJO_PROMEDIO":
        return {
          color: "purple",
          icon: "📊",
          bgColor: "bg-purple-50",
          borderColor: "border-purple-200",
        };
      default:
        return {
          color: "gray",
          icon: "⚠️",
          bgColor: "bg-gray-50",
          borderColor: "border-gray-200",
        };
    }
  };

  const tipoInfo = getTipoInfo();

  const formatFecha = (fecha: string) => {
    const date = new Date(fecha);
    return date.toLocaleDateString("es-ES", {
      day: "2-digit",
      month: "short",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  return (
    <div
      className={`border ${tipoInfo.borderColor} ${tipoInfo.bgColor} rounded-lg p-4`}
    >
      {/* Header */}
      <div className="flex items-start justify-between">
        <div className="flex items-start gap-3 flex-1">
          <span className="text-3xl">{tipoInfo.icon}</span>
          <div className="flex-1">
            <div className="flex items-center gap-2 mb-1">
              <h3 className="font-semibold text-gray-900">
                {alerta.estudianteNombre}
              </h3>
              <span className="text-sm text-gray-500">
                {alerta.estudianteEmail}
              </span>
            </div>
            <p className="text-gray-700">{alerta.descripcion}</p>
            <p className="text-xs text-gray-500 mt-1">
              Detectado el {formatFecha(alerta.fechaCreacion)}
            </p>
          </div>
        </div>

        {/* Acciones */}
        <div className="flex gap-2 ml-4">
          <button
            onClick={() => onResolver(alerta.id)}
            className="bg-green-600 hover:bg-green-700 text-white px-3 py-1 rounded text-sm font-medium"
            title="Marcar como resuelta"
          >
            ✓ Resuelta
          </button>
          <button
            onClick={() => onIgnorar(alerta.id)}
            className="bg-gray-400 hover:bg-gray-500 text-white px-3 py-1 rounded text-sm font-medium"
            title="Ignorar alerta"
          >
            ✗ Ignorar
          </button>
          <button
            onClick={() => setExpanded(!expanded)}
            className="bg-gray-200 hover:bg-gray-300 text-gray-700 px-3 py-1 rounded text-sm font-medium"
          >
            {expanded ? "▲" : "▼"}
          </button>
        </div>
      </div>

      {/* Detalles expandidos */}
      {expanded && (
        <div className="mt-4 pt-4 border-t border-gray-300">
          <h4 className="font-medium text-gray-800 mb-2">
            Detalles del contexto:
          </h4>
          <div className="bg-white rounded p-3 text-sm">
            {alerta.tipo === "INACTIVIDAD" && (
              <>
                <p>
                  <strong>Días sin actividad:</strong> {datos.diasInactivo}
                </p>
                <p>
                  <strong>Última actividad:</strong>{" "}
                  {datos.ultimaActividad
                    ? formatFecha(datos.ultimaActividad)
                    : "N/A"}
                </p>
              </>
            )}
            {alerta.tipo === "BAJO_RENDIMIENTO" && (
              <>
                <p>
                  <strong>Completitud:</strong>{" "}
                  {datos.porcentajeCompletitud?.toFixed(1)}%
                </p>
                <p>
                  <strong>Misiones completadas:</strong>{" "}
                  {datos.misionesCompletadas} de {datos.totalMisiones}
                </p>
              </>
            )}
            {alerta.tipo === "DEBAJO_PROMEDIO" && (
              <>
                <p>
                  <strong>Puntos del estudiante:</strong>{" "}
                  {datos.puntosEstudiante}
                </p>
                <p>
                  <strong>Promedio del grupo:</strong>{" "}
                  {datos.promedioGrupo?.toFixed(1)}
                </p>
              </>
            )}
            {alerta.tipo === "MISIONES_PENDIENTES" && (
              <p>
                <strong>Misiones pendientes:</strong> {datos.misionesPendientes}
              </p>
            )}
          </div>

          <div className="mt-3 flex gap-2">
            <button
              onClick={() =>
                (window.location.href = `/profesor/estudiantes/${alerta.estudianteId}`)
              }
              className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded text-sm font-medium"
            >
              Ver perfil del estudiante
            </button>
            <button
              onClick={() => {
                /* Enviar mensaje o correo */
              }}
              className="bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded text-sm font-medium"
            >
              Contactar estudiante
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default AlertaCard;
