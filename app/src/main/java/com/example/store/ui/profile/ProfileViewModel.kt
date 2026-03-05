package com.example.store.ui.profile

import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.R
import com.example.store.data.ProfileRepository
import com.example.store.data.model.ProfileDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** ViewModel профиля. Дата: 05.03.2026, Автор: Бубнов Никита */
class ProfileViewModel : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        @StringRes val errorRes: Int? = null,
        val profile: ProfileDto? = null,
        val isEditing: Boolean = false,
        val avatarBitmap: Bitmap? = null
    )

    private val repo = ProfileRepository()

    private val _state = MutableStateFlow(UiState(loading = true))
    val state: StateFlow<UiState> = _state

    init {
        load()
    }

    fun dismissError() {
        _state.value = _state.value.copy(errorRes = null)
    }

    fun startEdit() {
        _state.value = _state.value.copy(isEditing = true)
    }

    fun cancelEdit() {
        _state.value = _state.value.copy(isEditing = false, avatarBitmap = null)
    }

    fun setAvatar(bitmap: Bitmap) {
        _state.value = _state.value.copy(avatarBitmap = bitmap)
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, errorRes = null)

            val userId = repo.currentUserId()
            if (userId == null) {
                _state.value = _state.value.copy(loading = false, errorRes = R.string.error_not_authorized)
                return@launch
            }

            val profile = repo.loadProfile(userId).getOrNull()
            _state.value = _state.value.copy(
                loading = false,
                profile = profile,
                isEditing = (profile == null)
            )
        }
    }

    fun save(firstname: String?, lastname: String?, phone: String?, address: String?) {
        viewModelScope.launch {
            val userId = repo.currentUserId()
            if (userId == null) {
                _state.value = _state.value.copy(errorRes = R.string.error_not_authorized)
                return@launch
            }

            _state.value = _state.value.copy(loading = true, errorRes = null)

            val avatarUrl = _state.value.avatarBitmap?.let { bmp ->
                repo.uploadAvatar(userId, bmp).getOrNull()
            } ?: _state.value.profile?.photo

            val saveRes = repo.saveProfile(
                userId = userId,
                firstname = firstname,
                lastname = lastname,
                phone = phone,
                address = address,
                photoUrl = avatarUrl
            )

            if (saveRes.isFailure) {
                _state.value = _state.value.copy(loading = false, errorRes = R.string.error_unknown, isEditing = true)
                return@launch
            }

            val updated = repo.loadProfile(userId).getOrNull()
            _state.value = _state.value.copy(
                loading = false,
                profile = updated,
                isEditing = false,
                avatarBitmap = null
            )
        }
    }
}