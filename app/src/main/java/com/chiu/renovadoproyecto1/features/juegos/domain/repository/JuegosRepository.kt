package com.chiu.renovadoproyecto1.features.juegos.domain.repository

import com.chiu.renovadoproyecto1.features.juegos.domain.model.Juego

interface JuegosRepository {
    suspend fun getJuegos(): Result<List<Juego>>
    suspend fun createJuego(juego: Juego): Result<Juego>
    suspend fun updateJuego(juego: Juego): Result<Juego>
    suspend fun deleteJuego(id: Int): Result<Unit>
}