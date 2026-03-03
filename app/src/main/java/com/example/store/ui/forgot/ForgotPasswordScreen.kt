package com.example.store.ui.forgot

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/** Экран восстановления пароля. Дата: 03.03.2026, Автор: Бубнов Никита */
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onNavigateToVerification: (email: String) -> Unit
) {
    Text("ForgotPasswordScreen")
}