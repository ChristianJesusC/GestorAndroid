package com.chiu.renovadoproyecto1.core.notifications.data.repository

import android.util.Log
import com.chiu.renovadoproyecto1.core.datastore.DataStoreManager
import com.chiu.renovadoproyecto1.core.notifications.data.datasource.remote.NotificationService
import com.chiu.renovadoproyecto1.core.notifications.data.model.RegisterTokenRequest
import com.chiu.renovadoproyecto1.core.notifications.domain.model.NotificationToken
import com.chiu.renovadoproyecto1.core.notifications.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val notificationService: NotificationService,
    private val dataStoreManager: DataStoreManager
) : NotificationRepository {

    override suspend fun registerToken(token: NotificationToken): Result<String> {
        return try {
            Log.d("NotificationRepository", "📤 Enviando token al servidor...")

            val request = RegisterTokenRequest(
                token = token.token,
                platform = token.platform
            )

            val response = notificationService.registerToken(request)

            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                if (responseBody.success) {
                    Log.d("NotificationRepository", "✅ Token registrado exitosamente")
                    Result.success(responseBody.mensaje)
                } else {
                    Log.e("NotificationRepository", "❌ Error del servidor: ${responseBody.mensaje}")
                    Result.failure(Exception(responseBody.mensaje))
                }
            } else {
                val errorMsg = "Error HTTP ${response.code()}: ${response.message()}"
                Log.e("NotificationRepository", "❌ $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("NotificationRepository", "❌ Excepción registrando token: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun unregisterToken(token: String): Result<String> {
        return try {
            val request = RegisterTokenRequest(token = token, platform = "android")
            val response = notificationService.unregisterToken(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.mensaje)
            } else {
                Result.failure(Exception("Error desregistrando token"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStoredToken(): String? {
        return try {
            dataStoreManager.getFCMToken()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveToken(token: String) {
        try {
            dataStoreManager.saveFCMToken(token)
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error guardando token FCM: ${e.message}")
        }
    }

    override suspend fun clearToken() {
        try {
            dataStoreManager.clearFCMToken()
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error eliminando token FCM: ${e.message}")
        }
    }
}