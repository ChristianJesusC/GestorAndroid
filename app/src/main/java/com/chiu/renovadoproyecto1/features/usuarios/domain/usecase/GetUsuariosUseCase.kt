package com.chiu.renovadoproyecto1.features.usuarios.domain.usecase

import com.chiu.renovadoproyecto1.features.usuarios.domain.model.Usuario
import com.chiu.renovadoproyecto1.features.usuarios.domain.repository.UsuariosRepository
import com.chiu.renovadoproyecto1.features.login.domain.repository.TokenRepository

class GetUsuariosUseCase(
    private val usuariosRepository: UsuariosRepository,
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke(): Result<List<Usuario>> {
        if (!tokenRepository.hasToken()) {
            return Result.failure(Exception("No hay token de autenticación"))
        }

        val result = usuariosRepository.obtenerTodos()

        result.onFailure { exception ->
            if (exception.message?.contains("401") == true) {
                tokenRepository.clearToken()
            }
        }

        return result
    }
}