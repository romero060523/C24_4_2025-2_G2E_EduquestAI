package com.eduquestia.frontend_mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduquestia.frontend_mobile.data.local.TokenManager
import com.eduquestia.frontend_mobile.data.model.PerfilGamificado
import com.eduquestia.frontend_mobile.data.model.Ranking
import com.eduquestia.frontend_mobile.data.repository.GamificacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val nombreUsuario: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val perfilGamificado: PerfilGamificado? = null,
    val rankingGlobal: Ranking? = null,
    val posicionRanking: Int? = null,
    val selectedTab: ProfileTab = ProfileTab.INFORMACION,
    val error: String? = null
)

enum class ProfileTab {
    INFORMACION,
    LOGROS,
    ACTIVIDAD,
    CONFIGURACION
}

class ProfileViewModel(
    private val gamificacionRepository: GamificacionRepository = GamificacionRepository(),
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val userId = tokenManager.getUserId()
                val nombreUsuario = tokenManager.getUserNombre() ?: "Usuario"
                val email = tokenManager.getUserEmail() ?: ""

                if (userId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No se pudo obtener el ID del usuario"
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    nombreUsuario = nombreUsuario,
                    email = email
                )

                // Cargar perfil gamificado
                gamificacionRepository.obtenerPerfilGamificado(userId)
                    .onSuccess { perfil ->
                        _uiState.value = _uiState.value.copy(perfilGamificado = perfil)
                    }
                    .onFailure { exception ->
                        // No es crítico, puede que no haya datos aún
                    }

                // Cargar ranking global
                gamificacionRepository.obtenerRankingGlobal()
                    .onSuccess { ranking ->
                        val posicion = ranking.estudiantes.indexOfFirst { it.estudianteId == userId }
                        _uiState.value = _uiState.value.copy(
                            rankingGlobal = ranking,
                            posicionRanking = if (posicion >= 0) posicion + 1 else null
                        )
                    }
                    .onFailure {
                        // No es crítico
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

    fun selectTab(tab: ProfileTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun refresh() {
        loadProfileData()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

