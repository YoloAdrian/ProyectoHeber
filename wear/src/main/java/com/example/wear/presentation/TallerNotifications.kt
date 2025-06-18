package com.example.wear.presentation


import android.content.Context
//import android.widget.Button
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.CircularProgressIndicator
// hasta aqui
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.CircularProgressIndicator
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.roundToInt

private val azulRey = Color(0xFF007AFF)
private val amarillo = Color(0xFFFFD700)

@Composable
fun TallerNotificationScreens(
    scalingLazyListState: ScalingLazyListState,
    focusRequester: FocusRequester,
    mensajes: List<ParcialMensaje>,
    receiveAlerts: Boolean,
    onReceiveAlertsChange: (Boolean) -> Unit,
    fcmToken: String?,
    onUnlink: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isUnlinking by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

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
                DismissiblePillCard(
                    mensaje = mensaje,
                    background = azulRey,
                    onDismiss = { NotificationRepository.removeMensaje(context, mensaje) }
                )
            }
        }
        item {
            Button(
                enabled = !isUnlinking,
                onClick = {
                    if (fcmToken.isNullOrEmpty()) {
                        Toast.makeText(context, "Token FCM no disponible", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isUnlinking = true
                    coroutineScope.launch {
                        try {
                            val response = ApiClient.wearApi.unlinkSmartwatch(
                                UnlinkSmartwatchRequest(fcmToken = fcmToken)
                            )
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Desvinculado correctamente", Toast.LENGTH_SHORT).show()
                                NotificationRepository.clearAllMensajes(context)
                                onUnlink()
                            } else {
                                val err = response.errorBody()?.string() ?: "Error desconocido"
                                Toast.makeText(context, "Error al desvincular: $err", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Excepción: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            isUnlinking = false
                        }
                    }
                },
                colors = androidx.wear.compose.material.ButtonDefaults.buttonColors(
                    backgroundColor = Color.Red,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (isUnlinking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Desvinculando...")
                } else {
                    Text("Desvincular")
                }
            }
        }
    }
}
@Composable
fun DismissiblePillCard(
    mensaje: ParcialMensaje,
    background: Color,
    onDismiss: () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    var widthPx by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { widthPx = it.width }
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .pointerInput(mensaje) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = offsetX.value + dragAmount.x
                        scope.launch { offsetX.snapTo(newOffset) }
                    },
                    onDragEnd = {
                        if (abs(offsetX.value) > widthPx / 2f) {
                            val target = if (offsetX.value > 0) widthPx.toFloat() else -widthPx.toFloat()
                            scope.launch {
                                offsetX.animateTo(target, tween(durationMillis = 200))
                                onDismiss()
                            }
                        } else {
                            scope.launch { offsetX.animateTo(0f, tween(durationMillis = 200)) }
                        }
                    }
                )
            }
    ) {
        PillCardContent(
            title = mensaje.title,
            subtitle = mensaje.subtitle,
            background = background
        )
    }
}
@Composable
private fun PillCardContent(
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
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.title3,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.body2,
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
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val todayDate = LocalDate.now().format(dateFormatter)
    val currentTime = LocalTime.now().format(timeFormatter)

    Column(
        modifier = Modifier.fillMaxWidth().background(Color.Black).padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = currentTime,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = todayDate,
            style = MaterialTheme.typography.body1,
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
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )
    }
}
