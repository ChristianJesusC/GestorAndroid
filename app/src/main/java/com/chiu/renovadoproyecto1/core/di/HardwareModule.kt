package com.chiu.renovadoproyecto1.core.hardware.di

import android.content.Context
import android.Manifest
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import com.chiu.renovadoproyecto1.core.camera.data.CameraManagerImpl
import com.chiu.renovadoproyecto1.core.hardware.data.BiometricAuthenticatorImpl
import com.chiu.renovadoproyecto1.core.hardware.domain.Biometric.AuthenticateBiometricUseCase
import com.chiu.renovadoproyecto1.core.hardware.domain.Biometric.BiometricAuthenticator
import com.chiu.renovadoproyecto1.core.hardware.domain.Camera.CameraManager
import com.chiu.renovadoproyecto1.core.hardware.domain.Camera.CapturePhotoUseCase

object HardwareModule {

    private var cameraManagerInstance: CameraManager? = null
    private var permissionLauncher: ActivityResultLauncher<String>? = null
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null

    private var permissionCallback: ((Boolean) -> Unit)? = null
    private var cameraCallback: Pair<(Bitmap) -> Unit, (String) -> Unit>? = null

    fun setupLaunchers(
        permissionLauncher: ActivityResultLauncher<String>,
        cameraLauncher: ActivityResultLauncher<Void?>
    ) {
        this.permissionLauncher = permissionLauncher
        this.cameraLauncher = cameraLauncher
        Log.d("HardwareModule", "✅ Launchers configurados")
    }

    fun handlePermissionResult(granted: Boolean) {
        permissionCallback?.invoke(granted)
        permissionCallback = null
    }

    fun handleCameraResult(bitmap: Bitmap?) {
        cameraCallback?.let { (onSuccess, onError) ->
            if (bitmap != null) {
                Log.d("HardwareModule", "✅ Bitmap recibido: ${bitmap.width}x${bitmap.height}")
                onSuccess(bitmap)
            } else {
                Log.e("HardwareModule", "❌ Bitmap es null")
                onError("No se pudo capturar la imagen")
            }
        }
        cameraCallback = null
    }

    fun requestCameraPermission(callback: (Boolean) -> Unit) {
        permissionCallback = callback
        permissionLauncher?.launch(Manifest.permission.CAMERA)
            ?: callback(false)
    }

    fun launchCamera(onSuccess: (Bitmap) -> Unit, onError: (String) -> Unit) {
        cameraCallback = Pair(onSuccess, onError)
        cameraLauncher?.launch(null)
            ?: onError("Camera launcher not available")
    }

    fun provideBiometricAuthenticator(
        context: Context,
        activity: FragmentActivity
    ): BiometricAuthenticator {
        return try {
            BiometricAuthenticatorImpl(context, activity)
        } catch (e: Exception) {
            Log.e("HardwareModule", "Error creando BiometricAuthenticator: ${e.message}")
            object : BiometricAuthenticator {
                override fun authenticate(onSuccess: () -> Unit, onError: (String) -> Unit) {
                    onError("Biometría no disponible")
                }
                override fun isAvailable(): Boolean = false
            }
        }
    }

    fun provideBiometricUseCase(
        context: Context,
        activity: FragmentActivity
    ): AuthenticateBiometricUseCase {
        return try {
            val authenticator = provideBiometricAuthenticator(context, activity)
            AuthenticateBiometricUseCase(authenticator)
        } catch (e: Exception) {
            Log.e("HardwareModule", "Error creando BiometricUseCase: ${e.message}")
            AuthenticateBiometricUseCase(object : BiometricAuthenticator {
                override fun authenticate(onSuccess: () -> Unit, onError: (String) -> Unit) {
                    onError("Biometría no disponible")
                }
                override fun isAvailable(): Boolean = false
            })
        }
    }

    fun provideCameraManager(
        context: Context,
        activity: FragmentActivity
    ): CameraManager {
        return cameraManagerInstance ?: try {
            Log.d("HardwareModule", "🔧 Creando nueva instancia de CameraManager")
            val cameraManager = CameraManagerImpl(context, activity)
            cameraManagerInstance = cameraManager
            cameraManager
        } catch (e: Exception) {
            Log.e("HardwareModule", "Error creando CameraManager: ${e.message}")
            object : CameraManager {
                override fun authenticate(onSuccess: () -> Unit, onError: (String) -> Unit) {
                    onError("Cámara no disponible")
                }
                override fun isAvailable(): Boolean = false
                override fun hasPermission(): Boolean = false
                override fun requestPermission(onResult: (Boolean) -> Unit) {
                    onResult(false)
                }
                override fun capturePhoto(onSuccess: (android.graphics.Bitmap) -> Unit, onError: (String) -> Unit) {
                    onError("Cámara no disponible")
                }
            }
        }
    }

    fun getCapturePhotoUseCase(
        context: Context,
        activity: FragmentActivity
    ): CapturePhotoUseCase {
        return try {
            val cameraManager = provideCameraManager(context, activity)
            CapturePhotoUseCase(cameraManager)
        } catch (e: Exception) {
            Log.e("HardwareModule", "Error creando CapturePhotoUseCase: ${e.message}")
            CapturePhotoUseCase(object : CameraManager {
                override fun authenticate(onSuccess: () -> Unit, onError: (String) -> Unit) {
                    onError("Cámara no disponible")
                }
                override fun isAvailable(): Boolean = false
                override fun hasPermission(): Boolean = false
                override fun requestPermission(onResult: (Boolean) -> Unit) {
                    onResult(false)
                }
                override fun capturePhoto(onSuccess: (android.graphics.Bitmap) -> Unit, onError: (String) -> Unit) {
                    onError("Cámara no disponible")
                }
            })
        }
    }

    fun clearCameraManager() {
        cameraManagerInstance = null
        permissionCallback = null
        cameraCallback = null
        Log.d("HardwareModule", "🧹 Cache de HardwareModule limpiado")
    }
}