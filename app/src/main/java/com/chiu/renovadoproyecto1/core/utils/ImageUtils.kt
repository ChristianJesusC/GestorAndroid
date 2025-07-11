package com.chiu.renovadoproyecto1.core.utils

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

object ImageUtils {

    fun createMultipartFromImage(context: Context, imageData: String): MultipartBody.Part? {
        return try {
            Log.d("ImageUtils", "Procesando imagen: ${imageData.take(50)}...")

            when {
                // Si es base64 (desde cámara)
                imageData.startsWith("data:image") -> {
                    Log.d("ImageUtils", "Procesando imagen base64")
                    val base64Data = imageData.substringAfter("base64,")
                    val imageBytes = Base64.decode(base64Data, Base64.DEFAULT)
                    val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("logo", "camera_image.jpg", requestBody)
                }
                imageData.startsWith("content://") -> {
                    Log.d("ImageUtils", "Procesando imagen desde galería")
                    val uri = Uri.parse(imageData)
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    bytes?.let {
                        Log.d("ImageUtils", "Imagen leída: ${it.size} bytes")
                        val requestBody = it.toRequestBody("image/*".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("logo", "gallery_image.jpg", requestBody)
                    }
                }
                // Si es URL del servidor
                imageData.startsWith("http") -> {
                    Log.d("ImageUtils", "Imagen ya es URL del servidor, no se procesa")
                    null // No procesamos URLs existentes
                }
                else -> {
                    Log.w("ImageUtils", "Formato de imagen no reconocido: $imageData")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("ImageUtils", "Error procesando imagen: ${e.message}", e)
            null
        }
    }

    fun createRequestBody(value: String?) =
        (value ?: "").toRequestBody("text/plain".toMediaTypeOrNull())

    fun createRequestBody(value: Int?) =
        (value ?: 0).toString().toRequestBody("text/plain".toMediaTypeOrNull())
}