package com.chiu.renovadoproyecto1.core.hardware.domain.Camera

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

class CapturePhotoUseCase(
    private val cameraManager: CameraManager  // ✅ Usa CameraManager en lugar de FragmentActivity
) {

    fun isAvailable(): Boolean {
        return cameraManager.isAvailable()
    }

    fun hasPermission(): Boolean {
        return cameraManager.hasPermission()
    }

    fun requestPermission(onResult: (Boolean) -> Unit) {
        cameraManager.requestPermission(onResult)
    }

    fun capturePhoto(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        cameraManager.capturePhoto(
            onSuccess = { bitmap ->
                try {
                    val base64 = bitmapToBase64(bitmap)
                    onSuccess(base64)
                } catch (e: Exception) {
                    onError("Error convirtiendo imagen: ${e.message}")
                }
            },
            onError = onError
        )
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}