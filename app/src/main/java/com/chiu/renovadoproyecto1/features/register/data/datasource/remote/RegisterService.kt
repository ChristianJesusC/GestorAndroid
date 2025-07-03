package com.chiu.renovadoproyecto1.features.register.data.datasource.remote

import com.chiu.renovadoproyecto1.features.register.data.model.RegisterResponse
import com.chiu.renovadoproyecto1.features.register.data.model.UsuarioRequest
import retrofit2.http.Body
import retrofit2.http.POST

    interface RegisterService {
    @POST("usuarios/registrar")
    suspend fun register(@Body usuarioRequest: UsuarioRequest): RegisterResponse
}