package com.chiu.renovadoproyecto1.core.http.interceptor

import android.util.Log
import com.chiu.renovadoproyecto1.core.datastore.DataStoreManager
import com.chiu.renovadoproyecto1.core.jwt.JwtHelper
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val dataStore: DataStoreManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val publicEndpoints = listOf("/login", "/register")
        val isPublicEndpoint = publicEndpoints.any {
            originalRequest.url.encodedPath.contains(it)
        }

        if (isPublicEndpoint) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking {
            try {
                val token = dataStore.getTokenSync()
                if (!token.isNullOrEmpty() && JwtHelper.isTokenExpired(token)) {
                    Log.w("AuthInterceptor", "Token expirado, eliminando")
                    dataStore.clearToken()
                    return@runBlocking null
                }
                token
            } catch (e: Exception) {
                Log.e("AuthInterceptor", "Error obteniendo token", e)
                null
            }
        }

        val newRequest = if (token != null) {
            Log.d("AuthInterceptor", "Agregando token válido")
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            Log.w("AuthInterceptor", "No hay token válido")
            originalRequest
        }

        val response = chain.proceed(newRequest)

        if (response.code == 401 || response.code == 403) {
            Log.w("AuthInterceptor", "Token rechazado por servidor, eliminando")
            runBlocking {
                dataStore.clearToken()
            }
        }

        return response
    }
}