package com.example.store.ui.verification

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.R
import com.example.store.data.AuthRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException

/** ViewModel OTP проверки. Дата: 03.03.2026, Автор: Бубнов Никита */
class VerificationViewModel : ViewModel() {

    sealed class UiText {
        data class Res(@StringRes val id: Int) : UiText()
        data class Dynamic(val value: String) : UiText()
    }

    private val repo = AuthRepository()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<UiText?>(null)
    val error: StateFlow<UiText?> = _error

    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code

    private val _codeError = MutableStateFlow(false)
    val codeError: StateFlow<Boolean> = _codeError

    private val _secondsLeft = MutableStateFlow(60)
    val secondsLeft: StateFlow<Int> = _secondsLeft

    private val _verified = MutableStateFlow(false)
    val verified: StateFlow<Boolean> = _verified

    private var email: String = ""
    private var timerJob: Job? = null

    fun dismissError() {
        _error.value = null
    }

    fun consumeVerified() {
        _verified.value = false
    }

    fun init(email: String) {
        if (this.email == email && timerJob != null) return
        this.email = email
        startTimer(60)
    }

    fun onCodeChange(value: String) {
        val filtered = value.filter { it.isDigit() }.take(6)
        _code.value = filtered
        if (_codeError.value) _codeError.value = false
        if (filtered.length == 6) verify(filtered)
    }

    fun resend() {
        if (_secondsLeft.value > 0) return
        viewModelScope.launch {
            _loading.value = true
            val result = repo.resetPassword(email)
            _loading.value = false

            if (result.isSuccess) {
                _code.value = ""
                _codeError.value = false
                startTimer(60)
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

    private fun verify(token: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = repo.verifyOtp(email = email, token = token)
            _loading.value = false

            if (result.isSuccess) {
                _verified.value = true
            } else {
                _codeError.value = true
            }
        }
    }

    private fun startTimer(totalSeconds: Int) {
        timerJob?.cancel()
        _secondsLeft.value = totalSeconds
        timerJob = viewModelScope.launch {
            while (_secondsLeft.value > 0) {
                delay(1000)
                _secondsLeft.value = _secondsLeft.value - 1
            }
        }
    }
}