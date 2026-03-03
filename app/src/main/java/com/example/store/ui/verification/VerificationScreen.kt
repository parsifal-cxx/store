package com.example.store.ui.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.store.R
import com.example.store.ui.theme.StoreTheme

/** Экран OTP проверки. Дата: 03.03.2026, Автор: Бубнов Никита */
@Composable
fun VerificationScreen(
    email: String,
    onBack: () -> Unit,
    onNavigateToCreateNewPassword: () -> Unit,
    vm: VerificationViewModel = viewModel()
) {
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val code by vm.code.collectAsState()
    val secondsLeft by vm.secondsLeft.collectAsState()
    val blinkAll by vm.blinkAll.collectAsState()
    val verified by vm.verified.collectAsState()

    LaunchedEffect(email) { vm.init(email) }

    LaunchedEffect(verified) {
        if (verified) {
            vm.consumeVerified()
            onNavigateToCreateNewPassword()
        }
    }

    error?.let { err ->
        val msg = when (err) {
            is VerificationViewModel.UiText.Res -> stringResource(err.id)
            is VerificationViewModel.UiText.Dynamic -> err.value
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

    VerificationContent(
        code = code,
        blinkAll = blinkAll,
        secondsLeft = secondsLeft,
        loading = loading,
        onBack = onBack,
        onCodeChange = vm::onCodeChange,
        onResend = vm::resend
    )
}

/** UI OTP проверки. Дата: 03.03.2026, Автор: Бубнов Никита */
@Composable
fun VerificationContent(
    code: String,
    blinkAll: Boolean,
    secondsLeft: Int,
    loading: Boolean,
    onBack: () -> Unit,
    onCodeChange: (String) -> Unit,
    onResend: () -> Unit
) {
    val bg = colorResource(R.color.brand_background)
    val block = colorResource(R.color.brand_block)
    val textPrimary = colorResource(R.color.brand_text)
    val textSecondary = colorResource(R.color.brand_sub_text_dark)
    val hint = colorResource(R.color.brand_hint)
    val accent = colorResource(R.color.brand_accent)
    val red = colorResource(R.color.brand_red)

    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

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
                color = block,
                onClick = onBack
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        tint = textPrimary
                    )
                }
            }

            Spacer(Modifier.height(36.dp))

            Text(
                text = stringResource(R.string.otp_title),
                style = MaterialTheme.typography.headlineLarge,
                color = textPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.otp_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = textSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(34.dp))

            Text(
                text = stringResource(R.string.otp_code_label),
                style = MaterialTheme.typography.labelSmall,
                color = hint
            )

            Spacer(Modifier.height(10.dp))

            OtpInput(
                code = code,
                blinkAll = blinkAll,
                secondsLeft = secondsLeft,
                blockColor = block,
                red = red,
                hint = hint,
                textPrimary = textPrimary,
                onCodeChange = onCodeChange,
                onResend = onResend,
                focusRequester = focusRequester
            )

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                keyboard?.show()
            }
        }

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accent)
            }
        }
    }
}

@Composable
private fun OtpInput(
    code: String,
    blinkAll: Boolean,
    secondsLeft: Int,
    blockColor: Color,
    red: Color,
    hint: Color,
    textPrimary: Color,
    onCodeChange: (String) -> Unit,
    onResend: () -> Unit,
    focusRequester: FocusRequester
) {
    val digitsCount = 6
    val cellShape = RoundedCornerShape(12.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusRequester.requestFocus() }
    ) {
        Box {
            BasicTextField(
                value = code,
                onValueChange = onCodeChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                textStyle = TextStyle(color = Color.Transparent),
                cursorBrush = SolidColor(Color.Transparent)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until digitsCount) {
                    val ch = code.getOrNull(i)?.toString() ?: ""
                    val isActiveCell = code.length < digitsCount && i == code.length

                    val borderColor = when {
                        blinkAll -> red
                        isActiveCell -> red
                        else -> Color.Transparent
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(44f / 64f),
                        color = blockColor,
                        shape = cellShape,
                        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = ch,
                                style = MaterialTheme.typography.bodyMedium,
                                color = textPrimary
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (secondsLeft > 0) {
                Text(
                    text = formatTime(secondsLeft),
                    style = MaterialTheme.typography.labelSmall,
                    color = hint
                )
            } else {
                TextButton(
                    onClick = onResend,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.resend_again),
                        style = MaterialTheme.typography.labelSmall,
                        color = hint
                    )
                }
            }
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%02d:%02d".format(m, s)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun VerificationPreview() {
    StoreTheme {
        VerificationContent(
            code = "123",
            blinkAll = false,
            secondsLeft = 42,
            loading = false,
            onBack = {},
            onCodeChange = {},
            onResend = {}
        )
    }
}