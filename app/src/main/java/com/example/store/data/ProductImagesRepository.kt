package com.example.store.data

import com.example.store.utils.StorageUrl
import io.github.jan.supabase.storage.storage

/** Репозиторий фото товаров в Storage. Дата: 05.03.2026, Автор: Бубнов Никита */
class ProductImagesRepository {

    private val storage = SupabaseClient.client.storage

    suspend fun getPreviewUrlsForProducts(productIds: List<String>): Result<Map<String, String>> = runCatching {
        val idSet = productIds.toSet()
        val objects = storage.from("products").list()

        val previews = LinkedHashMap<String, String>()

        for (obj in objects) {
            val name = obj.name
            val productId = extractProductId(name) ?: continue
            if (productId !in idSet) continue
            if (previews.containsKey(productId)) continue

            previews[productId] = StorageUrl.publicObject(bucket = "products", fileName = name)
        }

        previews
    }

    suspend fun getVariantUrls(productId: String): Result<List<String>> = runCatching {
        val prefix = "$productId-"
        val objects = storage.from("products").list()
        objects
            .map { it.name }
            .filter { it.startsWith(prefix) }
            .sorted()
            .map { com.example.store.utils.StorageUrl.publicObject("products", it) }
    }

    private fun extractProductId(fileName: String): String? {
        val base = fileName.substringBeforeLast('.', missingDelimiterValue = "")
        if (base.isBlank()) return null
        val productId = base.substringBeforeLast('-', missingDelimiterValue = "")
        return productId.ifBlank { null }
    }
}