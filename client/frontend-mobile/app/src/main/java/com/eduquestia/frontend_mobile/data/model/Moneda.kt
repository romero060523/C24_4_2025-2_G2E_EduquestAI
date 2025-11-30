package com.eduquestia.frontend_mobile.data.model

import kotlinx.serialization.Serializable

/**
 * Modelo para el sistema de monedas (EduCoins) Las monedas se obtienen al completar misiones y
 * evaluaciones
 */
@Serializable
data class BalanceMonedas(
        val eduCoins: Int = 0,
        val totalGanado: Int = 0,
        val totalGastado: Int = 0
)

/** Transacción de monedas (historial) */
@Serializable
data class TransaccionMoneda(
        val id: String,
        val tipo: TipoTransaccion,
        val cantidad: Int,
        val descripcion: String,
        val fecha: String,
        val origenId: String? = null // ID de misión o evaluación
)

enum class TipoTransaccion {
    GANADO_MISION,
    GANADO_EVALUACION,
    GANADO_LOGRO,
    GASTADO_TIENDA,
    BONUS
}

/** Recompensa al completar una actividad */
@Serializable data class Recompensa(val xp: Int, val eduCoins: Int, val mensaje: String? = null)
