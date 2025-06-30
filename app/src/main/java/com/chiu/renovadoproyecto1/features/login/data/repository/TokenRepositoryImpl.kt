package com.chiu.renovadoproyecto1.features.login.data.repository

import android.util.Log
import com.chiu.renovadoproyecto1.core.datastore.DataStoreManager
import com.chiu.renovadoproyecto1.core.jwt.JwtHelper
import com.chiu.renovadoproyecto1.features.login.domain.repository.TokenRepository

class TokenRepositoryImpl(
    private val dataStore: DataStoreManager
) : TokenRepository {

    override suspend fun getToken(): String? {
        return try {
            dataStore.getTokenSync()
        } catch (e: Exception) {
            Log.e("TokenRepository", "Error obteniendo token: ${e.message}")
            null
        }
    }

    override suspend fun saveToken(token: String) {
        try {
            dataStore.saveToken(token)
            Log.d("TokenRepository", "Token guardado exitosamente")
        } catch (e: Exception) {
            Log.e("TokenRepository", "Error guardando token: ${e.message}")
        }
    }

    override suspend fun clearToken() {
        try {
            dataStore.clearToken()
            Log.d("TokenRepository", "Token eliminado")
        } catch (e: Exception) {
            Log.e("TokenRepository", "Error eliminando token: ${e.message}")
        }
    }

    override suspend fun hasToken(): Boolean {
        return try {
            val token = dataStore.getTokenSync()
            !token.isNullOrEmpty()
        } catch (e: Exception) {
            Log.e("TokenRepository", "Error verificando token: ${e.message}")
            false
        }
    }

    override suspend fun isTokenValid(): Boolean {
        return try {
            val token = getToken()
            if (token.isNullOrEmpty()) {
                Log.d("TokenRepository", "No hay token")
                return false
            }

            val isExpired = JwtHelper.isTokenExpired(token)
            if (isExpired) {
                Log.d("TokenRepository", "Token expirado, eliminando")
                clearToken()
                return false
            }

            Log.d("TokenRepository", "Token válido")
            return true
        } catch (e: Exception) {
            Log.e("TokenRepository", "Error validando token: ${e.message}")
            clearToken()
            false
        }
    }
}