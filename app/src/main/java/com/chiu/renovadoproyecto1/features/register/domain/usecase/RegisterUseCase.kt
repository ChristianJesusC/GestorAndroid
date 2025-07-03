package com.chiu.renovadoproyecto1.features.register.domain.usecase

import android.util.Log
import com.chiu.renovadoproyecto1.features.register.domain.model.Usuario
import com.chiu.renovadoproyecto1.features.register.domain.repository.RegisterRepository

class RegisterUseCase(
    private val repository: RegisterRepository
) {
    suspend operator fun invoke(username: String, password: String, confirmPassword: String): Result<String> {
        return try {
            if (username.isBlank()) {
                return Result.failure(Exception("El nombre de usuario no puede estar vacío"))
            }

            if (username.length < 3) {
                return Result.failure(Exception("El nombre de usuario debe tener al menos 3 caracteres"))
            }

            if (password.isBlank()) {
                return Result.failure(Exception("La contraseña no puede estar vacía"))
            }

            if (password.length < 6) {
                return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
            }

            if (password != confirmPassword) {
                return Result.failure(Exception("Las contraseñas no coinciden"))
            }

            if (!isPasswordSecure(password)) {
                return Result.failure(Exception("La contraseña debe contener al menos una letra mayúscula, una minúscula y un número"))
            }

            Log.d("RegisterUseCase", "Iniciando registro para usuario: $username")

            val usuario = Usuario(
                username = username.trim(),
                password = password
            )

            repository.register(usuario)

        } catch (e: Exception) {
            Log.e("RegisterUseCase", "Error en RegisterUseCase: ${e.message}")
            Result.failure(e)
        }
    }

    private fun isPasswordSecure(password: String): Boolean {
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        return hasUpperCase && hasLowerCase && hasDigit
    }
}