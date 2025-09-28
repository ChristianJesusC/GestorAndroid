package com.chiu.renovadoproyecto1.features.usuarios.domain.repository

import com.chiu.renovadoproyecto1.features.usuarios.domain.model.Usuario

interface UsuariosRepository {
    suspend fun obtenerTodos(): Result<List<Usuario>>
}