package com.example.wear.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import androidx.wear.compose.material.ToggleChipDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue



private val azulRey = Color(0xFF007AFF)
private val amarillo = Color(0xFFFFD700)

@Composable
fun TallerNotificationScreens(
    scalingLazyListState: ScalingLazyListState,
    focusRequester: FocusRequester
) {
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
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
        // se desplaza como parte del scroll el encabezado
        item {
            HeaderNotificaciones()
        }

        item {
            PillCard(
                title = "Cita aceptada",
                subtitle = "Para el 28/05/25 3:30 PM",
                background = azulRey
            )
        }

        item {
            PillCard(
                title = "Cita pendiente",
                subtitle = "Su cita aun no esta confirmada\n para 25/06/25",
                background = azulRey
            )
        }


        item {
            PillCard(
                title = "Cita cancelada",
                subtitle = "Se cancelo su cita del\ndia: 29/05/25",
                background = azulRey
            )
        }

        item {
            PillCard(
                title = "Vehículo listo para entrega",
                subtitle = "Confirmar hora de recogida\nTel: 7712342721",
                background = azulRey
            )
        }

        // btn Recibir alertas
        item {
            var isAlertOn by remember { mutableStateOf(true) } //
            ToggleChip(
                checked = isAlertOn,
                onCheckedChange = { isAlertOn = it },
                label = {
                    Text("Recibir alertas", color = Color.Black)
                },
                toggleControl = {
                    Switch(checked = isAlertOn)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )
        }

// btn Modo silencioso
        item {
            var isSilentModeOn by remember { mutableStateOf(false) }
            ToggleChip(
                checked = isSilentModeOn,
                onCheckedChange = { isSilentModeOn = it },
                label = {
                    Text("Modo silencioso", color = Color.Black)
                },
                toggleControl = {
                    Switch(checked = isSilentModeOn)
                },

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
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.title3,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.body2,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun HeaderNotificaciones() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            text = "Sistema de notificaciones\ntaller heber",
            style = MaterialTheme.typography.body1,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )
    }
}
