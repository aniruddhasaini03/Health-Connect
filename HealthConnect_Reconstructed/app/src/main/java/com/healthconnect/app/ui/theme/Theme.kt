package com.healthconnect.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val HealthBlue = Color(0xFF2563EB)
val HealthBlueDark = Color(0xFF1D4ED8)
val HealthGreen = Color(0xFF10B981)
val SoftBackground = Color(0xFFF6F8FC)
val Danger = Color(0xFFDC2626)

private val scheme = lightColorScheme(
    primary = HealthBlue,
    secondary = HealthGreen,
    background = SoftBackground,
    surface = Color.White,
    error = Danger
)

@Composable
fun HealthConnectTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme, content = content)
}
