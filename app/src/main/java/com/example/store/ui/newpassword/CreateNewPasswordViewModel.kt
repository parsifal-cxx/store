package com.example.store.ui.newpassword

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.R
import com.example.store.data.AuthRepository
import com.example.store.utils.AuthErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException

/** ViewModel смены пароля. Дата: 03.03.2026, Автор: Бубнов Никита */
class CreateNewPasswordViewModel : ViewModel() {

    sealed class UiText {
        data class Res(@StringRes val id: Int) : UiText()
        data class Dynamic(val value: String) : UiText()
    }

    private val repo = AuthRepository()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<UiText?>(null)
    val error: StateFlow<UiText?> = _error

    private val _done = MutableStateFlow(false)
    val done: StateFlow<Boolean> = _done

    fun dismissError() {
        _error.value = null
    }

    fun consumeDone() {
        _done.value = false
    }

    fun save(password: String, confirmPassword: String) {
        if (password.isBlank() || confirmPassword.isBlank()) {
            _error.value = UiText.Res(R.string.error_empty_fields)
            return
        }
        if (password != confirmPassword) {
            _error.value = UiText.Res(R.string.error_passwords_not_match)
            return
        }

        viewModelScope.launch {
            _loading.value = true
            val result = repo.updatePassword(password)
            _loading.value = false

            if (result.isSuccess) {
                _done.value = true
            } else {
                val id = AuthErrorMapper.toMessageId(result.exceptionOrNull(), R.string.error_unknown)
                _error.value = UiText.Res(id)
            }
        }
    }
}