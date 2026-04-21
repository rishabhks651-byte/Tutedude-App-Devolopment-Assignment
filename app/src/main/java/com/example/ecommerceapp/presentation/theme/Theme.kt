package com.example.ecommerceapp.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Ink,
    secondary = Coral,
    tertiary = Slate,
    background = Snow,
    surface = Snow
)

private val DarkColors = darkColorScheme(
    primary = Mist,
    secondary = Coral,
    tertiary = Sand
)

@Composable
fun ECommerceTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}
