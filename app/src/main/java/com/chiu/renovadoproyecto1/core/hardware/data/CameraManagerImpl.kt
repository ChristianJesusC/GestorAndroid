package com.chiu.renovadoproyecto1.core.camera.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.chiu.renovadoproyecto1.core.hardware.di.HardwareModule
import com.chiu.renovadoproyecto1.core.hardware.domain.Camera.CameraManager

class CameraManagerImpl(
    private val context: Context,
    private val activity: FragmentActivity
) : CameraManager {

    override fun authenticate(onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Método no usado para cámara
    }

    override fun isAvailable(): Boolean {
        val available = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        Log.d("CameraManagerImpl", "🔍 Cámara disponible: $available")
        return available
    }

    override fun hasPermission(): Boolean {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        Log.d("CameraManagerImpl", "🔍 Permisos cámara: $hasPermission")
        return hasPermission
    }

    override fun requestPermission(onResult: (Boolean) -> Unit) {
        Log.d("CameraManagerImpl", "📋 Solicitando permisos de cámara")
        HardwareModule.requestCameraPermission(onResult)
    }

    override fun capturePhoto(onSuccess: (Bitmap) -> Unit, onError: (String) -> Unit) {
        Log.d("CameraManagerImpl", "📸 Iniciando captura de foto")

        if (!hasPermission()) {
            Log.e("CameraManagerImpl", "❌ Sin permisos de cámara")
            onError("Sin permisos de cámara")
            return
        }

        if (!isAvailable()) {
            Log.e("CameraManagerImpl", "❌ Cámara no disponible")
            onError("Cámara no disponible")
            return
        }

        try {
            Log.d("CameraManagerImpl", "🚀 Lanzando cámara...")
            HardwareModule.launchCamera(onSuccess, onError)
        } catch (e: Exception) {
            Log.e("CameraManagerImpl", "❌ Error lanzando cámara: ${e.message}")
            onError("Error abriendo cámara: ${e.message}")
        }
    }
}