package com.chiu.renovadoproyecto1.features.usuarios.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiu.renovadoproyecto1.features.login.domain.repository.TokenRepository
import com.chiu.renovadoproyecto1.features.usuarios.domain.model.Usuario
import com.chiu.renovadoproyecto1.features.usuarios.domain.usecase.GetUsuariosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsuariosViewModel(
    private val getUsuariosUseCase: GetUsuariosUseCase,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UsuariosState>(UsuariosState.Loading)
    val state: StateFlow<UsuariosState> = _state.asStateFlow()

    private val _authState = MutableStateFlow(true)
    val authState: StateFlow<Boolean> = _authState.asStateFlow()

    init {
        loadUsuarios()
    }

    fun loadUsuarios() {
        viewModelScope.launch {
            _state.value = UsuariosState.Loading

            getUsuariosUseCase().fold(
                onSuccess = { usuarios ->
                    Log.d("UsuariosViewModel", "✅ ${usuarios.size} usuarios cargados")
                    _state.value = UsuariosState.Success(usuarios)
                },
                onFailure = { exception ->
                    Log.e("UsuariosViewModel", "❌ Error: ${exception.message}")
                    if (isAuthError(exception)) {
                        logout()
                    } else {
                        _state.value = UsuariosState.Error(exception.message ?: "Error desconocido")
                    }
                }
            )
        }
    }

    private fun isAuthError(exception: Throwable): Boolean {
        val message = exception.message?.lowercase() ?: ""
        return message.contains("401") || message.contains("403") ||
                message.contains("token") || message.contains("sesión")
    }

    private fun logout() {
        viewModelScope.launch {
            tokenRepository.clearToken()
            _authState.value = false
        }
    }
}

sealed class UsuariosState {
    object Loading : UsuariosState()
    data class Success(val usuarios: List<Usuario>) : UsuariosState()
    data class Error(val error: String) : UsuariosState()
}