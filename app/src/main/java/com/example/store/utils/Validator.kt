package com.example.store.utils

/**
 * Утилиты для валидации данных
 * Дата создания: 03.03.2026
 * Автор: Бубнов Никита
 */
object Validator {

    // name@domenname.ru, name и domenname: [a-z0-9]+, tld: только буквы, длина > 2
    private val EMAIL_PATTERN = "^[a-z0-9]+@[a-z0-9]+\\.[a-z]{3,}$".toRegex()

    fun isValidEmail(email: String): Boolean = email.matches(EMAIL_PATTERN)
    fun isNotBlank(value: String): Boolean = value.isNotBlank()

    fun isNotEmpty(text: String): Boolean {
        return text.isNotBlank()
    }

    fun passwordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword && password.isNotBlank()
    }
}