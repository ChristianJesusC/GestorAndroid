package com.chiu.renovadoproyecto1.features.login.domain.repository

interface TokenRepository {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun clearToken()
    suspend fun hasToken(): Boolean
    suspend fun isTokenValid():Boolean
}