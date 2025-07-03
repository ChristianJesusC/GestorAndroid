package com.chiu.renovadoproyecto1.features.register.domain.repository

import com.chiu.renovadoproyecto1.features.register.domain.model.Usuario

interface RegisterRepository {
    suspend fun register(usuario: Usuario): Result<String>
}