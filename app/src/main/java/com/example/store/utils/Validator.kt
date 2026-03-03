package com.example.store.utils

/**
 * Утилиты для валидации данных
 * Дата создания: 03.03.2026
 * Автор: Бубнов Никита
 */
object Validator {

    private val EMAIL_PATTERN = "^[a-z0-9]+@[a-z0-9]+\\.[a-z]{3,}$".toRegex()

    fun isValidEmail(email: String): Boolean {
        return email.matches(EMAIL_PATTERN)
    }

    fun isNotEmpty(text: String): Boolean {
        return text.isNotBlank()
    }

    fun passwordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword && password.isNotBlank()
    }
}