package com.example.store.data

import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Репозиторий избранного. Дата: 05.03.2026, Автор: Бубнов Никита */
class FavoritesRepository {

    private val auth = SupabaseClient.client.auth
    private val postgrest = SupabaseClient.client.postgrest

    suspend fun currentUserId(): String? = auth.currentSessionOrNull()?.user?.id

    suspend fun getFavoriteProductIds(userId: String): Result<List<String>> = runCatching {
        postgrest["favourite"]
            .select { filter { eq("user_id", userId) } }
            .decodeList<FavouriteDto>()
            .mapNotNull { it.productId }
    }

    suspend fun isFavorite(userId: String, productId: String): Result<Boolean> = runCatching {
        postgrest["favourite"]
            .select {
                filter {
                    eq("user_id", userId)
                    eq("product_id", productId)
                }
            }
            .decodeList<FavouriteDto>()
            .isNotEmpty()
    }

    suspend fun addToFavorite(userId: String, productId: String): Result<Unit> = runCatching {
        val exists = isFavorite(userId, productId).getOrNull() == true
        if (!exists) {
            postgrest["favourite"].insert(FavouriteInsertDto(userId = userId, productId = productId))
        }
        Unit
    }

    suspend fun removeFromFavorite(userId: String, productId: String): Result<Unit> = runCatching {
        postgrest["favourite"].delete {
            filter {
                eq("user_id", userId)
                eq("product_id", productId)
            }
        }
        Unit
    }
}

@Serializable
private data class FavouriteDto(
    val id: String,
    @SerialName("product_id") val productId: String? = null,
    @SerialName("user_id") val userId: String? = null
)

@Serializable
private data class FavouriteInsertDto(
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String
)