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
import androidx.lifecycle.lifecycleScope
import com.chiu.renovadoproyecto1.core.appcontext.AppContextHolder
import com.chiu.renovadoproyecto1.core.hardware.di.HardwareModule
import com.chiu.renovadoproyecto1.core.navigation.NavigationWrapper
import com.chiu.renovadoproyecto1.core.security.ScreenCaptureManager
import com.chiu.renovadoproyecto1.core.security.SecureScreenManager
import com.chiu.renovadoproyecto1.ui.theme.RenovadoProyecto1Theme
import kotlinx.coroutines.launch

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

        // ✅ Observar cambios en el modo seguro
        lifecycleScope.launch {
            SecureScreenManager.isSecureScreen.collect { isSecure ->
                if (isSecure) {
                    ScreenCaptureManager.blockScreenCapture(this@MainActivity)
                    Log.d("MainActivity", "🔒 Capturas BLOQUEADAS")
                } else {
                    ScreenCaptureManager.allowScreenCapture(this@MainActivity)
                    Log.d("MainActivity", "🔓 Capturas PERMITIDAS")
                }
            }
        }

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
        SecureScreenManager.reset()
    }
}