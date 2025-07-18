package com.chiu.renovadoproyecto1.features.juegos.presentation.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiu.renovadoproyecto1.core.hardware.domain.Camera.CapturePhotoUseCase
import com.chiu.renovadoproyecto1.core.network.domain.usecase.CheckNetworkUseCase
import com.chiu.renovadoproyecto1.core.network.NetworkState
import com.chiu.renovadoproyecto1.core.offline.domain.usecase.SaveOfflineJuegoUseCase
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
    private val tokenRepository: TokenRepository,
    private val capturePhotoUseCase: CapturePhotoUseCase,
    private val checkNetworkUseCase: CheckNetworkUseCase,
    private val saveOfflineJuegoUseCase: SaveOfflineJuegoUseCase
) : ViewModel() {

    // Estados existentes
    private val _state = MutableStateFlow<JuegosState>(JuegosState.Loading)
    val state: StateFlow<JuegosState> = _state.asStateFlow()

    private val _authState = MutableStateFlow(true)
    val authState: StateFlow<Boolean> = _authState.asStateFlow()

    private val _cameraState = MutableStateFlow<CameraState>(CameraState.Idle)
    val cameraState: StateFlow<CameraState> = _cameraState.asStateFlow()

    private val _offlineJuegosCount = MutableStateFlow(0)
    val offlineJuegosCount: StateFlow<Int> = _offlineJuegosCount.asStateFlow()

    // Estados de red
    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Unknown)
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()

    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus.asStateFlow()

    private val _connectionType = MutableStateFlow("Verificando...")
    val connectionType: StateFlow<String> = _connectionType.asStateFlow()

    private var lastFailedOperation: (() -> Unit)? = null

    init {
        checkAuthAndLoadJuegos()
        observeNetworkStatus()
        observeOfflineJuegos()
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            checkNetworkUseCase.getNetworkState().collect { networkState ->
                _networkState.value = networkState
                Log.d("JuegosViewModel", "Network state changed: $networkState")
            }
        }

        viewModelScope.launch {
            checkNetworkUseCase.isConnected().collect { isConnected ->
                _connectionStatus.value = isConnected
                Log.d("JuegosViewModel", "Connection status: $isConnected")
            }
        }

        viewModelScope.launch {
            checkNetworkUseCase.getConnectionType().collect { connectionType ->
                _connectionType.value = connectionType
                Log.d("JuegosViewModel", "Connection type: $connectionType")
            }
        }
    }

    private fun observeOfflineJuegos() {
        viewModelScope.launch {
            // Aquí podrías observar el Flow de cantidad offline si quisieras mostrar contador
            // Por ahora solo mantenemos la funcionalidad básica
            Log.d("JuegosViewModel", "Observando juegos offline...")
        }
    }

    fun checkNetworkStatus() {
        viewModelScope.launch {
            val currentState = checkNetworkUseCase.checkNetworkStatus()
            _networkState.value = currentState
            Log.d("JuegosViewModel", "Manual network check: $currentState")
        }
    }

    fun retryLastOperation() {
        if (_connectionStatus.value) {
            lastFailedOperation?.invoke() ?: loadJuegos()
            lastFailedOperation = null
        } else {
            Log.d("JuegosViewModel", "Cannot retry - no connection available")
        }
    }

    fun isCameraAvailable(): Boolean = capturePhotoUseCase.isAvailable()

    fun hasCameraPermission(): Boolean = capturePhotoUseCase.hasPermission()

    fun requestCameraPermission() {
        _cameraState.value = CameraState.RequestingPermission
        capturePhotoUseCase.requestPermission { granted ->
            _cameraState.value = if (granted) {
                CameraState.PermissionGranted
            } else {
                CameraState.PermissionDenied("Permisos de cámara denegados")
            }
        }
    }

    fun capturePhoto() {
        Log.d("JuegosViewModel", "🔍 Iniciando captura de foto")
        Log.d("JuegosViewModel", "🔍 Cámara disponible: ${isCameraAvailable()}")
        Log.d("JuegosViewModel", "🔍 Permisos: ${hasCameraPermission()}")

        if (!hasCameraPermission()) {
            Log.e("JuegosViewModel", "❌ Sin permisos de cámara")
            _cameraState.value = CameraState.Error("Sin permisos de cámara")
            return
        }

        if (!isCameraAvailable()) {
            Log.e("JuegosViewModel", "❌ Cámara no disponible")
            _cameraState.value = CameraState.Error("Cámara no disponible")
            return
        }

        _cameraState.value = CameraState.Capturing
        Log.d("JuegosViewModel", "📸 Llamando capturePhotoUseCase...")

        capturePhotoUseCase.capturePhoto(
            onSuccess = { base64Image ->
                Log.d("JuegosViewModel", "✅ Foto capturada: ${base64Image.length} caracteres")
                val fullBase64 = "data:image/jpeg;base64,$base64Image"
                _cameraState.value = CameraState.PhotoCaptured(fullBase64)
            },
            onError = { error ->
                Log.e("JuegosViewModel", "❌ Error capturando foto: $error")
                _cameraState.value = CameraState.Error(error)
            }
        )
    }

    fun resetCameraState() {
        _cameraState.value = CameraState.Idle
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

            if (!_connectionStatus.value) {
                _state.value = JuegosState.Error("Sin conexión a internet")
                lastFailedOperation = { loadJuegos() }
                return@launch
            }

            _state.value = JuegosState.Loading

            getJuegosUseCase().fold(
                onSuccess = { juegos ->
                    _state.value = JuegosState.Success(juegos)
                    lastFailedOperation = null
                    Log.d("JuegosViewModel", "Juegos cargados: ${juegos.size}")
                },
                onFailure = { exception ->
                    Log.e("JuegosViewModel", "Error cargando juegos: ${exception.message}")
                    lastFailedOperation = { loadJuegos() }
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
                if (!_connectionStatus.value) {
                    Log.d("JuegosViewModel", "Sin conexión - guardando juego offline: ${juego.nombre}")

                    saveOfflineJuegoUseCase(juego).fold(
                        onSuccess = {
                            _state.value = JuegosState.OfflineSaved("Juego guardado en almacenamiento local")
                            Log.d("JuegosViewModel", "✅ Juego guardado offline exitosamente")
                        },
                        onFailure = { error ->
                            _state.value = JuegosState.Error("Error guardando offline: ${error.message}")
                            Log.e("JuegosViewModel", "❌ Error guardando offline: ${error.message}")
                        }
                    )
                    return@launch
                }

                _state.value = JuegosState.Loading
                Log.d("JuegosViewModel", "Creando juego online: ${juego.nombre}")

                createJuegoUseCase(juego).fold(
                    onSuccess = {
                        Log.d("JuegosViewModel", "✅ Juego creado exitosamente")
                        lastFailedOperation = null
                        loadJuegos()
                    },
                    onFailure = { error ->
                        Log.e("JuegosViewModel", "❌ Error: ${error.message}")
                        lastFailedOperation = { createJuego(juego) }
                        _state.value = JuegosState.Error(error.message ?: "Error desconocido")
                    }
                )
            } catch (e: Exception) {
                Log.e("JuegosViewModel", "❌ Excepción: ${e.message}", e)
                lastFailedOperation = { createJuego(juego) }
                _state.value = JuegosState.Error("Error inesperado: ${e.message}")
            }
        }
    }

    fun updateJuego(juego: Juego) {
        viewModelScope.launch {
            val isValid = checkTokenValid()
            if (!isValid) return@launch

            if (!_connectionStatus.value) {
                _state.value = JuegosState.Error("Sin conexión a internet")
                lastFailedOperation = { updateJuego(juego) }
                return@launch
            }

            _state.value = JuegosState.Loading

            updateJuegoUseCase(juego).fold(
                onSuccess = { juegoActualizado ->
                    _state.value = JuegosState.ActionSuccess("Juego actualizado exitosamente")
                    lastFailedOperation = null
                    Log.d("JuegosViewModel", "Juego actualizado: ${juegoActualizado.nombre}")
                    loadJuegos()
                },
                onFailure = { exception ->
                    Log.e("JuegosViewModel", "Error actualizando juego: ${exception.message}")
                    lastFailedOperation = { updateJuego(juego) }
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

            if (!_connectionStatus.value) {
                _state.value = JuegosState.Error("Sin conexión a internet")
                lastFailedOperation = { deleteJuego(id) }
                return@launch
            }

            _state.value = JuegosState.Loading

            deleteJuegoUseCase(id).fold(
                onSuccess = {
                    _state.value = JuegosState.ActionSuccess("Juego eliminado exitosamente")
                    lastFailedOperation = null
                    Log.d("JuegosViewModel", "Juego eliminado con ID: $id")
                    loadJuegos()
                },
                onFailure = { exception ->
                    Log.e("JuegosViewModel", "Error eliminando juego: ${exception.message}")
                    lastFailedOperation = { deleteJuego(id) }
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
    data class OfflineSaved(val message: String) : JuegosState() // ✅ Nuevo estado para guardado offline
}

sealed class CameraState {
    object Idle : CameraState()
    object RequestingPermission : CameraState()
    object PermissionGranted : CameraState()
    data class PermissionDenied(val message: String) : CameraState()
    object Capturing : CameraState()
    data class PhotoCaptured(val base64Image: String) : CameraState()
    data class Error(val message: String) : CameraState()
}