package com.chiu.renovadoproyecto1.core.di

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.chiu.renovadoproyecto1.core.hardware.di.HardwareModule
import com.chiu.renovadoproyecto1.core.hardware.domain.Camera.CameraManager
import com.chiu.renovadoproyecto1.core.hardware.domain.Camera.CapturePhotoUseCase

object CameraModule {

    fun provideCameraManager(
        context: Context,
        activity: FragmentActivity
    ): CameraManager {
        return HardwareModule.provideCameraManager(context, activity)
    }

    fun provideCapturePhotoUseCase(
        context: Context,
        activity: FragmentActivity
    ): CapturePhotoUseCase {
        return HardwareModule.getCapturePhotoUseCase(context, activity)
    }
}