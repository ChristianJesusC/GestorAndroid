package com.chiu.renovadoproyecto1.core.hardware.domain.Camera

import android.graphics.Bitmap

interface CameraManager {
    fun authenticate(onSuccess: () -> Unit, onError: (String) -> Unit)
    fun isAvailable(): Boolean
    fun hasPermission(): Boolean
    fun requestPermission(onResult: (Boolean) -> Unit)
    fun capturePhoto(onSuccess: (Bitmap) -> Unit, onError: (String) -> Unit)
}