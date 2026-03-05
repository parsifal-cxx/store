package com.example.store.data

import android.graphics.Bitmap
import android.util.Log
import com.example.store.data.model.ProfileDto
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.Serializable
import java.io.ByteArrayOutputStream

/** Репозиторий профиля. Дата: 05.03.2026, Автор: Бубнов Никита */
class ProfileRepository {

    private val auth = SupabaseClient.client.auth
    private val postgrest = SupabaseClient.client.postgrest
    private val storage = SupabaseClient.client.storage

    suspend fun currentUserId(): String? = auth.currentSessionOrNull()?.user?.id

    suspend fun loadProfile(userId: String): Result<ProfileDto?> = runCatching {
        val list = postgrest["profiles"]
            .select { filter { eq("user_id", userId) } }
            .decodeList<ProfileDto>()

        list.maxByOrNull { it.createdAt }
    }.onFailure {
        Log.e("ProfileRepository", "loadProfile failed", it)
    }

    suspend fun saveProfile(
        userId: String,
        firstname: String?,
        lastname: String?,
        phone: String?,
        address: String?,
        photoUrl: String?
    ): Result<Unit> = runCatching {
        val existing = loadProfile(userId).getOrNull()

        if (existing == null) {
            postgrest["profiles"].insert(
                ProfileInsertDto(
                    user_id = userId,
                    photo = photoUrl,
                    firstname = firstname,
                    lastname = lastname,
                    address = address,
                    phone = phone
                )
            )
        } else {
            postgrest["profiles"].update(
                ProfileUpdateDto(
                    photo = photoUrl,
                    firstname = firstname,
                    lastname = lastname,
                    address = address,
                    phone = phone
                )
            ) {
                filter { eq("id", existing.id) }
            }
        }

        Unit
    }.onFailure {
        Log.e("ProfileRepository", "saveProfile failed", it)
    }

    suspend fun uploadAvatar(userId: String, bitmap: Bitmap): Result<String> = runCatching {
        val bytes = bitmap.toPngBytes()
        val fileName = "$userId.png"

        storage.from("avatars").upload(
            path = fileName,
            data = bytes,
            upsert = true
        )

        "${SupabaseClient.SUPABASE_URL}/storage/v1/object/public/avatars/$fileName"
    }.onFailure {
        Log.e("ProfileRepository", "uploadAvatar failed", it)
    }

    private fun Bitmap.toPngBytes(): ByteArray {
        val baos = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }
}

@Serializable
private data class ProfileInsertDto(
    val user_id: String,
    val photo: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val address: String? = null,
    val phone: String? = null
)

@Serializable
private data class ProfileUpdateDto(
    val photo: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val address: String? = null,
    val phone: String? = null
)