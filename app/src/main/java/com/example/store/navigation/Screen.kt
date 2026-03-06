package com.example.store.navigation

import android.net.Uri

/** Маршруты навигации. Дата: 06.03.2026, Автор: Бубнов Никита */
sealed class Screen(val route: String) {
    data object Onboard : Screen("onboard")
    data object SignIn : Screen("sign_in")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")
    data object CreateNewPassword : Screen("create_new_password")

    data object Verification : Screen("verification?email={email}") {
        fun createRoute(email: String): String = "verification?email=${Uri.encode(email)}"
    }

    data object HomeRoot : Screen("home_root")

    data object Details : Screen("details/{productId}") {
        fun createRoute(productId: String) = "details/$productId"
    }

    data object Cart : Screen("cart")
    data object Checkout : Screen("checkout")
    data object OrderDetail : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: Long) = "order_detail/$orderId"
    }
}