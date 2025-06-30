package com.chiu.renovadoproyecto1.features.juegos.data.datasource.remote

import com.chiu.renovadoproyecto1.features.juegos.data.model.CreateJuegoDto
import com.chiu.renovadoproyecto1.features.juegos.data.model.JuegoDto
import com.chiu.renovadoproyecto1.features.juegos.data.model.UpdateJuegoDto
import retrofit2.Response
import retrofit2.http.*

interface JuegosService {
    @GET("juegos")
    suspend fun getJuegos(): Response<List<JuegoDto>>

    @POST("juegos")
    suspend fun createJuego(@Body juego: CreateJuegoDto): Response<JuegoDto>

    @PUT("juegos/{id}")
    suspend fun updateJuego(
        @Path("id") id: Int,
        @Body juego: UpdateJuegoDto
    ): Response<JuegoDto>

    @DELETE("juegos/{id}")
    suspend fun deleteJuego(@Path("id") id: Int): Response<Unit>
}