package com.example.wear.presentation


import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "FCM_SERVICE"
    private val CHANNEL_ID = "notificaciones_taller"
    private val PREFS_NAME = "wear_prefs"
    private val KEY_RECEIVE_ALERTS = "receive_alerts"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "📨 Mensaje FCM recibido: ${remoteMessage.data}")

        // Asegurar inicialización del repositorio
        NotificationRepository.init(applicationContext)

        val notificationPayload = remoteMessage.notification
        val title = notificationPayload?.title ?: "Notificación del taller"
        val body = notificationPayload?.body ?: "Tienes una nueva notificación"

        // 1) Guardar siempre en repositorio
        NotificationRepository.addMensaje(applicationContext, title, body)

        // 2) Mostrar en sistema sólo si prefs “receive_alerts” está true
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val receiveAlerts = prefs.getBoolean(KEY_RECEIVE_ALERTS, true)
        if (receiveAlerts) {
            showNotification(title, body)
        } else {
            Log.d(TAG, "Recibir alertas desactivado: no muestro notificación externa")
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(title: String, body: String) {
        // Verificar permiso POST_NOTIFICATIONS en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "⚠️ Permiso de notificaciones no concedido")
            return
        }
        // Crear canal en Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notificaciones Taller",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Canal para notificaciones de cita" }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
        // Mostrar notificación
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(this)
            .notify(System.currentTimeMillis().toInt(), notification)
    }
}
