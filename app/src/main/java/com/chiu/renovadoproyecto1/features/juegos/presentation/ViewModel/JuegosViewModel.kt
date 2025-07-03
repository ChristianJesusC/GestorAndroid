package com.chiu.renovadoproyecto1.features.juegos.presentation.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiu.renovadoproyecto1.features.juegos.domain.model.Juego
import com.chiu.renovadoproyecto1.features.juegos.domain.usecase.CreateJuegoUseCase
import com.chiu.renovadoproyecto1.features.juegos.domain.usecase.DeleteJuegoUseCase
import com.chiu.renovadoproyecto1.features.juegos.domain.usecase.GetJuegosUseCase
import com.chiu.renovadoproyecto1.features.juegos.domain.usecase.UpdateJuegoUseCase
import com.chiu.renovadoproyecto1.features.login.domain.repository.TokenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JuegosViewModel(
    private val getJuegosUseCase: GetJuegosUseCase,
    private val createJuegoUseCase: CreateJuegoUseCase,
    private val updateJuegoUseCase: UpdateJuegoUseCase,
    private val deleteJuegoUseCase: DeleteJuegoUseCase,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private val _state = MutableStateFlow<JuegosState>(JuegosState.Loading)
    val state: StateFlow<JuegosState> = _state.asStateFlow()

    private val _authState = MutableStateFlow(true)
    val authState: StateFlow<Boolean> = _authState.asStateFlow()

    init {
        checkAuthAndLoadJuegos()
    }

    private fun checkAuthAndLoadJuegos() {
        viewModelScope.launch {
            val isValid = checkTokenValid()
            if (isValid) {
                loadJuegos()
            }
        }
    }

    private suspend fun checkTokenValid(): Boolean {
        return try {
            val isValid = tokenRepository.isTokenValid()
            if (!isValid) {
                Log.d("JuegosViewModel", "Token inválido o expirado")
                _authState.value = false
                _state.value = JuegosState.Error("Sesión expirada")
            }
            isValid
        } catch (e: Exception) {
            Log.e("JuegosViewModel", "Error verificando token: ${e.message}")
            _authState.value = false
            _state.value = JuegosState.Error("Error de autenticación")
            false
        }
    }

    fun loadJuegos() {
        viewModelScope.launch {
            val isValid = checkTokenValid()
            if (!isValid) return@launch

            _state.value = JuegosState.Loading

            getJuegosUseCase().fold(
                onSuccess = { juegos ->
                    _state.value = JuegosState.Success(juegos)
                    Log.d("JuegosViewModel", "Juegos cargados: ${juegos.size}")
                },
                onFailure = { exception ->
                    Log.e("JuegosViewModel", "Error cargando juegos: ${exception.message}")
                    if (isAuthError(exception)) {
                        logout()
                    } else {
                        _state.value = JuegosState.Error(exception.message ?: "Error desconocido")
                    }
                }
            )
        }
    }

    fun createJuego(juego: Juego) {
        viewModelScope.launch {
            try {
                _state.value = JuegosState.Loading
                Log.d("JuegosViewModel", "Creando juego: ${juego.nombre}")

                createJuegoUseCase(juego).fold(
                    onSuccess = {
                        Log.d("JuegosViewModel", "✅ Juego creado exitosamente")
                        loadJuegos() // Recargar lista
                    },
                    onFailure = { error ->
                        Log.e("JuegosViewModel", "❌ Error: ${error.message}")
                        _state.value = JuegosState.Error(error.message ?: "Error desconocido")
                    }
                )
            } catch (e: Exception) {
                Log.e("JuegosViewModel", "❌ Excepción: ${e.message}", e)
                _state.value = JuegosState.Error("Error inesperado: ${e.message}")
            }
        }
    }
    fun updateJuego(juego: Juego) {
        viewModelScope.launch {
            val isValid = checkTokenValid()
            if (!isValid) return@launch

            _state.value = JuegosState.Loading

            updateJuegoUseCase(juego).fold(
                onSuccess = { juegoActualizado ->
                    _state.value = JuegosState.ActionSuccess("Juego actualizado exitosamente")
                    Log.d("JuegosViewModel", "Juego actualizado: ${juegoActualizado.nombre}")
                    loadJuegos()
                },
                onFailure = { exception ->
                    Log.e("JuegosViewModel", "Error actualizando juego: ${exception.message}")
                    if (isAuthError(exception)) {
                        logout()
                    } else {
                        _state.value = JuegosState.Error(exception.message ?: "Error desconocido")
                    }
                }
            )
        }
    }

    fun deleteJuego(id: Int) {
        viewModelScope.launch {
            val isValid = checkTokenValid()
            if (!isValid) return@launch

            _state.value = JuegosState.Loading

            deleteJuegoUseCase(id).fold(
                onSuccess = {
                    _state.value = JuegosState.ActionSuccess("Juego eliminado exitosamente")
                    Log.d("JuegosViewModel", "Juego eliminado con ID: $id")
                    loadJuegos()
                },
                onFailure = { exception ->
                    Log.e("JuegosViewModel", "Error eliminando juego: ${exception.message}")
                    if (isAuthError(exception)) {
                        logout()
                    } else {
                        _state.value = JuegosState.Error(exception.message ?: "Error desconocido")
                    }
                }
            )
        }
    }

    private fun isAuthError(exception: Throwable): Boolean {
        val message = exception.message?.lowercase() ?: ""
        return message.contains("401") ||
                message.contains("403") ||
                message.contains("unauthorized") ||
                message.contains("token") ||
                message.contains("sesión")
    }

    fun logout() {
        viewModelScope.launch {
            Log.d("JuegosViewModel", "Cerrando sesión por token expirado")
            tokenRepository.clearToken()
            _authState.value = false
        }
    }
}

sealed class JuegosState {
    object Loading : JuegosState()
    data class Success(val juegos: List<Juego>) : JuegosState()
    data class Error(val error: String) : JuegosState()
    data class ActionSuccess(val message: String) : JuegosState()
}