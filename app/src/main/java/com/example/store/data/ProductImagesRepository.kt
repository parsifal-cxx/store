package com.example.store.data

import com.example.store.utils.StorageUrl
import io.github.jan.supabase.storage.storage

/** Работа с фото товаров в Storage. Дата: 04.03.2026, Автор: Бубнов Никита */
class ProductImagesRepository {

    private val storage = SupabaseClient.client.storage

    suspend fun getVariantFileNames(productId: String): List<String> {
        val prefix = "$productId-"

        val objects = storage
            .from("products")
            .list()

        return objects
            .map { it.name }
            .filter { it.startsWith(prefix) }
            .sorted()
    }

    suspend fun getVariantUrls(productId: String): List<String> {
        return getVariantFileNames(productId).map { file ->
            StorageUrl.publicObject(bucket = "products", fileName = file)
        }
    }

    suspend fun getPreviewUrl(productId: String): String? {
        return getVariantFileNames(productId).firstOrNull()?.let { file ->
            StorageUrl.publicObject(bucket = "products", fileName = file)
        }
    }
}