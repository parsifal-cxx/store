package com.example.store.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** DTO позиции заказа. Дата: 06.03.2026, Автор: Бубнов Никита */
@Serializable
data class OrderItemDto(
    val id: String,
    @SerialName("order_id") val orderId: Long? = null,
    @SerialName("product_id") val productId: String? = null,
    val title: String? = null,
    val coast: Double? = null,
    val count: Long? = null
)