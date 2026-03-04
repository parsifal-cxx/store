package com.example.store.ui.signin

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.R
import com.example.store.data.AuthRepository
import com.example.store.utils.AuthErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** ViewModel входа. Дата: 03.03.2026, Автор: Бубнов Никита */
class SignInViewModel : ViewModel() {

    sealed class UiText {
        data class Res(@StringRes val id: Int) : UiText()
    }

    private val repo = AuthRepository()
    private val emailRegex = Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$")

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _error = MutableStateFlow<UiText?>(null)
    val error: StateFlow<UiText?> = _error

    fun dismissError() {
        _error.value = null
    }

    fun consumeSuccess() {
        _success.value = false
    }

    fun signIn(email: String, password: String) {
        val e = email.trim()
        val p = password

        if (e.isBlank() || p.isBlank()) {
            _error.value = UiText.Res(R.string.error_empty_fields)
            return
        }

        if (!emailRegex.matches(e)) {
            _error.value = UiText.Res(R.string.error_invalid_email)
            return
        }

        viewModelScope.launch {
            _loading.value = true
            val result = repo.signIn(e, p)
            _loading.value = false

            if (result.isSuccess) {
                _success.value = true
            } else {
                val id = AuthErrorMapper.toMessageId(
                    throwable = result.exceptionOrNull(),
                    defaultId = R.string.error_login_failed
                )
                _error.value = UiText.Res(id)
            }
        }
    }
}