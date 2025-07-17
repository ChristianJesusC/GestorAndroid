package com.chiu.renovadoproyecto1.core.hardware.di

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.chiu.renovadoproyecto1.core.camera.data.CameraManagerImpl
import com.chiu.renovadoproyecto1.core.hardware.data.BiometricAuthenticatorImpl
import com.chiu.renovadoproyecto1.core.hardware.domain.Biometric.AuthenticateBiometricUseCase
import com.chiu.renovadoproyecto1.core.hardware.domain.Biometric.BiometricAuthenticator
import com.chiu.renovadoproyecto1.core.hardware.domain.Camera.CameraManager
import com.chiu.renovadoproyecto1.core.hardware.domain.Camera.CapturePhotoUseCase

object HardwareModule {

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
        return try {
            val cameraManager = CameraManagerImpl(context, activity)
            cameraManager.initialize()
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
}