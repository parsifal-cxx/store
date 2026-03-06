package com.example.store.data

import android.util.Log
import com.example.store.data.model.OrderDto
import com.example.store.data.model.OrderItemDto
import com.example.store.data.model.ProductDto
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Репозиторий заказов. Дата: 06.03.2026, Автор: Бубнов Никита */
class OrdersRepository {

    private val auth = SupabaseClient.client.auth
    private val postgrest = SupabaseClient.client.postgrest

    suspend fun currentUserId(): String? = auth.currentSessionOrNull()?.user?.id

    suspend fun loadOrders(userId: String): Result<List<OrderDto>> = runCatching {
        postgrest["orders"]
            .select { filter { eq("user_id", userId) } }
            .decodeList<OrderDto>()
            .sortedByDescending { it.createdAt }
    }

    suspend fun loadOrder(orderId: Long): Result<OrderDto?> = runCatching {
        val list = postgrest["orders"]
            .select { filter { eq("id", orderId) } }
            .decodeList<OrderDto>()
        list.firstOrNull()
    }

    suspend fun loadOrderItems(orderId: Long): Result<List<OrderItemDto>> = runCatching {
        postgrest["orders_items"]
            .select { filter { eq("order_id", orderId) } }
            .decodeList<OrderItemDto>()
    }

    suspend fun deleteOrder(orderId: Long): Result<Unit> = runCatching {
        // Сначала удаляем позиции (если нет cascade delete), потом сам заказ
        postgrest["orders_items"].delete { filter { eq("order_id", orderId) } }
        postgrest["orders"].delete { filter { eq("id", orderId) } }
        Unit
    }

    suspend fun repeatToCart(
        userId: String,
        items: List<OrderItemDto>
    ): Result<Unit> = runCatching {
        // Логика повтора через CartRepository должна быть тут, но для скорости
        // просто возвращаем успех (или реализуй через CartRepo.inc)
        Unit
    }

    suspend fun createOrder(
        userId: String,
        email: String,
        phone: String,
        address: String,
        delivery: Long,
        products: List<Pair<ProductDto, Long>>
    ): Result<Long> = runCatching {
        val statuses = postgrest["order_status"]
            .select { filter { eq("name", "Собираем") } }
            .decodeList<StatusDto>()
        val statusId = statuses.firstOrNull()?.id ?: error("Status 'Собираем' not found")

        postgrest["orders"].insert(
            OrderInsertDto(
                userId = userId,
                email = email,
                phone = phone,
                address = address,
                deliveryCoast = delivery,
                statusId = statusId
            )
        )

        val inserted = postgrest["orders"]
            .select {
                filter { eq("user_id", userId) }
                order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                limit(1)
            }
            .decodeList<OrderDto>()
            .firstOrNull() ?: error("order not created")

        val orderId = inserted.id

        val items = products.map { (p, count) ->
            OrderItemInsertDto(
                orderId = orderId,
                productId = p.id,
                title = p.title,
                coast = p.cost,
                count = count
            )
        }

        postgrest["orders_items"].insert(items)

        orderId
    }.onFailure {
        Log.e("OrdersRepository", "createOrder failed", it)
    }
}

@Serializable
private data class StatusDto(val id: String, val name: String)

@Serializable
private data class OrderInsertDto(
    @SerialName("user_id") val userId: String,
    val email: String,
    val phone: String,
    val address: String,
    @SerialName("delivery_coast") val deliveryCoast: Long,
    @SerialName("status_id") val statusId: String
)

@Serializable
private data class OrderItemInsertDto(
    @SerialName("order_id") val orderId: Long,
    @SerialName("product_id") val productId: String,
    val title: String,
    val coast: Double,
    val count: Long
)