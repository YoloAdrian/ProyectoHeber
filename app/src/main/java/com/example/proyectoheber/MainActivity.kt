package com.example.proyectoheber

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectoheber.ui.theme.ProyectoHeberTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.* // <--- importante para navegación
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }
        setContent {
            ProyectoHeberTheme {
                AppNavigation() // Esta línea llama a la función que definimos abajo
            }
        }
    }
}

// 👇 Aquí comienzan las funciones que debes agregar

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "telefono") {
        composable("telefono") { PantallaTelefono(navController) }
        composable("smartwatch") { PantallaSmartwatch() }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTelefono(navController: NavHostController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("App de Notificaciones") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enviar notificación al smartwatch",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(onClick = {
                enviarNotificacion(context)
            }) {
                Text("Notificar: Auto Reparado")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                enviarNotificacion(context, mensaje = "Cita programada para el 28 de mayo a las 10:00 AM")
            }) {
                Text("Notificar: Próxima cita")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSmartwatch() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Simulación Smartwatch") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Aquí simulamos un smartwatch redondo
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔔", style = MaterialTheme.typography.headlineLarge)
                    Text("Notificación", style = MaterialTheme.typography.titleMedium)
                    Text("Revisa tu teléfono", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

fun enviarNotificacion(context: Context, mensaje: String = "Tu auto ya está reparado.") {
    val channelId = "notificaciones_taller"
    val notificationId = 1

    // Verifica si el permiso está concedido (solo Android 13+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permission = android.Manifest.permission.POST_NOTIFICATIONS
        val granted = androidx.core.content.ContextCompat.checkSelfPermission(context, permission) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!granted) {
            // Si no tiene permiso, no se envía la notificación
            return
        }
    }

    // Crear canal (solo una vez)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Notificaciones del taller"
        val descriptionText = "Notificaciones de autos reparados y citas"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Crear notificación
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Notificación del Taller")
        .setContentText(mensaje)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, builder.build())
    }
}
