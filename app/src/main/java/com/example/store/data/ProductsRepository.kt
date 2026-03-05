package com.example.store.data

import com.example.store.data.model.CategoryDto
import com.example.store.data.model.ProductDto
import io.github.jan.supabase.postgrest.postgrest

/** Репозиторий товаров/категорий. Дата: 04.03.2026, Автор: Бубнов Никита */
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
}