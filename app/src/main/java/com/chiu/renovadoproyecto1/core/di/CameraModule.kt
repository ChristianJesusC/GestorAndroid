package com.chiu.renovadoproyecto1.core.di

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.chiu.renovadoproyecto1.core.camera.data.CameraManagerImpl
import com.chiu.renovadoproyecto1.core.hardware.domain.Camera.CameraManager
import com.chiu.renovadoproyecto1.core.hardware.domain.Camera.CapturePhotoUseCase

object CameraModule {

    fun provideCameraManager(
        context: Context,
        activity: FragmentActivity
    ): CameraManager {
        val cameraManager = CameraManagerImpl(context, activity)
        cameraManager.initialize()
        return cameraManager
    }

    fun provideCapturePhotoUseCase(
        context: Context,
        activity: FragmentActivity
    ): CapturePhotoUseCase {
        val cameraManager = provideCameraManager(context, activity)
        return CapturePhotoUseCase(cameraManager)
    }
}