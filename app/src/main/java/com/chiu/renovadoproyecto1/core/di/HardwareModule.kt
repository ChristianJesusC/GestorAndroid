package com.chiu.renovadoproyecto1.core.hardware.di

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.chiu.renovadoproyecto1.core.hardware.data.BiometricAuthenticatorImpl
import com.chiu.renovadoproyecto1.core.hardware.domain.Biometric.AuthenticateBiometricUseCase
import com.chiu.renovadoproyecto1.core.hardware.domain.Biometric.BiometricAuthenticator

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
}