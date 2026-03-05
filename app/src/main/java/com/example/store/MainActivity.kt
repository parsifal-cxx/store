package com.example.store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.example.store.navigation.AppNavGraph
import com.example.store.ui.theme.StoreTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

/** Главная Activity приложения. Дата: 03.03.2026, Автор: Бубнов Никита */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            StoreTheme {
                AppNavGraph()
            }
        }
    }
}