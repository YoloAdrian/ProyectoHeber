package com.example.wear.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.ToggleChip
import com.example.wear.presentation.MensajeNotificacion
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val azulRey = Color(0xFF007AFF)
private val amarillo = Color(0xFFFFD700)

@Composable
fun TallerNotificationScreens(
    scalingLazyListState: ScalingLazyListState,
    focusRequester: FocusRequester,
    mensajes: List<MensajeNotificacion>
) {
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focusRequester)
            .onRotaryScrollEvent {
                scalingLazyListState.dispatchRawDelta(it.verticalScrollPixels)
                true
            }
            .focusable(),
        state = scalingLazyListState,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { HeaderNotificaciones() }
        mensajes.forEach { mensaje ->
            item {
                PillCard(
                    title = mensaje.titulo,
                    subtitle = mensaje.subtitulo,
                    background = azulRey
                )
            }
        }
        item {
            var isAlertOn by remember { mutableStateOf(true) }
            ToggleChip(
                checked = isAlertOn,
                onCheckedChange = { isAlertOn = it },
                label = { Text("Recibir alertas", color = Color.Black) },
                toggleControl = { Switch(checked = isAlertOn) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )
        }
        item {
            var isSilentModeOn by remember { mutableStateOf(false) }
            ToggleChip(
                checked = isSilentModeOn,
                onCheckedChange = { isSilentModeOn = it },
                label = { Text("Modo silencioso", color = Color.Black) },
                toggleControl = { Switch(checked = isSilentModeOn) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )
        }
    }
}

@Composable
private fun PillCard(
    title: String,
    subtitle: String,
    background: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp),
        color = background,
        shape = RoundedCornerShape(32.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = androidx.compose.material.MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = androidx.compose.material.MaterialTheme.typography.body2,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun HeaderNotificaciones() {
    // Formatear hora y fecha actual
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val todayDate = LocalDate.now().format(dateFormatter)
    val currentTime = LocalTime.now().format(timeFormatter)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hora arriba (destacada)
        Text(
            text = currentTime,
            style = androidx.compose.material.MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        // Fecha debajo (destacada)
        Text(
            text = todayDate,
            style = androidx.compose.material.MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color = azulRey, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null,
                tint = amarillo,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Sistema de notificaciones\nTaller Heber",
            style = androidx.compose.material.MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )
    }
}


