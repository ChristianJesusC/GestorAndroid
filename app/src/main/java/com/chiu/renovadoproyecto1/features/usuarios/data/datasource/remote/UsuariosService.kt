package com.chiu.renovadoproyecto1.features.usuarios.data.datasource.remote

import com.chiu.renovadoproyecto1.features.usuarios.data.model.UsuariosResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface UsuariosService {
    @GET("usuarios/obtenerTodos")
    suspend fun obtenerTodos(): Response<UsuariosResponseDto>
}