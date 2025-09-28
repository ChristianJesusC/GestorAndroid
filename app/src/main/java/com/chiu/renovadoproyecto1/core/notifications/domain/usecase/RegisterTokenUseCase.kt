package com.chiu.renovadoproyecto1.core.notifications.domain.usecase

import android.util.Log
import com.chiu.renovadoproyecto1.core.notifications.domain.model.NotificationToken
import com.chiu.renovadoproyecto1.core.notifications.domain.repository.NotificationRepository
import com.chiu.renovadoproyecto1.features.login.domain.repository.TokenRepository

class RegisterTokenUseCase(
    private val notificationRepository: NotificationRepository,
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke(fcmToken: String, platform: String): Result<String> {
        return try {
            if (!tokenRepository.isTokenValid()) {
                Log.d("RegisterTokenUseCase", "No hay sesión activa, no se registra token FCM")
                return Result.success("Token guardado localmente para registro posterior")
            }

            Log.d("RegisterTokenUseCase", "Registrando token FCM en servidor...")

            val notificationToken = NotificationToken(
                token = fcmToken,
                platform = platform
            )

            val result = notificationRepository.registerToken(notificationToken)

            result.fold(
                onSuccess = { message ->
                    notificationRepository.saveToken(fcmToken)
                    Log.d("RegisterTokenUseCase", "Token registrado: $message")
                },
                onFailure = { error ->
                    Log.e("RegisterTokenUseCase", "Error registrando token: ${error.message}")
                }
            )

            result
        } catch (e: Exception) {
            Log.e("RegisterTokenUseCase", "Excepción en RegisterTokenUseCase: ${e.message}")
            Result.failure(e)
        }
    }
}