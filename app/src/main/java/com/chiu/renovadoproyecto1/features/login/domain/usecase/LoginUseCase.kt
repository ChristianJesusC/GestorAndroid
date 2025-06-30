package com.chiu.renovadoproyecto1.features.login.domain.usecase

import android.util.Log
import com.chiu.renovadoproyecto1.features.login.domain.model.LoginResponse
import com.chiu.renovadoproyecto1.features.login.domain.model.User
import com.chiu.renovadoproyecto1.features.login.domain.repository.LoginRepository
import com.chiu.renovadoproyecto1.features.login.domain.repository.TokenRepository

class LoginUseCase (
    private val loginRepository: LoginRepository,
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<LoginResponse> {
        val user = User(username, password)
        val result = loginRepository.login(user)

        result.onSuccess { loginResponse ->
            Log.d("LoginUseCase", "Login exitoso, guardando token")
            tokenRepository.saveToken(loginResponse.token)
        }.onFailure { exception ->
            Log.e("LoginUseCase", "Error en login: ${exception.message}")
            tokenRepository.clearToken()
        }

        return result
    }

    suspend fun logout() {
        Log.d("LoginUseCase", "Cerrando sesión")
        tokenRepository.clearToken()
    }

    suspend fun isLoggedIn(): Boolean {
        return try {
            val isValid = tokenRepository.isTokenValid()
            Log.d("LoginUseCase", "Usuario logueado: $isValid")
            return isValid
        } catch (e: Exception) {
            Log.e("LoginUseCase", "Error verificando login: ${e.message}")
            tokenRepository.clearToken()
            false
        }
    }
}