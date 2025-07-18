package com.chiu.renovadoproyecto1.core.offline.data.usecase

import android.util.Log
import com.chiu.renovadoproyecto1.core.offline.domain.repository.OfflineRepository
import com.chiu.renovadoproyecto1.core.offline.domain.usecase.SaveOfflineJuegoUseCase
import com.chiu.renovadoproyecto1.features.juegos.domain.model.Juego

class SaveOfflineJuegoUseCaseImpl(
    private val offlineRepository: OfflineRepository
) : SaveOfflineJuegoUseCase {

    override suspend operator fun invoke(juego: Juego): Result<Unit> {
        return try {
            Log.d("SaveOfflineUseCase", "🔄 Intentando guardar juego offline: ${juego.nombre}")

            val result = offlineRepository.saveOfflineJuego(juego)

            result.fold(
                onSuccess = {
                    Log.d("SaveOfflineUseCase", "✅ Juego guardado exitosamente en Room")
                },
                onFailure = { error ->
                    Log.e("SaveOfflineUseCase", "❌ Error guardando: ${error.message}")
                }
            )

            result
        } catch (e: Exception) {
            Log.e("SaveOfflineUseCase", "❌ Excepción guardando offline: ${e.message}", e)
            Result.failure(e)
        }
    }
}