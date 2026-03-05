package com.example.store.utils

import com.example.store.data.SupabaseClient

/** Построение public URL для Storage. Дата: 05.03.2026, Автор: Бубнов Никита */
object StorageUrl {
    fun publicObject(bucket: String, fileName: String): String {
        return "${SupabaseClient.SUPABASE_URL}/storage/v1/object/public/$bucket/$fileName"
    }
}