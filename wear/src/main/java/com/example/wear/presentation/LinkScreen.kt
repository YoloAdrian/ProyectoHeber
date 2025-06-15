package com.example.wear.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.focusable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import android.widget.Toast
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.wear.presentation.ApiClient
import com.example.wear.presentation.LinkSmartwatchRequest
import kotlinx.coroutines.withContext


@Composable
fun LinkScreen(
    fcmToken: String?,
    onLinkedSuccess: () -> Unit,
    focusRequester: FocusRequester,
    scalingLazyListState: ScalingLazyListState
) {
    var code by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }  // ← nuevo estado
    val context = LocalContext.current

    // Cuando cambie errorMessage, esperar 3s y luego quitarlo
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            kotlinx.coroutines.delay(3000)
            errorMessage = null
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        timeText = {
            CompositionLocalProvider(LocalContentColor provides Color.White) {
                TimeText()
            }
        },
        positionIndicator = { PositionIndicator(scalingLazyListState) },
        modifier = Modifier.background(Color.Black)
    ) {
        Box {
            // Contenido principal
            ScalingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .focusRequester(focusRequester)
                    .focusable(),
                state = scalingLazyListState,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Ingresa el código de vinculación",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
                item {
                    TextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Código", color = Color.White) },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.White,
                            backgroundColor = Color(0x33FFFFFF),
                            cursorColor = Color.White,
                            focusedIndicatorColor = Color.White,
                            unfocusedIndicatorColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Button(
                        onClick = {
                            if (code.text.isBlank()) {
                                errorMessage = "Ingresa un código válido"
                                return@Button
                            }
                            if (fcmToken.isNullOrEmpty()) {
                                errorMessage = "Token FCM no listo"
                                return@Button
                            }
                            isLoading = true
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val response = ApiClient.wearApi.linkSmartwatch(
                                        LinkSmartwatchRequest(code = code.text, fcmToken = fcmToken)
                                    )
                                    if (response.isSuccessful) {
                                        withContext(Dispatchers.Main) {
                                            isLoading = false
                                            onLinkedSuccess()
                                        }
                                    } else {
                                        val err = response.errorBody()?.string() ?: "Error desconocido"
                                        withContext(Dispatchers.Main) {
                                            isLoading = false
                                            errorMessage = "Error: $err"
                                        }
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        isLoading = false
                                        errorMessage = "Excepción: ${e.message}"
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF007AFF),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Vincular")
                    }
                }
                if (isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                }
            }

            // Overlay de error, si lo hay
            errorMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = msg,
                        color = Color.White,
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color.Red.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}