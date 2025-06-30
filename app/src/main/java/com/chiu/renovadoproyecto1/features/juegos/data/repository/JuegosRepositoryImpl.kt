package com.chiu.renovadoproyecto1.features.juegos.data.repository

import com.chiu.renovadoproyecto1.features.juegos.data.datasource.remote.JuegosService
import com.chiu.renovadoproyecto1.features.juegos.data.model.CreateJuegoDto
import com.chiu.renovadoproyecto1.features.juegos.data.model.UpdateJuegoDto
import com.chiu.renovadoproyecto1.features.juegos.domain.model.Juego
import com.chiu.renovadoproyecto1.features.juegos.domain.repository.JuegosRepository

class JuegosRepositoryImpl (
    private val juegosService: JuegosService
) : JuegosRepository {

    override suspend fun getJuegos(): Result<List<Juego>> {
        return try {
            val response = juegosService.getJuegos()
            if (response.isSuccessful && response.body() != null) {
                val juegos = response.body()!!.map { it.toDomain() }
                Result.success(juegos)
            } else {
                Result.failure(Exception("Error al obtener juegos: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createJuego(juego: Juego): Result<Juego> {
        return try {
            val createDto = CreateJuegoDto(
                nombre = juego.nombre ?: "",
                compania = juego.compania ?: "",
                descripcion = juego.descripcion ?: "",
                cantidad = juego.cantidad ?: 0
            )
            val response = juegosService.createJuego(createDto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error al crear juego: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateJuego(juego: Juego): Result<Juego> {
        return try {
            val updateDto = UpdateJuegoDto(
                nombre = juego.nombre ?: "",
                compania = juego.compania ?: "",
                descripcion = juego.descripcion ?: "",
                cantidad = juego.cantidad ?: 0
            )
            val response = juegosService.updateJuego(juego.id!!, updateDto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error al actualizar juego: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteJuego(id: Int): Result<Unit> {
        return try {
            val response = juegosService.deleteJuego(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar juego: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}