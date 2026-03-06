package com.example.store.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** DTO строки корзины. Дата: 06.03.2026, Автор: Бубнов Никита */
@Serializable
data class CartRowDto(
    val id: String,
    @SerialName("product_id") val productId: String? = null,
    @SerialName("user_id") val userId: String? = null,
    val count: Long? = 1
)