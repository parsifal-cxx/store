package com.example.store.data

import com.example.store.data.model.CategoryDto
import com.example.store.data.model.ProductDto
import io.github.jan.supabase.postgrest.postgrest

/** Репозиторий товаров/категорий. Дата: 05.03.2026, Автор: Бубнов Никита */
class ProductsRepository {

    private val postgrest = SupabaseClient.client.postgrest

    suspend fun getCategories(): Result<List<CategoryDto>> = runCatching {
        postgrest["categories"]
            .select()
            .decodeList<CategoryDto>()
    }

    suspend fun getProducts(categoryId: String? = null): Result<List<ProductDto>> = runCatching {
        postgrest["products"]
            .select {
                if (categoryId != null) {
                    filter { eq("category_id", categoryId) }
                }
            }
            .decodeList<ProductDto>()
    }

    suspend fun getBestSellers(categoryId: String? = null): Result<List<ProductDto>> = runCatching {
        postgrest["products"]
            .select {
                filter { eq("is_best_seller", true) }
                if (categoryId != null) {
                    filter { eq("category_id", categoryId) }
                }
            }
            .decodeList<ProductDto>()
    }

    suspend fun getProductById(id: String): Result<ProductDto?> = runCatching {
        val list = postgrest["products"]
            .select {
                filter { eq("id", id) }
            }
            .decodeList<ProductDto>()

        list.firstOrNull()
    }

    suspend fun getProductsByIds(ids: List<String>): Result<List<ProductDto>> = runCatching {
        if (ids.isEmpty()) return@runCatching emptyList<ProductDto>()

        val all = postgrest["products"]
            .select()
            .decodeList<ProductDto>()

        val idSet = ids.toHashSet()
        all.filter { it.id in idSet }
    }
}