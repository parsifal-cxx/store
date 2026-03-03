package com.example.store.ui.register

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.store.R
import com.example.store.ui.components.StoreTextField
import com.example.store.ui.theme.StoreTheme

/** Экран регистрации. Дата: 03.03.2026, Автор: Бубнов Никита */
@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    vm: RegisterViewModel = viewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var agree by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val loading by vm.loading.collectAsState()
    val success by vm.success.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(success) {
        if (success) {
            vm.consumeSuccess()
            onNavigateToSignIn()
        }
    }

    error?.let { err ->
        val msg = when (err) {
            is RegisterViewModel.UiText.Res -> stringResource(err.id)
            is RegisterViewModel.UiText.Dynamic -> err.value
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

    RegisterContent(
        fullName = fullName,
        email = email,
        password = password,
        agree = agree,
        passwordVisible = passwordVisible,
        loading = loading,
        onBack = onBack,
        onFullNameChange = { fullName = it },
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onTogglePassword = { passwordVisible = !passwordVisible },
        onAgreeChange = { agree = it },
        onRegisterClick = { vm.register(email, password) },
        onSignInClick = onNavigateToSignIn
    )
}

/** UI регистрации без ViewModel (Preview). Дата: 03.03.2026, Автор: Бубнов Никита */
@Composable
fun RegisterContent(
    fullName: String,
    email: String,
    password: String,
    agree: Boolean,
    passwordVisible: Boolean,
    loading: Boolean,
    onBack: () -> Unit,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onAgreeChange: (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onSignInClick: () -> Unit
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
            Spacer(Modifier.height(48.dp))

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
                text = stringResource(R.string.register_title),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = TextStyle(fontSize = 22.sp, color = textPrimary)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.register_subtitle),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = TextStyle(fontSize = 12.sp, color = textSecondary)
            )

            Spacer(Modifier.height(28.dp))

            StoreTextField(
                label = stringResource(R.string.name_label),
                placeholder = stringResource(R.string.name_placeholder),
                value = fullName,
                onValueChange = onFullNameChange
            )

            Spacer(Modifier.height(14.dp))

            StoreTextField(
                label = stringResource(R.string.email_label),
                placeholder = stringResource(R.string.email_placeholder),
                value = email,
                onValueChange = onEmailChange
            )

            Spacer(Modifier.height(14.dp))

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

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAgreeChange(!agree) },
                verticalAlignment = Alignment.Top
            ) {
                CircleCheck(
                    checked = agree,
                    modifier = Modifier.padding(top = 3.dp),
                    borderColor = iconPrimary,
                    fillColor = iconPrimary
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.agree_personal_data),
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = textSecondary,
                        textDecoration = TextDecoration.Underline
                    )
                )
            }

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onRegisterClick,
                enabled = agree && !loading,
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
                Text(text = stringResource(R.string.register_button), fontSize = 12.sp)
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.have_account) + " ",
                    style = TextStyle(fontSize = 12.sp, color = textSecondary)
                )
                Text(
                    text = stringResource(R.string.sign_in_link),
                    style = TextStyle(fontSize = 12.sp, color = textPrimary),
                    modifier = Modifier.clickable(onClick = onSignInClick)
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

@Composable
private fun CircleCheck(
    checked: Boolean,
    modifier: Modifier = Modifier,
    borderColor: Color,
    fillColor: Color
) {
    Surface(
        modifier = modifier.size(16.dp),
        shape = CircleShape,
        color = Color.Transparent,
        border = BorderStroke(1.dp, borderColor)
    ) {
        if (checked) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(8.dp),
                    shape = CircleShape,
                    color = fillColor
                ) {}
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterPreview() {
    StoreTheme {
        RegisterContent(
            fullName = "",
            email = "",
            password = "",
            agree = false,
            passwordVisible = false,
            loading = false,
            onBack = {},
            onFullNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePassword = {},
            onAgreeChange = {},
            onRegisterClick = {},
            onSignInClick = {}
        )
    }
}