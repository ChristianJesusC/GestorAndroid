package com.chiu.renovadoproyecto1.core.camera.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.chiu.renovadoproyecto1.core.hardware.domain.Camera.CameraManager

class CameraManagerImpl(
    private val context: Context,
    private val activity: FragmentActivity
) : CameraManager {

    private var permissionLauncher: ActivityResultLauncher<String>? = null
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var permissionCallback: ((Boolean) -> Unit)? = null
    private var captureCallback: Pair<(Bitmap) -> Unit, (String) -> Unit>? = null

    fun initialize() {
        permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            permissionCallback?.invoke(isGranted)
        }

        cameraLauncher = activity.registerForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->
            captureCallback?.let { (onSuccess, onError) ->
                if (bitmap != null) {
                    onSuccess(bitmap)
                } else {
                    onError("No se pudo capturar la imagen")
                }
            }
        }
    }

    override fun authenticate(onSuccess: () -> Unit, onError: (String) -> Unit) {
    }

    override fun isAvailable(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    override fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestPermission(onResult: (Boolean) -> Unit) {
        permissionCallback = onResult
        permissionLauncher?.launch(Manifest.permission.CAMERA)
    }

    override fun capturePhoto(onSuccess: (Bitmap) -> Unit, onError: (String) -> Unit) {
        captureCallback = Pair(onSuccess, onError)
        cameraLauncher?.launch(null)
    }
}