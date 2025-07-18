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
            Log.d("OfflineRepository", "📝 Guardando en base de datos...")

            val offlineJuego = OfflineJuegoEntity(
                nombre = juego.nombre,
                compania = juego.compania,
                descripcion = juego.descripcion,
                cantidad = juego.cantidad,
                logo = juego.logo,
                fechaCreacion = System.currentTimeMillis()
            )

            val insertedId = offlineJuegosDao.insertOfflineJuego(offlineJuego)

            val count = offlineJuegosDao.getOfflineJuegosCount()
            Log.d("OfflineRepository", "📈 Total juegos offline en BD: $count")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("OfflineRepository", "❌ Error en saveOfflineJuego: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllOfflineJuegos(): List<OfflineJuegoEntity> {
        val juegos = offlineJuegosDao.getAllOfflineJuegos()
        Log.d("OfflineRepository", "📋 Obtenidos ${juegos.size} juegos offline")
        return juegos
    }

    override fun getAllOfflineJuegosFlow(): Flow<List<OfflineJuegoEntity>> {
        return offlineJuegosDao.getAllOfflineJuegosFlow()
    }

    override fun getOfflineJuegosCountFlow(): Flow<Int> {
        return offlineJuegosDao.getOfflineJuegosCountFlow()
    }

    override suspend fun getOfflineJuegosCount(): Int {
        return offlineJuegosDao.getOfflineJuegosCount()
    }

    override suspend fun deleteOfflineJuego(juego: OfflineJuegoEntity) {
        offlineJuegosDao.deleteOfflineJuego(juego)
    }

    override suspend fun deleteOfflineJuegoById(id: Int) {
        offlineJuegosDao.deleteOfflineJuegoById(id)
    }

    override suspend fun deleteAllOfflineJuegos() {
        offlineJuegosDao.deleteAllOfflineJuegos()
    }
}