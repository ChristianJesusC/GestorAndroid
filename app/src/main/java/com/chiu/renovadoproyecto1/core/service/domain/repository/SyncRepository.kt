package com.chiu.renovadoproyecto1.core.service.domain.repository

import com.chiu.renovadoproyecto1.core.database.entities.OfflineJuegoEntity

interface SyncRepository {
    suspend fun syncOfflineJuegos(): Result<Int>
    suspend fun uploadJuegoToApi(offlineJuego: OfflineJuegoEntity): Result<Unit>
    suspend fun deleteOfflineJuego(offlineJuego: OfflineJuegoEntity): Result<Unit>
    suspend fun getAllOfflineJuegos(): List<OfflineJuegoEntity>
}