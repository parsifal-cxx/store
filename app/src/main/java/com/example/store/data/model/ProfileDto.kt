package com.example.store.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** DTO профиля. Дата: 05.03.2026, Автор: Бубнов Никита */
@Serializable
data class ProfileDto(
    val id: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("user_id") val userId: String,
    val photo: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val address: String? = null,
    val phone: String? = null
)