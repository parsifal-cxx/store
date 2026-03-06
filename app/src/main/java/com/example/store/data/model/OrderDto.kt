package com.example.store.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** DTO заказа. Дата: 06.03.2026, Автор: Бубнов Никита */
@Serializable
data class OrderDto(
    val id: Long,
    @SerialName("created_at") val createdAt: String,
    @SerialName("user_id") val userId: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    @SerialName("delivery_coast") val deliveryCoast: Long? = null,
    @SerialName("status_id") val statusId: String? = null,
    @SerialName("payment_id") val paymentId: String? = null
)