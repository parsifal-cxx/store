package com.example.store.ui.verification

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/** Экран ввода OTP-кода. Дата: 03.03.2026, Автор: Бубнов Никита */
@Composable
fun VerificationScreen(
    email: String,
    onBack: () -> Unit,
    onNavigateToCreateNewPassword: () -> Unit
) {
    Text("VerificationScreen: $email")
}