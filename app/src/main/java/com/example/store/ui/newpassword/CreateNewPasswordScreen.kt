package com.example.store.ui.newpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.store.R
import com.example.store.ui.components.StoreTextField
import com.example.store.ui.theme.StoreTheme

/** Экран задания нового пароля. Дата: 03.03.2026, Автор: Бубнов Никита */
@Composable
fun CreateNewPasswordScreen(
    onBack: () -> Unit,
    onDone: () -> Unit,
    vm: CreateNewPasswordViewModel = viewModel()
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val done by vm.done.collectAsState()

    LaunchedEffect(done) {
        if (done) {
            vm.consumeDone()
            onDone()
        }
    }

    error?.let { err ->
        val msg = when (err) {
            is CreateNewPasswordViewModel.UiText.Res -> stringResource(err.id)
            is CreateNewPasswordViewModel.UiText.Dynamic -> err.value
        }

        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(msg) },
            confirmButton = {
                TextButton(onClick = vm::dismissError) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }

    CreateNewPasswordContent(
        password = password,
        confirmPassword = confirmPassword,
        passwordVisible = passwordVisible,
        confirmVisible = confirmVisible,
        loading = loading,
        onBack = onBack,
        onPasswordChange = { password = it },
        onConfirmPasswordChange = { confirmPassword = it },
        onTogglePassword = { passwordVisible = !passwordVisible },
        onToggleConfirm = { confirmVisible = !confirmVisible },
        onSave = { vm.save(password, confirmPassword) }
    )
}

/** UI задания нового пароля. Дата: 03.03.2026, Автор: Бубнов Никита */
@Composable
fun CreateNewPasswordContent(
    password: String,
    confirmPassword: String,
    passwordVisible: Boolean,
    confirmVisible: Boolean,
    loading: Boolean,
    onBack: () -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onToggleConfirm: () -> Unit,
    onSave: () -> Unit
) {
    val bg = colorResource(R.color.brand_background)
    val fieldBg = colorResource(R.color.brand_sub_text_light)

    val textPrimary = colorResource(R.color.brand_text)
    val textSecondary = colorResource(R.color.brand_sub_text_dark)
    val iconPrimary = colorResource(R.color.brand_text)
    val iconSecondary = colorResource(R.color.brand_hint)

    val btnActive = colorResource(R.color.brand_accent)
    val btnDisabled = colorResource(R.color.brand_disable)
    val btnText = colorResource(R.color.brand_block)

    val shape = RoundedCornerShape(14.dp)

    val canSave = password.isNotBlank() && confirmPassword.isNotBlank() && password == confirmPassword && !loading

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
                        tint = iconPrimary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.new_password_title),
                style = MaterialTheme.typography.headlineLarge,
                color = textPrimary
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.new_password_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = textSecondary
            )

            Spacer(Modifier.height(28.dp))

            StoreTextField(
                label = stringResource(R.string.password_label),
                placeholder = stringResource(R.string.password_placeholder),
                value = password,
                onValueChange = onPasswordChange,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = stringResource(
                                if (passwordVisible) R.string.cd_hide_password else R.string.cd_show_password
                            ),
                            tint = iconSecondary
                        )
                    }
                }
            )

            Spacer(Modifier.height(18.dp))

            StoreTextField(
                label = stringResource(R.string.confirm_password_label),
                placeholder = stringResource(R.string.password_placeholder),
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = onToggleConfirm) {
                        Icon(
                            imageVector = if (confirmVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = stringResource(
                                if (confirmVisible) R.string.cd_hide_password else R.string.cd_show_password
                            ),
                            tint = iconSecondary
                        )
                    }
                }
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = onSave,
                enabled = canSave,
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
                Text(
                    text = stringResource(R.string.save_button),
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = btnActive)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CreateNewPasswordPreview() {
    StoreTheme {
        CreateNewPasswordContent(
            password = "12345678",
            confirmPassword = "12345678",
            passwordVisible = false,
            confirmVisible = false,
            loading = false,
            onBack = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePassword = {},
            onToggleConfirm = {},
            onSave = {}
        )
    }
}