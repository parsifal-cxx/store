package com.example.store.ui.forgot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.store.R
import com.example.store.ui.components.StoreTextField
import com.example.store.ui.theme.StoreTheme
import kotlinx.coroutines.delay

/** Экран восстановления пароля. Дата: 03.03.2026, Автор: Бубнов Никита */
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onNavigateToVerification: (email: String) -> Unit,
    vm: ForgotPasswordViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }

    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val sentDialog by vm.sentDialog.collectAsState()

    error?.let { err ->
        val msg = when (err) {
            is ForgotPasswordViewModel.UiText.Res -> stringResource(err.id)
            is ForgotPasswordViewModel.UiText.Dynamic -> err.value
        }

        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(msg) },
            confirmButton = {
                TextButton(onClick = vm::dismissError) { Text(stringResource(R.string.ok)) }
            }
        )
    }

    LaunchedEffect(sentDialog) {
        if (sentDialog) {
            delay(4000)
            if (vm.sentDialog.value) {
                vm.dismissSentDialog()
                onNavigateToVerification(email.trim())
            }
        }
    }

    if (sentDialog) {
        EmailSentDialog(
            onClick = {
                vm.dismissSentDialog()
                onNavigateToVerification(email.trim())
            }
        )
    }

    ForgotPasswordContent(
        email = email,
        loading = loading,
        onBack = onBack,
        onEmailChange = { email = it },
        onSendClick = { vm.sendCode(email) }
    )
}

/** UI восстановления пароля. Дата: 03.03.2026, Автор: Бубнов Никита */
@Composable
fun ForgotPasswordContent(
    email: String,
    loading: Boolean,
    onBack: () -> Unit,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    val bg = colorResource(R.color.brand_background)
    val fieldBg = colorResource(R.color.brand_sub_text_light)

    val textPrimary = colorResource(R.color.brand_text)
    val textSecondary = colorResource(R.color.brand_sub_text_dark)

    val btnActive = colorResource(R.color.brand_accent)
    val btnDisabled = colorResource(R.color.brand_disable)
    val btnText = colorResource(R.color.brand_block)

    val shape = RoundedCornerShape(14.dp)

    Box(Modifier.fillMaxSize().background(bg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(64.dp))

            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = fieldBg,
                onClick = onBack
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = textPrimary
                    )
                }
            }

            Spacer(Modifier.height(36.dp))

            Text(
                text = stringResource(R.string.forgot_password_title),
                style = MaterialTheme.typography.headlineLarge,
                color = textPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.forgot_password_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = textSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(34.dp))

            StoreTextField(
                label = "",
                placeholder = stringResource(R.string.email_placeholder),
                value = email,
                onValueChange = onEmailChange,
                showLabel = false
            )

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onSendClick,
                enabled = !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = btnActive,
                    contentColor = btnText,
                    disabledContainerColor = btnDisabled,
                    disabledContentColor = btnText
                )
            ) {
                Text(text = stringResource(R.string.send_button), style = MaterialTheme.typography.labelLarge)
            }
        }

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = btnActive)
            }
        }
    }
}

@Composable
private fun EmailSentDialog(onClick: () -> Unit) {
    val border = colorResource(R.color.brand_accent)
    val cardBg = colorResource(R.color.brand_block)
    val textPrimary = colorResource(R.color.brand_text)
    val textSecondary = colorResource(R.color.brand_sub_text_dark)

    Dialog(onDismissRequest = { }) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = cardBg,
            border = androidx.compose.foundation.BorderStroke(2.dp, border),
            onClick = onClick
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.email_sent_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = textPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.email_sent_message),
                    style = MaterialTheme.typography.labelSmall,
                    color = textSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ForgotPasswordPreview() {
    StoreTheme {
        ForgotPasswordContent(
            email = "",
            loading = false,
            onBack = {},
            onEmailChange = {},
            onSendClick = {}
        )
    }
}