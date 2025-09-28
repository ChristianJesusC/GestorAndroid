package com.chiu.renovadoproyecto1.core.service.domain.usecase

import android.util.Log
import com.chiu.renovadoproyecto1.core.service.domain.repository.SyncRepository

interface SyncOfflineDataUseCase {
    suspend operator fun invoke(): Result<SyncResult>
}

data class SyncResult(
    val totalProcessed: Int,
    val successCount: Int,
    val failureCount: Int,
    val errors: List<String> = emptyList()
)

class SyncOfflineDataUseCaseImpl(
    private val syncRepository: SyncRepository
) : SyncOfflineDataUseCase {

    override suspend operator fun invoke(): Result<SyncResult> {
        return try {
            Log.d("SyncUseCase", "Iniciando sincronización de datos offline")

            val offlineJuegos = syncRepository.getAllOfflineJuegos()

            if (offlineJuegos.isEmpty()) {
                Log.d("SyncUseCase", "ℹ️ No hay juegos offline para sincronizar")
                return Result.success(
                    SyncResult(
                        totalProcessed = 0,
                        successCount = 0,
                        failureCount = 0
                    )
                )
            }

            Log.d("SyncUseCase", "📊 Encontrados ${offlineJuegos.size} juegos offline")

            var successCount = 0
            var failureCount = 0
            val errors = mutableListOf<String>()

            offlineJuegos.forEach { offlineJuego ->
                try {
                    Log.d("SyncUseCase", "⬆️ Subiendo: ${offlineJuego.nombre}")

                    syncRepository.uploadJuegoToApi(offlineJuego).fold(
                        onSuccess = {
                            Log.d("SyncUseCase", "✅ ${offlineJuego.nombre} subido exitosamente")

                            syncRepository.deleteOfflineJuego(offlineJuego).fold(
                                onSuccess = {
                                    Log.d("SyncUseCase", "🗑️ ${offlineJuego.nombre} eliminado de Room")
                                    successCount++
                                },
                                onFailure = { error ->
                                    Log.e("SyncUseCase", "❌ Error eliminando ${offlineJuego.nombre}: ${error.message}")
                                    errors.add("Error eliminando ${offlineJuego.nombre}: ${error.message}")
                                    failureCount++
                                }
                            )
                        },
                        onFailure = { error ->
                            Log.e("SyncUseCase", "❌ Error subiendo ${offlineJuego.nombre}: ${error.message}")
                            errors.add("Error subiendo ${offlineJuego.nombre}: ${error.message}")
                            failureCount++
                        }
                    )
                } catch (e: Exception) {
                    Log.e("SyncUseCase", "❌ Excepción procesando ${offlineJuego.nombre}: ${e.message}")
                    errors.add("Excepción en ${offlineJuego.nombre}: ${e.message}")
                    failureCount++
                }
            }

            val result = SyncResult(
                totalProcessed = offlineJuegos.size,
                successCount = successCount,
                failureCount = failureCount,
                errors = errors
            )

            Log.d("SyncUseCase", "📋 Sincronización completada: $result")

            Result.success(result)

        } catch (e: Exception) {
            Log.e("SyncUseCase", "💥 Error crítico en sincronización: ${e.message}", e)
            Result.failure(e)
        }
    }
}