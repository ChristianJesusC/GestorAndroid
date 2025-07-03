package com.chiu.renovadoproyecto1.core.hardware.domain.Biometric

interface BiometricAuthenticator {
    fun authenticate(onSuccess: () -> Unit, onError: (String) -> Unit)
    fun isAvailable(): Boolean
}