package com.chiu.renovadoproyecto1.core.notifications.domain.usecase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class GetFCMTokenUseCase {
    suspend operator fun invoke(): Result<String> {
        return try {
            Log.d("GetFCMTokenUseCase", "🔍 Obteniendo token FCM...")

            val token = FirebaseMessaging.getInstance().token.await()

            Log.d("GetFCMTokenUseCase", "✅ Token FCM obtenido: ${token.take(20)}...")
            Result.success(token)
        } catch (e: Exception) {
            Log.e("GetFCMTokenUseCase", "❌ Error obteniendo token FCM: ${e.message}")
            Result.failure(e)
        }
    }
}