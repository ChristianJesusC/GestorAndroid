package com.chiu.renovadoproyecto1.features.login.presentation.ViewModel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiu.renovadoproyecto1.features.login.domain.usecase.LoginUseCase
import com.chiu.renovadoproyecto1.core.hardware.domain.Biometric.AuthenticateBiometricUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val biometricUseCase: AuthenticateBiometricUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        checkBiometricRequirement()
    }

    private fun checkBiometricRequirement() {
        Log.d("LoginViewModel", "Verificando requisitos biométricos")

        val isBiometricAvailable = biometricUseCase.isAvailable()
        Log.d("LoginViewModel", "Biometría disponible: $isBiometricAvailable")

        if (!isBiometricAvailable) {
            _uiState.value = _uiState.value.copy(
                shouldCloseApp = true,
                error = "Este dispositivo no tiene autenticación biométrica configurada. La app se cerrará por seguridad."
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            requiresMandatoryBiometric = true,
            message = "Autenticación biométrica requerida para continuar"
        )
    }

    fun authenticateWithMandatoryBiometric() {
        Log.d("LoginViewModel", "Iniciando autenticación biométrica obligatoria")
        _uiState.value = _uiState.value.copy(
            isLoadingBiometric = true,
            error = null
        )

        biometricUseCase.execute(
            onSuccess = {
                Log.d("LoginViewModel", "Autenticación biométrica obligatoria exitosa")
                viewModelScope.launch {
                    try {
                        val isLoggedIn = loginUseCase.isLoggedIn()
                        if (isLoggedIn) {
                            _uiState.value = _uiState.value.copy(
                                isLoadingBiometric = false,
                                requiresMandatoryBiometric = false,
                                biometricAuthCompleted = true,
                                isLoginSuccessful = true,
                                shouldNavigateToJuegos = true,
                                message = "Autenticación exitosa. Sesión activa encontrada."
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoadingBiometric = false,
                                requiresMandatoryBiometric = false,
                                biometricAuthCompleted = true,
                                message = "Autenticación biométrica exitosa. Ingresa tus credenciales."
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("LoginViewModel", "Error verificando sesión: ${e.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoadingBiometric = false,
                            requiresMandatoryBiometric = false,
                            biometricAuthCompleted = true,
                            message = "Autenticación biométrica exitosa. Ingresa tus credenciales."
                        )
                    }
                }
            },
            onError = { error ->
                Log.e("LoginViewModel", "Error en autenticación biométrica obligatoria: $error")
                _uiState.value = _uiState.value.copy(
                    isLoadingBiometric = false,
                    shouldCloseApp = true,
                    error = "Autenticación biométrica fallida. La app se cerrará por seguridad."
                )
            }
        )
    }

    fun login(username: String, password: String) {
        if (!_uiState.value.biometricAuthCompleted) {
            _uiState.value = _uiState.value.copy(
                error = "Debe completar la autenticación biométrica primero"
            )
            return
        }

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

    fun closeApp(context: Context) {
        if (context is Activity) {
            context.finishAffinity()
        }
    }

    fun retryBiometricAuth() {
        authenticateWithMandatoryBiometric()
    }

    fun clearNavigationFlag() {
        _uiState.value = _uiState.value.copy(shouldNavigateToJuegos = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun logout() {
        viewModelScope.launch {
            try {
                loginUseCase.logout()
                Log.d("LoginViewModel", "Logout exitoso")
                // Después del logout, requerir biometría nuevamente
                _uiState.value = LoginUiState()
                checkBiometricRequirement()
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error en logout: ${e.message}")
                _uiState.value = LoginUiState()
                checkBiometricRequirement()
            }
        }
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoadingBiometric: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val isCheckingAuth: Boolean = false,
    val shouldNavigateToJuegos: Boolean = false,
    val requiresMandatoryBiometric: Boolean = false,
    val biometricAuthCompleted: Boolean = false,
    val shouldCloseApp: Boolean = false,
    val message: String? = null,
    val error: String? = null
)