package com.chiu.renovadoproyecto1.core.hardware.domain.Camera

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream

class CapturePhotoUseCase(
    private val cameraManager: CameraManager
) {
    fun isAvailable(): Boolean = cameraManager.isAvailable()

    fun hasPermission(): Boolean = cameraManager.hasPermission()

    fun requestPermission(onResult: (Boolean) -> Unit) {
        cameraManager.requestPermission(onResult)
    }

    fun capturePhoto(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!cameraManager.isAvailable()) {
            onError("Cámara no disponible en este dispositivo")
            return
        }

        if (!cameraManager.hasPermission()) {
            onError("Permisos de cámara no concedidos")
            return
        }

        cameraManager.capturePhoto(
            onSuccess = { bitmap ->
                try {
                    val base64String = bitmapToBase64(bitmap)
                    Log.d("CapturePhotoUseCase", "Foto capturada y convertida a Base64")
                    onSuccess(base64String)
                } catch (e: Exception) {
                    Log.e("CapturePhotoUseCase", "Error convirtiendo imagen: ${e.message}")
                    onError("Error procesando la imagen: ${e.message}")
                }
            },
            onError = { error ->
                Log.e("CapturePhotoUseCase", "Error capturando foto: $error")
                onError(error)
            }
        )
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}