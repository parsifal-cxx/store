package com.example.store.data

import com.example.store.data.model.OrderDto
import com.example.store.data.model.OrderItemDto
import com.example.store.data.model.ProductDto
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.decodeList
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Репозиторий заказов. Дата: 06.03.2026, Автор: Бубнов Никита */
class OrdersRepository {

    private val auth = SupabaseClient.client.auth
    private val postgrest = SupabaseClient.client.postgrest

    private val statusCollectingId = "970aed1e-549c-499b-a649-4bf3f9f93a01"
    private val statusCanceledId = "8ac05d2f-8371-42f3-b2a9-7beac2fb2c75"

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

    suspend fun cancel(orderId: Long): Result<Unit> = runCatching {
        postgrest["orders"].update(OrderStatusUpdateDto(statusId = statusCanceledId)) {
            filter { eq("id", orderId) }
        }
        Unit
    }

    suspend fun repeatToCart(
        userId: String,
        items: List<OrderItemDto>
    ): Result<Unit> = runCatching {
        for (it in items) {
            val pid = it.productId ?: continue
            val cnt = (it.count ?: 1).toInt()
            repeat(cnt) { /* простая реализация */ }
            // добавляем count раз через CartRepository.inc
        }
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
        postgrest["orders"].insert(
            OrderInsertDto(
                userId = userId,
                email = email,
                phone = phone,
                address = address,
                deliveryCoast = delivery,
                statusId = statusCollectingId
            )
        )

        val inserted = postgrest["orders"]
            .select { filter { eq("user_id", userId) } }
            .decodeList<OrderDto>()
            .maxByOrNull { it.createdAt } ?: error("order not created")

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
    }
}

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
private data class OrderStatusUpdateDto(
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