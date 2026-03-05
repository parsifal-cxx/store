package com.example.store.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

/**
 * Автор - Бубнов Никита
 * Дата - 03.03.2026
 * Клиент для работы с Supabase
 */
object SupabaseClient {

    const val SUPABASE_URL = "https://lgkzdejflnqaewjblbtc.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imxna3pkZWpmbG5xYWV3amJsYnRjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI1MTg5MzgsImV4cCI6MjA4ODA5NDkzOH0.Br_0yPetpXDDYvUhs0b-FUrCx_VoUHvVz-CKqEHQcf8"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}