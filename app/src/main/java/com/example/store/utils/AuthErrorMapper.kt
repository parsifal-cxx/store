package com.example.store.utils

import androidx.annotation.StringRes
import com.example.store.R
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import java.net.UnknownHostException
import java.security.cert.CertPathValidatorException
import javax.net.ssl.SSLHandshakeException

/** Маппинг ошибок сети/сервера в строки ресурсов. Дата: 03.03.2026, Автор: Бубнов Никита */
object AuthErrorMapper {

    @StringRes
    fun toMessageId(
        throwable: Throwable?,
        @StringRes defaultId: Int = R.string.error_unknown
    ): Int {
        if (throwable == null) return defaultId

        return when (throwable) {
            is UnknownHostException -> R.string.error_no_internet
            is SSLHandshakeException,
            is CertPathValidatorException -> R.string.error_secure_connection

            is ClientRequestException -> mapHttp4xx(throwable)

            is ServerResponseException -> R.string.error_server

            else -> {
                val msg = (throwable.message ?: "").lowercase()
                when {
                    "already registered" in msg -> R.string.error_user_already_registered
                    "invalid login credentials" in msg -> R.string.error_invalid_credentials
                    "too many requests" in msg -> R.string.error_too_many_requests
                    else -> defaultId
                }
            }
        }
    }

    @StringRes
    private fun mapHttp4xx(e: ClientRequestException): Int {
        return when (e.response.status.value) {
            400 -> R.string.error_bad_request
            401, 403 -> R.string.error_invalid_credentials
            409 -> R.string.error_user_already_registered
            429 -> R.string.error_too_many_requests
            else -> R.string.error_unknown
        }
    }
}