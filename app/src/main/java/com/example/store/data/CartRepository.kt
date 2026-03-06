package com.example.store.data

import com.example.store.data.model.CartRowDto
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.decodeList
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Репозиторий корзины. Дата: 06.03.2026, Автор: Бубнов Никита */
class CartRepository {

    private val auth = SupabaseClient.client.auth
    private val postgrest = SupabaseClient.client.postgrest

    suspend fun currentUserId(): String? = auth.currentSessionOrNull()?.user?.id

    suspend fun loadCart(userId: String): Result<List<CartRowDto>> = runCatching {
        postgrest["cart"]
            .select { filter { eq("user_id", userId) } }
            .decodeList<CartRowDto>()
    }

    suspend fun inc(userId: String, productId: String): Result<Unit> = runCatching {
        val rows = postgrest["cart"].select {
            filter { eq("user_id", userId); eq("product_id", productId) }
        }.decodeList<CartRowDto>()

        val row = rows.firstOrNull()
        if (row == null) {
            postgrest["cart"].insert(CartInsertDto(userId = userId, productId = productId, count = 1))
        } else {
            val newCount = (row.count ?: 1) + 1
            postgrest["cart"].update(CartUpdateDto(count = newCount)) { filter { eq("id", row.id) } }
        }
        Unit
    }

    suspend fun dec(userId: String, productId: String): Result<Unit> = runCatching {
        val rows = postgrest["cart"].select {
            filter { eq("user_id", userId); eq("product_id", productId) }
        }.decodeList<CartRowDto>()

        val row = rows.firstOrNull() ?: return@runCatching Unit
        val current = row.count ?: 1
        val newCount = (current - 1).coerceAtLeast(1)
        postgrest["cart"].update(CartUpdateDto(count = newCount)) { filter { eq("id", row.id) } }
        Unit
    }

    suspend fun deleteRow(rowId: String): Result<Unit> = runCatching {
        postgrest["cart"].delete { filter { eq("id", rowId) } }
        Unit
    }

    suspend fun clear(userId: String): Result<Unit> = runCatching {
        postgrest["cart"].delete { filter { eq("user_id", userId) } }
        Unit
    }
}

@Serializable
private data class CartInsertDto(
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,
    val count: Long
)

@Serializable
private data class CartUpdateDto(
    val count: Long
)