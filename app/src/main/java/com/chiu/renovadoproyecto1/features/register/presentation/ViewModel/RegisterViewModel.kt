package com.chiu.renovadoproyecto1.features.register.presentation.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiu.renovadoproyecto1.features.register.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(username: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            Log.d("RegisterViewModel", "Iniciando registro para usuario: $username")
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            registerUseCase(username, password, confirmPassword).fold(
                onSuccess = { mensaje ->
                    Log.d("RegisterViewModel", "Registro exitoso: $mensaje")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegisterSuccessful = true,
                        message = mensaje,
                        shouldNavigateToLogin = true
                    )
                },
                onFailure = { exception ->
                    Log.e("RegisterViewModel", "Error en registro: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error desconocido"
                    )
                }
            )
        }
    }

    fun clearNavigationFlag() {
        _uiState.value = _uiState.value.copy(shouldNavigateToLogin = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

}

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isRegisterSuccessful: Boolean = false,
    val shouldNavigateToLogin: Boolean = false,
    val message: String? = null,
    val error: String? = null
)