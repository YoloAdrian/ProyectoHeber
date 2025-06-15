/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.wear.presentation

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.wear.presentation.TallerNotificationScreens
import com.example.wear.presentation.theme.ProyectoHeberTheme
import com.google.firebase.messaging.FirebaseMessaging
import androidx.compose.ui.focus.FocusRequester
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.rememberScalingLazyListState
import android.content.pm.PackageManager

class Wear : ComponentActivity() {
    private val TAG = "FCM_TOKEN"
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Log.d(TAG, "✅ Permiso de notificación concedido.")
            obtenerTokenFCM()
        } else {
            Log.w(TAG, "⚠️ Permiso de notificación denegado.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> obtenerTokenFCM()
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) ->
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                else -> requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            obtenerTokenFCM()
        }
        setContent {
            val listState = rememberScalingLazyListState()
            // Este es tu repo que te da List<ParcialMensaje>
            val parcial = NotificationRepository.mensajes.collectAsState().value

            // Lo mapeas a List<MensajeNotificacion>
            val mensajes: List<MensajeNotificacion> = parcial.map {
                MensajeNotificacion(titulo = it.title, subtitulo = it.subtitle)
            }

            ProyectoHeberTheme {
                Scaffold(
                    timeText = { TimeText() },
                    positionIndicator = { PositionIndicator(listState) }
                ) {
                    TallerNotificationScreens(
                        scalingLazyListState = listState,
                        focusRequester = FocusRequester(),
                        mensajes = mensajes
                    )
                }
            }
        }
    }

    private fun obtenerTokenFCM() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "❌ Error obteniendo token FCM", task.exception)
                return@addOnCompleteListener
            }
            Log.d(TAG, "✅ Token FCM: ${task.result}")
        }
    }
}