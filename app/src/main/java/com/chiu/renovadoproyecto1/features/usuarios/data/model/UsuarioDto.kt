package com.chiu.renovadoproyecto1.features.usuarios.data.model

import com.chiu.renovadoproyecto1.features.usuarios.domain.model.Usuario

data class UsuarioDto(
    val id: Int,
    val username: String,
    val password: String
) {
    fun toDomain() = Usuario(
        id = id,
        username = username
    )
}

data class UsuariosResponseDto(
    val message: String,
    val usuarios: List<UsuarioDto>
)