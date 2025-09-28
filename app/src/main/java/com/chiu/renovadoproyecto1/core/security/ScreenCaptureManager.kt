package com.chiu.renovadoproyecto1.core.security

import android.app.Activity
import android.view.WindowManager

object ScreenCaptureManager {

    fun blockScreenCapture(activity: Activity) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    fun allowScreenCapture(activity: Activity) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}