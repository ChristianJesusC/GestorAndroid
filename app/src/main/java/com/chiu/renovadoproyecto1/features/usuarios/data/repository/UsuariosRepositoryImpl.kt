package com.chiu.renovadoproyecto1.features.usuarios.data.repository

import android.util.Log
import com.chiu.renovadoproyecto1.features.usuarios.data.datasource.remote.UsuariosService
import com.chiu.renovadoproyecto1.features.usuarios.domain.model.Usuario
import com.chiu.renovadoproyecto1.features.usuarios.domain.repository.UsuariosRepository

class UsuariosRepositoryImpl(
    private val usuariosService: UsuariosService
) : UsuariosRepository {

    override suspend fun obtenerTodos(): Result<List<Usuario>> {
        return try {
            val response = usuariosService.obtenerTodos()

            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                // ✅ Ya no verificamos "success", solo si hay usuarios
                val usuarios = responseBody.usuarios.map { it.toDomain() }
                Log.d("UsuariosRepository", "✅ ${usuarios.size} usuarios obtenidos")
                Result.success(usuarios)
            } else {
                Result.failure(Exception("Error al obtener usuarios: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("UsuariosRepository", "Error: ${e.message}")
            Result.failure(e)
        }
    }
}