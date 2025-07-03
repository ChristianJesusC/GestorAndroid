package com.chiu.renovadoproyecto1.core.hardware.domain.Biometric

class AuthenticateBiometricUseCase(
    private val biometricAuthenticator: BiometricAuthenticator
) {
    fun execute(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (biometricAuthenticator.isAvailable()) {
            biometricAuthenticator.authenticate(onSuccess, onError)
        } else {
            onError("Autenticación biométrica no disponible")
        }
    }

    fun isAvailable(): Boolean = biometricAuthenticator.isAvailable()
}