package com.example.store.ui.forgot

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.R
import com.example.store.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException

/** ViewModel восстановления пароля. Дата: 03.03.2026, Автор: Бубнов Никита */
class ForgotPasswordViewModel : ViewModel() {

    sealed class UiText {
        data class Res(@StringRes val id: Int) : UiText()
        data class Dynamic(val value: String) : UiText()
    }

    private val repo = AuthRepository()
    private val emailRegex = Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{3,}$")

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<UiText?>(null)
    val error: StateFlow<UiText?> = _error

    private val _sentDialog = MutableStateFlow(false)
    val sentDialog: StateFlow<Boolean> = _sentDialog

    fun dismissError() {
        _error.value = null
    }

    fun dismissSentDialog() {
        _sentDialog.value = false
    }

    fun sendCode(email: String) {
        val e = email.trim()

        if (e.isBlank()) {
            _error.value = UiText.Res(R.string.error_empty_fields)
            return
        }

        if (!emailRegex.matches(e)) {
            _error.value = UiText.Res(R.string.error_invalid_email)
            return
        }

        viewModelScope.launch {
            _loading.value = true
            val result = repo.resetPassword(e)
            _loading.value = false

            if (result.isSuccess) {
                _sentDialog.value = true
            } else {
                val ex = result.exceptionOrNull()
                _error.value = when (ex) {
                    is UnknownHostException -> UiText.Res(R.string.error_no_internet)
                    else -> ex?.message?.takeIf { it.isNotBlank() }?.let { UiText.Dynamic(it) }
                        ?: UiText.Res(R.string.error_unknown)
                }
            }
        }
    }
}