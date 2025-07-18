package com.chiu.renovadoproyecto1.core.offline.domain.repository

import com.chiu.renovadoproyecto1.core.database.entities.OfflineJuegoEntity
import com.chiu.renovadoproyecto1.features.juegos.domain.model.Juego
import kotlinx.coroutines.flow.Flow

interface OfflineRepository {
    suspend fun saveOfflineJuego(juego: Juego): Result<Unit>
    suspend fun getAllOfflineJuegos(): List<OfflineJuegoEntity>
    fun getOfflineJuegosCountFlow(): Flow<Int>
    suspend fun getOfflineJuegosCount(): Int
}