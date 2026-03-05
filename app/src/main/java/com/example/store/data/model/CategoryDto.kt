package com.example.store.data.model

import kotlinx.serialization.Serializable

/** DTO категории. Дата: 05.03.2026, Автор: Бубнов Никита */
@Serializable
data class CategoryDto(
    val id: String,
    val title: String
)