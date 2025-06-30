package com.chiu.renovadoproyecto1.features.juegos.domain.usecase

import com.chiu.renovadoproyecto1.features.juegos.domain.model.Juego
import com.chiu.renovadoproyecto1.features.juegos.domain.repository.JuegosRepository
import com.chiu.renovadoproyecto1.features.login.domain.repository.TokenRepository

class CreateJuegoUseCase (
    private val juegosRepository: JuegosRepository,
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke(juego: Juego): Result<Juego> {
        if (!tokenRepository.hasToken()) {
            return Result.failure(Exception("No hay token de autenticación"))
        }

        val result = juegosRepository.createJuego(juego)

        result.onFailure { exception ->
            if (exception.message?.contains("401") == true) {
                tokenRepository.clearToken()
            }
        }

        return result
    }
}