package com.eduquestia.frontend_mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduquestia.frontend_mobile.data.local.TokenManager
import com.eduquestia.frontend_mobile.data.model.Logro
import com.eduquestia.frontend_mobile.data.model.MisionEstudiante
import com.eduquestia.frontend_mobile.data.model.PerfilGamificado
import com.eduquestia.frontend_mobile.data.model.Ranking
import com.eduquestia.frontend_mobile.data.repository.GamificacionRepository
import com.eduquestia.frontend_mobile.data.repository.MisionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val perfilGamificado: PerfilGamificado? = null,
    val misionesRecientes: List<MisionEstudiante> = emptyList(),
    val logrosRecientes: List<Logro> = emptyList(),
    val rankingGlobal: Ranking? = null,
    val posicionRanking: Int? = null,
    val nombreUsuario: String = "",
    val error: String? = null
)

class HomeViewModel(
    private val gamificacionRepository: GamificacionRepository = GamificacionRepository(),
    private val misionRepository: MisionRepository = MisionRepository(),
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val userId = tokenManager.getUserId()
                val nombreUsuario = tokenManager.getUserNombre() ?: "Usuario"

                if (userId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No se pudo obtener el ID del usuario"
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(nombreUsuario = nombreUsuario)

                // Cargar datos en paralelo
                val perfilResult = gamificacionRepository.obtenerPerfilGamificado(userId)
                val misionesResult = misionRepository.obtenerMisionesPorEstudiante(userId)
                val rankingResult = gamificacionRepository.obtenerRankingGlobal()

                // Procesar resultados
                perfilResult.onSuccess { perfil ->
                    _uiState.value = _uiState.value.copy(perfilGamificado = perfil)

                    // Obtener logros recientes (últimos 3 obtenidos)
                    val logrosObtenidos = perfil.logros.filter { it.obtenido }
                        .sortedByDescending { it.fechaObtenido }
                        .take(3)
                    _uiState.value = _uiState.value.copy(logrosRecientes = logrosObtenidos)

                    // Buscar posición en ranking
                    rankingResult.onSuccess { ranking ->
                        val posicion = ranking.estudiantes.indexOfFirst { it.estudianteId == userId }
                        _uiState.value = _uiState.value.copy(
                            rankingGlobal = ranking,
                            posicionRanking = if (posicion >= 0) posicion + 1 else null
                        )
                    }
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Error al cargar perfil gamificado"
                    )
                }

                misionesResult.onSuccess { misiones ->
                    // Obtener misiones recientes (últimas 3, ordenadas por fecha)
                    val misionesRecientes = misiones
                        .sortedByDescending { it.ultimaActividad ?: it.fechaLimite ?: "" }
                        .take(3)
                    _uiState.value = _uiState.value.copy(misionesRecientes = misionesRecientes)
                }.onFailure { exception ->
                    // No es crítico si falla, solo no mostramos misiones
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun refresh() {
        loadHomeData()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

