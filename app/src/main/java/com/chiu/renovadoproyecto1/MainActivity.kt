package com.chiu.renovadoproyecto1

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.chiu.renovadoproyecto1.core.appcontext.AppContextHolder
import com.chiu.renovadoproyecto1.core.hardware.di.HardwareModule
import com.chiu.renovadoproyecto1.core.navigation.NavigationWrapper
import com.chiu.renovadoproyecto1.ui.theme.RenovadoProyecto1Theme

class MainActivity : FragmentActivity() {

    private val permissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Log.d("MainActivity", "📋 Camera permission result: $isGranted")
            HardwareModule.handlePermissionResult(isGranted)
        }

    private val cameraLauncher: ActivityResultLauncher<Void?> =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            Log.d("MainActivity", "📸 Camera result: ${bitmap != null}")
            HardwareModule.handleCameraResult(bitmap)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "Iniciando aplicación")
        AppContextHolder.init(this)

        HardwareModule.setupLaunchers(permissionLauncher, cameraLauncher)

        enableEdgeToEdge()
        setContent {
            RenovadoProyecto1Theme {
                NavigationWrapper()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        HardwareModule.clearCameraManager()
    }
}