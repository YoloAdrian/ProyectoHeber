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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.core.content.ContextCompat
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.wear.compose.material.rememberScalingLazyListState
import com.example.wear.presentation.LinkScreen
import com.example.wear.presentation.NotificationRepository
import com.example.wear.presentation.ParcialMensaje
import com.example.wear.presentation.TallerNotificationScreens
import com.example.wear.presentation.theme.ProyectoHeberTheme
import com.google.firebase.messaging.FirebaseMessaging
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

    // Estado para token FCM
    private val fcmTokenState = mutableStateOf<String?>(null)
    private lateinit var prefsEditor: android.content.SharedPreferences.Editor

    companion object {
        private const val KEY_LINKED = "is_linked"
        private const val KEY_RECEIVE_ALERTS = "receive_alerts"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializar repositorio para que cargue notificaciones guardadas
        NotificationRepository.init(this)

        val prefsName = "wear_prefs"
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        prefsEditor = prefs.edit()
        val initiallyLinked = prefs.getBoolean(KEY_LINKED, false)
        val initiallyReceiveAlerts = prefs.getBoolean(KEY_RECEIVE_ALERTS, true)

        // Pedir permiso de notificaciones y obtener token FCM
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    obtenerTokenFCM()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) ->
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                else ->
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            obtenerTokenFCM()
        }

        setContent {
            ProyectoHeberTheme {
                var isLinkedState by rememberSaveable { mutableStateOf(initiallyLinked) }
                var isReceiveAlerts by rememberSaveable { mutableStateOf(initiallyReceiveAlerts) }
                val focusRequester = remember { FocusRequester() }
                val listState = rememberScalingLazyListState()
                val mensajes by NotificationRepository.mensajes.collectAsState()
                val fcmToken by remember { fcmTokenState }  // obtiene el token

                if (isLinkedState) {
                    TallerNotificationScreens(
                        scalingLazyListState = listState,
                        focusRequester = focusRequester,
                        mensajes = mensajes,
                        receiveAlerts = isReceiveAlerts,
                        onReceiveAlertsChange = { new ->
                            isReceiveAlerts = new
                            prefsEditor.putBoolean(KEY_RECEIVE_ALERTS, new).apply()
                        },
                        fcmToken = fcmToken,
                        onUnlink = {
                            prefsEditor.putBoolean(KEY_LINKED, false).apply()
                            isLinkedState = false
                        }
                    )
                } else {
                    LinkScreen(
                        fcmToken = fcmToken,
                        onLinkedSuccess = {
                            prefsEditor.putBoolean(KEY_LINKED, true).apply()
                            isLinkedState = true
                        },
                        focusRequester = focusRequester,
                        scalingLazyListState = listState
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
            val token = task.result
            Log.d(TAG, "✅ Token FCM: $token")
            fcmTokenState.value = token
        }
    }
}