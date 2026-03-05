package com.example.store.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** DTO товара. Дата: 05.03.2026, Автор: Бубнов Никита */
@Serializable
data class ProductDto(
    val id: String,
    val title: String,
    @SerialName("category_id") val categoryId: String? = null,
    @SerialName("cost") val cost: Double,
    val description: String,
    @SerialName("is_best_seller") val isBestSeller: Boolean? = false
)