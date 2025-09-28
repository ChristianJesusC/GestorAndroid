package com.chiu.renovadoproyecto1.core.service.data.repository

import android.util.Log
import com.chiu.renovadoproyecto1.core.database.entities.OfflineJuegoEntity
import com.chiu.renovadoproyecto1.core.offline.domain.repository.OfflineRepository
import com.chiu.renovadoproyecto1.core.service.domain.repository.SyncRepository
import com.chiu.renovadoproyecto1.features.juegos.domain.model.Juego
import com.chiu.renovadoproyecto1.features.juegos.domain.usecase.CreateJuegoUseCase

class SyncRepositoryImpl(
    private val offlineRepository: OfflineRepository,
    private val createJuegoUseCase: CreateJuegoUseCase
) : SyncRepository {

    override suspend fun syncOfflineJuegos(): Result<Int> {
        return try {
            val offlineJuegos = offlineRepository.getAllOfflineJuegos()
            var successCount = 0

            offlineJuegos.forEach { offlineJuego ->
                uploadJuegoToApi(offlineJuego).fold(
                    onSuccess = {
                        deleteOfflineJuego(offlineJuego).fold(
                            onSuccess = { successCount++ },
                            onFailure = { "Error" }
                        )
                    },
                    onFailure = {"Error"}
                )
            }

            Result.success(successCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadJuegoToApi(offlineJuego: OfflineJuegoEntity): Result<Unit> {
        return try {
            Log.d("SyncRepository", "⬆️ Subiendo ${offlineJuego.nombre} a la API")

            val juego = Juego(
                id = null,
                nombre = offlineJuego.nombre,
                compania = offlineJuego.compania,
                descripcion = offlineJuego.descripcion,
                cantidad = offlineJuego.cantidad,
                logo = offlineJuego.logo,
                isOffline = false
            )

            createJuegoUseCase(juego).fold(
                onSuccess = {
                    Log.d("SyncRepository", "✅ ${offlineJuego.nombre} subido exitosamente")
                    Result.success(Unit)
                },
                onFailure = { error ->
                    Log.e("SyncRepository", "❌ Error subiendo ${offlineJuego.nombre}: ${error.message}")
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Log.e("SyncRepository", "💥 Excepción subiendo ${offlineJuego.nombre}: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteOfflineJuego(offlineJuego: OfflineJuegoEntity): Result<Unit> {
        return try {
            Log.d("SyncRepository", "🗑️ Eliminando ${offlineJuego.nombre} de Room")
            offlineRepository.deleteOfflineJuego(offlineJuego)
            Log.d("SyncRepository", "✅ ${offlineJuego.nombre} eliminado de Room")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SyncRepository", "❌ Error eliminando ${offlineJuego.nombre}: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getAllOfflineJuegos(): List<OfflineJuegoEntity> {
        return offlineRepository.getAllOfflineJuegos()
    }
}