package com.chiu.renovadoproyecto1.core.notifications.data.datasource.remote

import com.chiu.renovadoproyecto1.core.notifications.data.model.RegisterTokenRequest
import com.chiu.renovadoproyecto1.core.notifications.data.model.RegisterTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface NotificationService {
    @POST("notifications/device-token")
    suspend fun registerToken(@Body request: RegisterTokenRequest): Response<RegisterTokenResponse>

    @DELETE("notifications/device-token")
    suspend fun unregisterToken(@Body request: RegisterTokenRequest): Response<RegisterTokenResponse>
}