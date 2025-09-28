package com.chiu.renovadoproyecto1.core.notifications.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.chiu.renovadoproyecto1.MainActivity
import com.chiu.renovadoproyecto1.R
import com.chiu.renovadoproyecto1.core.notifications.di.NotificationModule
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val CHANNEL_ID = "gamestore_notifications"
        const val CHANNEL_NAME = "GameStore Notificaciones"
        const val CHANNEL_DESCRIPTION = "Notificaciones de GameStore sobre juegos"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d("FCM_Service", "🔔 Firebase Messaging Service creado")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_Service", "Nuevo token FCM recibido")
        Log.d("FCM_Service", "Token: ${token.take(30)}...")

        serviceScope.launch {
            try {
                Log.d("FCM_Service", "Registrando token en servidor...")

                val registerTokenUseCase = NotificationModule.provideRegisterTokenUseCase(this@AppFirebaseMessagingService)
                val result = registerTokenUseCase(token, "android")

                result.fold(
                    onSuccess = { message ->
                        Log.d("FCM_Service", "Token registrado exitosamente: $message")
                    },
                    onFailure = { error ->
                        Log.e("FCM_Service", "Error registrando token: ${error.message}")
                        val notificationRepository = NotificationModule.provideNotificationRepository(this@AppFirebaseMessagingService)
                        notificationRepository.saveToken(token)
                        Log.d("FCM_Service", "Token guardado localmente para registro posterior")
                    }
                )
            } catch (e: Exception) {
                Log.e("FCM_Service", "Excepción registrando token: ${e.message}")
                try {
                    val notificationRepository = NotificationModule.provideNotificationRepository(this@AppFirebaseMessagingService)
                    notificationRepository.saveToken(token)
                    Log.d("FCM_Service", "Token guardado localmente como fallback")
                } catch (saveError: Exception) {
                    Log.e("FCM_Service", "Error guardando token localmente: ${saveError.message}")
                }
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCM_Service", "Mensaje FCM recibido")
        Log.d("FCM_Service", "From: ${remoteMessage.from}")
        Log.d("FCM_Service", "MessageId: ${remoteMessage.messageId}")

        val title = remoteMessage.notification?.title ?: "GameStore"
        val body = remoteMessage.notification?.body ?: "Nueva notificación"
        val data = remoteMessage.data

        Log.d("FCM_Service", "Título: $title")
        Log.d("FCM_Service", "Cuerpo: $body")
        Log.d("FCM_Service", "Datos extras: $data")

        val notificationType = data["tipo"] ?: "general"
        Log.d("FCM_Service", "Tipo de notificación: $notificationType")

        showCustomNotification(title, body, data, notificationType)
    }

    private fun showCustomNotification(
        title: String,
        body: String,
        data: Map<String, String>,
        type: String
    ) {
        try {
            Log.d("FCM_Service", "🔔 Creando notificación personalizada tipo: $type")

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

                data.forEach { (key, value) ->
                    putExtra(key, value)
                }

                when (type) {
                    "nuevo_juego" -> putExtra("action", "view_new_game")
                    "juego_actualizado" -> putExtra("action", "view_updated_game")
                    "juego_eliminado" -> putExtra("action", "refresh_games")
                    else -> putExtra("action", "view_games")
                }
            }

            val pendingIntent = PendingIntent.getActivity(
                this,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val iconResource = when (type) {
                "nuevo_juego" -> R.drawable.ic_launcher_foreground
                "juego_actualizado" -> R.drawable.ic_launcher_foreground
                "juego_eliminado" -> R.drawable.ic_launcher_foreground
                else -> R.drawable.ic_launcher_foreground
            }

            val notificationColor = when (type) {
                "nuevo_juego" -> R.color.purple_500
                "juego_actualizado" -> R.color.teal_200
                "juego_eliminado" -> R.color.purple_700
                else -> R.color.purple_500
            }

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(iconResource)
                .setColor(getColor(notificationColor))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setSound(defaultSoundUri)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)

            data["usuario_creador"]?.let { usuario ->
                notificationBuilder.setSubText("Por: $usuario")
            }
            data["usuario_editor"]?.let { usuario ->
                notificationBuilder.setSubText("Por: $usuario")
            }
            data["usuario_eliminador"]?.let { usuario ->
                notificationBuilder.setSubText("Por: $usuario")
            }

            val notification = notificationBuilder.build()

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = System.currentTimeMillis().toInt()

            notificationManager.notify(notificationId, notification)

            Log.d("FCM_Service", "✅ Notificación mostrada con ID: $notificationId")

        } catch (e: Exception) {
            Log.e("FCM_Service", "❌ Error mostrando notificación: ${e.message}", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = CHANNEL_DESCRIPTION
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 200, 500)
                    setShowBadge(true)
                    enableLights(true)
                    lightColor = getColor(R.color.purple_500)
                }

                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)

                Log.d("FCM_Service", "Canal de notificaciones '$CHANNEL_NAME' creado exitosamente")

            } catch (e: Exception) {
                Log.e("FCM_Service", "Error creando canal de notificaciones: ${e.message}")
            }
        } else {
            Log.d("FCM_Service", "ℹ️ Android < O, no se requiere crear canal de notificaciones")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("FCM_Service", "🔚 Firebase Messaging Service destruido")
    }
}