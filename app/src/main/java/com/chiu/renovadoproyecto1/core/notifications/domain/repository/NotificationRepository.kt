package com.chiu.renovadoproyecto1.core.notifications.domain.repository

import com.chiu.renovadoproyecto1.core.notifications.domain.model.NotificationToken

interface NotificationRepository {
    suspend fun registerToken(token: NotificationToken): Result<String>
    suspend fun unregisterToken(token: String): Result<String>
    suspend fun getStoredToken(): String?
    suspend fun saveToken(token: String)
    suspend fun clearToken()
}