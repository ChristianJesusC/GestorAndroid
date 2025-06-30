package com.chiu.renovadoproyecto1.features.login.data.model

import com.chiu.renovadoproyecto1.features.login.domain.model.LoginResponse

data class LoginResponseDto(
    val mensaje: String,
    val token: String
){
    fun toDomain() = LoginResponse(mensaje, token)
}
