package com.chiu.renovadoproyecto1.features.juegos.data.datasource.remote

import com.chiu.renovadoproyecto1.features.juegos.data.model.JuegoDto
import com.chiu.renovadoproyecto1.features.juegos.data.model.JuegosResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface JuegosService {
    @GET("juegos")
    suspend fun getJuegos(): Response<JuegosResponseDto>

    @Multipart
    @POST("juegos")
    suspend fun createJuego(
        @Part("nombre") nombre: RequestBody,
        @Part("compania") compania: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part("cantidad") cantidad: RequestBody,
        @Part logo: MultipartBody.Part
    ): Response<JuegoDto>

    @Multipart
    @PUT("juegos/{id}")
    suspend fun updateJuego(
        @Path("id") id: Int,
        @Part("nombre") nombre: RequestBody,
        @Part("compania") compania: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part("cantidad") cantidad: RequestBody,
        @Part logo: MultipartBody.Part?
    ): Response<JuegoDto>

    @DELETE("juegos/{id}")
    suspend fun deleteJuego(@Path("id") id: Int): Response<Unit>
}