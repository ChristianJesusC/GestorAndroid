package com.chiu.renovadoproyecto1.features.login.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiu.renovadoproyecto1.features.login.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel (
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        checkAuthenticationOnStart()
    }

    private fun checkAuthenticationOnStart() {
        viewModelScope.launch {
            Log.d("LoginViewModel", "Verificando autenticación al inicio")
            _uiState.value = _uiState.value.copy(isCheckingAuth = true)

            try {
                val isLoggedIn = loginUseCase.isLoggedIn()
                Log.d("LoginViewModel", "Resultado verificación: $isLoggedIn")

                if (isLoggedIn) {
                    _uiState.value = _uiState.value.copy(
                        isCheckingAuth = false,
                        isLoginSuccessful = true,
                        shouldNavigateToJuegos = true,
                        message = "Sesión activa"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isCheckingAuth = false,
                        isLoginSuccessful = false
                    )
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error verificando autenticación: ${e.message}")
                loginUseCase.logout()
                _uiState.value = _uiState.value.copy(
                    isCheckingAuth = false,
                    isLoginSuccessful = false
                )
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            Log.d("LoginViewModel", "Iniciando login para usuario: $username")
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            loginUseCase(username, password).fold(
                onSuccess = { loginResponse ->
                    Log.d("LoginViewModel", "Login exitoso: ${loginResponse.mensaje}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
                        message = loginResponse.mensaje,
                        shouldNavigateToJuegos = true
                    )
                },
                onFailure = { exception ->
                    Log.e("LoginViewModel", "Error en login: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error desconocido"
                    )
                }
            )
        }
    }

    fun clearNavigationFlag() {
        _uiState.value = _uiState.value.copy(shouldNavigateToJuegos = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val isCheckingAuth: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val shouldNavigateToJuegos: Boolean = false,
    val message: String? = null,
    val error: String? = null
)