package com.chiu.renovadoproyecto1.features.register.data.repository

import android.util.Log
import com.chiu.renovadoproyecto1.features.register.data.datasource.remote.RegisterService
import com.chiu.renovadoproyecto1.features.register.data.model.UsuarioRequest
import com.chiu.renovadoproyecto1.features.register.domain.model.Usuario
import com.chiu.renovadoproyecto1.features.register.domain.repository.RegisterRepository

class RegisterRepositoryImpl(
    private val registerService: RegisterService
) : RegisterRepository {

    override suspend fun register(usuario: Usuario): Result<String> {
        return try {
            Log.d("RegisterRepository", "Intentando registrar usuario: ${usuario.username}")

            val request = UsuarioRequest(
                username = usuario.username,
                password = usuario.password
            )

            val response = registerService.register(request)

            Log.d("RegisterRepository", "Registro exitoso: ${response.mensaje}")
            Result.success(response.mensaje)

        } catch (e: Exception) {
            Log.e("RegisterRepository", "Error en registro: ${e.message}")
            Result.failure(e)
        }
    }
}