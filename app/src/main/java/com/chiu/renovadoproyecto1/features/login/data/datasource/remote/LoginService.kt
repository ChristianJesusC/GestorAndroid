package com.chiu.renovadoproyecto1.features.login.data.datasource.remote

import com.chiu.renovadoproyecto1.features.login.data.model.LoginRequestDto
import com.chiu.renovadoproyecto1.features.login.data.model.LoginResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("usuarios/login")
    suspend fun login(@Body loginRequest: LoginRequestDto): Response<LoginResponseDto>
}