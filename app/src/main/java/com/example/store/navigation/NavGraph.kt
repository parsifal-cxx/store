package com.example.store.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.store.ui.forgot.ForgotPasswordScreen
import com.example.store.ui.newpassword.CreateNewPasswordScreen
import com.example.store.ui.onboard.OnboardScreen
import com.example.store.ui.register.RegisterScreen
import com.example.store.ui.signin.SignInScreen
import com.example.store.ui.verification.VerificationScreen

/** Граф навигации приложения. Дата: 04.03.2026, Автор: Бубнов Никита */
@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Onboard.route,
        modifier = modifier
    ) {

        composable(Screen.Onboard.route) {
            OnboardScreen(
                onFinished = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Onboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                onBack = { navController.popBackStack() },
                onNavigateToForgot = { navController.navigate(Screen.ForgotPassword.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onSignedIn = { /* будет определено позже */ }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onNavigateToVerification = { email ->
                    navController.navigate(Screen.Verification.createRoute(email))
                }
            )
        }

        composable(
            route = Screen.Verification.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType; defaultValue = "" })
        ) { entry ->
            val email = entry.arguments?.getString("email").orEmpty()
            VerificationScreen(
                email = email,
                onBack = { navController.popBackStack() },
                onNavigateToCreateNewPassword = { navController.navigate(Screen.CreateNewPassword.route) }
            )
        }

        composable(Screen.CreateNewPassword.route) {
            CreateNewPasswordScreen(
                onBack = { navController.popBackStack() },
                onDone = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }
    }
}