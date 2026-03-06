package com.example.store.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json

/** Клиент Supabase. Дата: 06.03.2026, Автор: Бубнов Никита */
object SupabaseClient {

    // Укажи здесь свои реальные URL и Key
    const val SUPABASE_URL = "https://lgkzdejflnqaewjblbtc.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imxna3pkZWpmbG5xYWV3amJsYnRjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI1MTg5MzgsImV4cCI6MjA4ODA5NDkzOH0.Br_0yPetpXDDYvUhs0b-FUrCx_VoUHvVz-CKqEHQcf8"

    val client by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)

            // Глобально игнорируем неизвестные поля в JSON (чтобы не падало на created_at и т.п.)
            defaultSerializer = KotlinXSerializer(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
    }
}
