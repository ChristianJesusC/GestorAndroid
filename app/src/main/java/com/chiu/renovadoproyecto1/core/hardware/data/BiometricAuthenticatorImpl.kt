package com.chiu.renovadoproyecto1.core.hardware.data

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.chiu.renovadoproyecto1.core.hardware.domain.Biometric.BiometricAuthenticator

class BiometricAuthenticatorImpl(
    private val context: Context,
    private val activity: FragmentActivity
) : BiometricAuthenticator {

    override fun isAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    override fun authenticate(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val executor = ContextCompat.getMainExecutor(context)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación Biométrica")
            .setSubtitle("Usa tu huella digital para acceder")
            .setNegativeButtonText("Cancelar")
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Autenticación fallida. Inténtalo de nuevo.")
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }
}