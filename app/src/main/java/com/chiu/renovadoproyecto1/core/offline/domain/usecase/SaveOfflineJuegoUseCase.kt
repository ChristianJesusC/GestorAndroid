package com.chiu.renovadoproyecto1.core.offline.domain.usecase

import com.chiu.renovadoproyecto1.features.juegos.domain.model.Juego

interface SaveOfflineJuegoUseCase {
    suspend operator fun invoke(juego: Juego): Result<Unit>
}