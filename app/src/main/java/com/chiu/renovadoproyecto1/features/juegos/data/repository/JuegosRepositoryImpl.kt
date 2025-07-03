package com.chiu.renovadoproyecto1.features.juegos.data.repository

import android.content.Context
import android.util.Log
import com.chiu.renovadoproyecto1.core.utils.ImageUtils
import com.chiu.renovadoproyecto1.features.juegos.data.datasource.remote.JuegosService
import com.chiu.renovadoproyecto1.features.juegos.domain.model.Juego
import com.chiu.renovadoproyecto1.features.juegos.domain.repository.JuegosRepository

class JuegosRepositoryImpl(
    private val juegosService: JuegosService,
    private val context: Context
) : JuegosRepository {

    override suspend fun getJuegos(): Result<List<Juego>> {
        return try {
            val response = juegosService.getJuegos()
            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                if (responseBody.success) {
                    val juegos = responseBody.data.map { it.toDomain() }
                    Result.success(juegos)
                } else {
                    Result.failure(Exception("Error del servidor: operación no exitosa"))
                }
            } else {
                Result.failure(Exception("Error al obtener juegos: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createJuego(juego: Juego): Result<Juego> {
        return try {
            Log.d("JuegosRepository", "=== CREANDO JUEGO ===")
            Log.d("JuegosRepository", "Nombre: ${juego.nombre}")
            Log.d("JuegosRepository", "Logo: ${juego.logo?.take(50)}...")

            // Validar campos básicos
            if (juego.nombre.isNullOrBlank()) {
                return Result.failure(Exception("El nombre del juego es obligatorio"))
            }
            if (juego.compania.isNullOrBlank()) {
                return Result.failure(Exception("La compañía es obligatoria"))
            }
            if (juego.descripcion.isNullOrBlank()) {
                return Result.failure(Exception("La descripción es obligatoria"))
            }
            if ((juego.cantidad ?: 0) <= 0) {
                return Result.failure(Exception("La cantidad debe ser mayor a 0"))
            }

            // Validar imagen
            val logoData = juego.logo
            if (logoData.isNullOrBlank()) {
                return Result.failure(Exception("Debe proporcionar una imagen para el juego"))
            }

            // Crear MultipartBody.Part para la imagen
            val logoPart = ImageUtils.createMultipartFromImage(context, logoData)
            if (logoPart == null) {
                return Result.failure(Exception("Error procesando la imagen. Verifique que sea válida."))
            }

            Log.d("JuegosRepository", "Enviando petición al servidor...")

            val response = juegosService.createJuego(
                nombre = ImageUtils.createRequestBody(juego.nombre),
                compania = ImageUtils.createRequestBody(juego.compania),
                descripcion = ImageUtils.createRequestBody(juego.descripcion),
                cantidad = ImageUtils.createRequestBody(juego.cantidad),
                logo = logoPart
            )

            Log.d("JuegosRepository", "Respuesta recibida: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                Log.d("JuegosRepository", "✅ Juego creado exitosamente")
                Result.success(response.body()!!.toDomain())
            } else {
                val errorMsg = "Error HTTP ${response.code()}: ${response.message()}"
                Log.e("JuegosRepository", errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("JuegosRepository", "❌ Error creando juego: ${e.message}", e)
            Result.failure(Exception("Error al crear juego: ${e.message}"))
        }
    }

    override suspend fun updateJuego(juego: Juego): Result<Juego> {
        return try {
            Log.d("JuegosRepository", "=== ACTUALIZANDO JUEGO ===")
            Log.d("JuegosRepository", "ID: ${juego.id}")
            Log.d("JuegosRepository", "Nombre: ${juego.nombre}")

            if (juego.id == null) {
                return Result.failure(Exception("ID del juego requerido para actualizar"))
            }

            // Validar campos básicos
            if (juego.nombre.isNullOrBlank()) {
                return Result.failure(Exception("El nombre del juego es obligatorio"))
            }

            // Para actualización, la imagen es OPCIONAL
            val logoPart = juego.logo?.let { logoData ->
                // Solo procesar si la imagen cambió (no es URL del servidor)
                if (!logoData.startsWith("http")) {
                    ImageUtils.createMultipartFromImage(context, logoData)
                } else {
                    Log.d("JuegosRepository", "Imagen no cambió, no se procesa")
                    null
                }
            }

            Log.d("JuegosRepository", "Enviando actualización al servidor...")

            val response = juegosService.updateJuego(
                id = juego.id,
                nombre = ImageUtils.createRequestBody(juego.nombre),
                compania = ImageUtils.createRequestBody(juego.compania),
                descripcion = ImageUtils.createRequestBody(juego.descripcion),
                cantidad = ImageUtils.createRequestBody(juego.cantidad),
                logo = logoPart
            )

            Log.d("JuegosRepository", "Respuesta recibida: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                Log.d("JuegosRepository", "✅ Juego actualizado exitosamente")
                Result.success(response.body()!!.toDomain())
            } else {
                val errorMsg = "Error HTTP ${response.code()}: ${response.message()}"
                Log.e("JuegosRepository", errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("JuegosRepository", "❌ Error actualizando juego: ${e.message}", e)
            Result.failure(Exception("Error al actualizar juego: ${e.message}"))
        }
    }

    override suspend fun deleteJuego(id: Int): Result<Unit> {
        return try {
            val response = juegosService.deleteJuego(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar juego: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}