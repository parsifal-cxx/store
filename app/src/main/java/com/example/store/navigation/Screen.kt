package com.example.store.navigation

import android.net.Uri

/** Маршруты навигации. Дата: 03.03.2026, Автор: Бубнов Никита */
sealed class Screen(val route: String) {
    data object SignIn : Screen("sign_in")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")

    data object Verification : Screen("verification?email={email}") {
        fun createRoute(email: String): String = "verification?email=${Uri.encode(email)}"
    }

    data object CreateNewPassword : Screen("create_new_password")
}