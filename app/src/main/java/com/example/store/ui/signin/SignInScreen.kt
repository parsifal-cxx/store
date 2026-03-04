package com.example.store.ui.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.store.R
import com.example.store.ui.components.StoreTextField
import com.example.store.ui.theme.StoreTheme

/** Экран входа. Дата: 03.03.2026, Автор: Бубнов Никита */
@Composable
fun SignInScreen(
    onBack: () -> Unit,
    onNavigateToForgot: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onSignedIn: () -> Unit,
    vm: SignInViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val loading by vm.loading.collectAsState()
    val success by vm.success.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(success) {
        if (success) {
            vm.consumeSuccess()
            onSignedIn()
        }
    }

    error?.let { err ->
        val msg = when (err) {
            is SignInViewModel.UiText.Res -> stringResource(err.id)
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

    SignInContent(
        email = email,
        password = password,
        passwordVisible = passwordVisible,
        loading = loading,
        onBack = onBack,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onTogglePassword = { passwordVisible = !passwordVisible },
        onForgotClick = onNavigateToForgot,
        onSignInClick = { vm.signIn(email, password) },
        onCreateClick = onNavigateToRegister
    )
}

/** UI входа (Preview). Дата: 03.03.2026, Автор: Бубнов Никита */
@Composable
fun SignInContent(
    email: String,
    password: String,
    passwordVisible: Boolean,
    loading: Boolean,
    onBack: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onForgotClick: () -> Unit,
    onSignInClick: () -> Unit,
    onCreateClick: () -> Unit
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

            Spacer(Modifier.height(36.dp))

            Text(
                text = stringResource(R.string.sign_in_title),
                style = MaterialTheme.typography.headlineLarge,
                color = textPrimary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.sign_in_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = textSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(34.dp))

            StoreTextField(
                label = stringResource(R.string.email_label),
                placeholder = stringResource(R.string.email_placeholder),
                value = email,
                onValueChange = onEmailChange
            )

            Spacer(Modifier.height(18.dp))

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

            Spacer(Modifier.height(6.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    text = stringResource(R.string.forgot_password_link),
                    style = MaterialTheme.typography.labelSmall,
                    color = textSecondary,
                    modifier = Modifier.clickable(onClick = onForgotClick)
                )
            }

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onSignInClick,
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
                Text(text = stringResource(R.string.sign_in_button), style = MaterialTheme.typography.labelLarge)
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.no_account) + " ",
                    style = MaterialTheme.typography.labelSmall,
                    color = textSecondary
                )
                Text(
                    text = stringResource(R.string.create_user_link),
                    style = MaterialTheme.typography.labelSmall,
                    color = textPrimary,
                    modifier = Modifier.clickable(onClick = onCreateClick)
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
private fun SignInPreview() {
    StoreTheme {
        SignInContent(
            email = "",
            password = "",
            passwordVisible = false,
            loading = false,
            onBack = {},
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePassword = {},
            onForgotClick = {},
            onSignInClick = {},
            onCreateClick = {}
        )
    }
}