package com.chiu.renovadoproyecto1.core.service.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.chiu.renovadoproyecto1.MainActivity
import com.chiu.renovadoproyecto1.R
import com.chiu.renovadoproyecto1.core.service.di.ServiceModule
import com.chiu.renovadoproyecto1.core.service.domain.usecase.SyncOfflineDataUseCase
import kotlinx.coroutines.*

class SyncForegroundService : Service() {

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "SYNC_CHANNEL"
        const val ACTION_START_SYNC = "START_SYNC"
        const val ACTION_STOP_SYNC = "STOP_SYNC"

        fun startSync(context: Context) {
            val intent = Intent(context, SyncForegroundService::class.java).apply {
                action = ACTION_START_SYNC
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun stopSync(context: Context) {
            val intent = Intent(context, SyncForegroundService::class.java).apply {
                action = ACTION_STOP_SYNC
            }
            context.startService(intent)
        }
    }

    private lateinit var syncUseCase: SyncOfflineDataUseCase
    private lateinit var notificationManager: NotificationManager
    private var syncJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("SyncService", "Servicio de sincronización creado")

        syncUseCase = ServiceModule.provideSyncOfflineDataUseCase(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SYNC -> startSyncProcess()
            ACTION_STOP_SYNC -> stopSyncProcess()
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startSyncProcess() {
        Log.d("SyncService", "Iniciando proceso de sincronización")

        startForeground(NOTIFICATION_ID, createSyncingNotification())

        syncJob?.cancel()

        syncJob = CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                updateNotification("Preparando sincronización...", 0)

                syncUseCase().fold(
                    onSuccess = { result ->
                        Log.d("SyncService", "Sincronización completada: $result")

                        if (result.failureCount > 0) {
                            updateNotification(
                                "Sincronización completada con errores: ${result.successCount}/${result.totalProcessed}",
                                100
                            )
                            delay(5000)
                        } else {
                            updateNotification(
                                "Sincronización exitosa: ${result.successCount} juegos",
                                100
                            )
                            delay(3000)
                        }

                        stopSelf()
                    },
                    onFailure = { error ->
                        Log.e("SyncService", "❌ Error en sincronización: ${error.message}")
                        updateNotification("Error en sincronización: ${error.message}", -1)
                        delay(5000)
                        stopSelf()
                    }
                )
            } catch (e: Exception) {
                Log.e("SyncService", "💥 Excepción en sincronización: ${e.message}", e)
                updateNotification("Fallo crítico en sincronización", -1)
                delay(5000)
                stopSelf()
            }
        }
    }

    private fun stopSyncProcess() {
        Log.d("SyncService", "⏹️ Deteniendo proceso de sincronización")
        syncJob?.cancel()
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Sincronización de Datos",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificaciones de sincronización de datos offline"
                setSound(null, null)
                enableVibration(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createSyncingNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("GameStore - Sincronizando")
            .setContentText("Subiendo juegos offline al servidor...")
            .setSmallIcon(R.drawable.ic_sync)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setProgress(100, 0, true)
            .build()
    }

    private fun updateNotification(message: String, progress: Int) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🔄 GameStore - Sincronizando")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_sync)
            .setOngoing(progress >= 0 && progress < 100)
            .setSilent(true)
            .apply {
                when {
                    progress < 0 -> {
                        setSmallIcon(R.drawable.ic_error)
                        setContentTitle(" GameStore - Error")
                    }
                    progress == 100 -> {
                        setSmallIcon(R.drawable.ic_check)
                        setContentTitle("GameStore - Completado")
                        setOngoing(false)
                    }
                    else -> {
                        setProgress(100, progress, progress == 0)
                    }
                }
            }
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("SyncService", "🔚 Servicio de sincronización destruido")
        syncJob?.cancel()
    }
}