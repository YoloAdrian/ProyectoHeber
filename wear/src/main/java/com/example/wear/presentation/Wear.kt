/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.rememberScalingLazyListState
import com.example.wear.presentation.theme.ProyectoHeberTheme

class Wear : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContent { WearApp() }
    }
}

@Composable
fun WearApp() {
    ProyectoHeberTheme {
        // Estado de scroll y FocusRequester compartido
        val listState = rememberScalingLazyListState()
        val focusRequester = remember { FocusRequester() }

        Scaffold(
            timeText = { TimeText() },
            positionIndicator = { PositionIndicator(listState) }
        ) {
            TallerNotificationScreens(
                scalingLazyListState = listState,
                focusRequester = focusRequester
            )
        }
        }
    }
