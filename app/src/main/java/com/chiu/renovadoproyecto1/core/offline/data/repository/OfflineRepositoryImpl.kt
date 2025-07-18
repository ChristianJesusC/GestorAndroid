package com.chiu.renovadoproyecto1.core.offline.data.repository

import android.util.Log
import com.chiu.renovadoproyecto1.core.database.dao.OfflineJuegosDao
import com.chiu.renovadoproyecto1.core.database.entities.OfflineJuegoEntity
import com.chiu.renovadoproyecto1.core.offline.domain.repository.OfflineRepository
import com.chiu.renovadoproyecto1.features.juegos.domain.model.Juego
import kotlinx.coroutines.flow.Flow

class OfflineRepositoryImpl(
    private val offlineJuegosDao: OfflineJuegosDao
) : OfflineRepository {

    override suspend fun saveOfflineJuego(juego: Juego): Result<Unit> {
        return try {
            val offlineJuego = OfflineJuegoEntity(
                nombre = juego.nombre,
                compania = juego.compania,
                descripcion = juego.descripcion,
                cantidad = juego.cantidad,
                logo = juego.logo,
                fechaCreacion = System.currentTimeMillis()
            )

            offlineJuegosDao.insertOfflineJuego(offlineJuego)
            Log.d("OfflineRepository", "Juego guardado offline: ${juego.nombre}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("OfflineRepository", "Error guardando juego offline: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getAllOfflineJuegos(): List<OfflineJuegoEntity> {
        return offlineJuegosDao.getAllOfflineJuegos()
    }

    override fun getOfflineJuegosCountFlow(): Flow<Int> {
        return offlineJuegosDao.getOfflineJuegosCountFlow()
    }

    override suspend fun getOfflineJuegosCount(): Int {
        return offlineJuegosDao.getOfflineJuegosCount()
    }
}