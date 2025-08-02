package com.chiu.renovadoproyecto1

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
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
import com.google.firebase.FirebaseApp

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

    private val notificationPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Log.d("MainActivity", "📋 Notification permission result: $isGranted")
            if (isGranted) {
                Log.d("MainActivity", "✅ Permisos de notificación concedidos")
            } else {
                Log.w("MainActivity", "⚠️ Permisos de notificación denegados")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "Iniciando aplicación")

        initializeFirebase()

        AppContextHolder.init(this)
        HardwareModule.setupLaunchers(permissionLauncher, cameraLauncher)

        requestNotificationPermission()

        enableEdgeToEdge()
        setContent {
            RenovadoProyecto1Theme {
                NavigationWrapper()
            }
        }
    }

    private fun initializeFirebase() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("MainActivity", "🔥 Firebase inicializado correctamente")
            } else {
                Log.d("MainActivity", "🔥 Firebase ya estaba inicializado")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "❌ Error inicializando Firebase: ${e.message}")
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "📋 Solicitando permisos de notificación...")
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Log.d("MainActivity", "✅ Permisos de notificación ya concedidos")
            }
        } else {
            Log.d("MainActivity", "ℹ️ Android < 13, no se requieren permisos de notificación")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        HardwareModule.clearCameraManager()
    }
}