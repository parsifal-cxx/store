package com.example.store.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/** Тема приложения. Дата: 03.03.2026, Автор: Бубнов Никита */
private val LightColorScheme = lightColorScheme()

@Composable
fun StoreTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = StoreTypography,
        content = content
    )
}