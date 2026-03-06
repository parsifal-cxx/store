package com.example.store.ui.checkout

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.store.R
import com.example.store.utils.LocationUtils

/** Экран Checkout. Дата: 06.03.2026, Автор: Бубнов Никита */
@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onReturnToShopping: () -> Unit,
    vm: CheckoutViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val ctx = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && LocationUtils.isLocationEnabled(ctx)) {
            LocationUtils.tryGetAddress(ctx)?.let { vm.setAddress(it) }
        }
    }

    LaunchedEffect(Unit) {
        if (LocationUtils.isLocationEnabled(ctx)) {
            val granted = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
            if (granted) {
                LocationUtils.tryGetAddress(ctx)?.let { vm.setAddress(it) }
            } else {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    state.errorRes?.let { res ->
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(stringResource(res)) },
            confirmButton = { TextButton(onClick = vm::dismissError) { Text(stringResource(R.string.ok)) } }
        )
    }

    if (state.showDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(R.string.checkout_dialog_title)) },
            text = { Text(stringResource(R.string.checkout_dialog_text)) },
            confirmButton = {
                TextButton(onClick = {
                    vm.dismissDialog()
                    onReturnToShopping()
                }) { Text(stringResource(R.string.checkout_back_to_shop)) }
            }
        )
    }

    val bg = colorResource(R.color.brand_background)
    val block = colorResource(R.color.brand_block)
    val text = colorResource(R.color.brand_text)
    val sub = colorResource(R.color.brand_sub_text_dark)
    val accent = colorResource(R.color.brand_accent)

    var editEmail by remember { mutableStateOf(false) }
    var editPhone by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = bg,
        bottomBar = {
            Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp)) {
                SummaryRow(stringResource(R.string.cart_sum), state.sum)
                SummaryRow(stringResource(R.string.cart_delivery), state.delivery)
                Divider(color = colorResource(R.color.brand_sub_text_light), thickness = 1.dp)
                SummaryRow(stringResource(R.string.cart_total), state.total, total = true)

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = vm::confirmOrder,
                    enabled = !state.loading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent,
                        contentColor = block,
                        disabledContainerColor = colorResource(R.color.brand_disable),
                        disabledContentColor = block
                    )
                ) { Text(stringResource(R.string.checkout_confirm)) }
            }
        }
    ) { inner ->
        Column(
            Modifier.fillMaxSize().padding(inner).background(bg).padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(64.dp))

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = block,
                    onClick = onBack
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(painterResource(R.drawable.ic_back), null, tint = text)
                    }
                }
                Spacer(Modifier.weight(1f))
                Text(stringResource(R.string.checkout_title), style = MaterialTheme.typography.headlineSmall, color = text)
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.size(40.dp))
            }

            Spacer(Modifier.height(14.dp))

            Surface(shape = RoundedCornerShape(16.dp), color = block) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Text(stringResource(R.string.checkout_contact), color = text, style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(10.dp))

                    ContactRow(
                        iconRes = R.drawable.ic_mail,
                        label = if (editEmail) "" else state.email,
                        editing = editEmail,
                        value = state.email,
                        onValueChange = vm::setEmail,
                        onToggleEdit = { editEmail = !editEmail },
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(Modifier.height(10.dp))

                    ContactRow(
                        iconRes = R.drawable.ic_phone,
                        label = if (editPhone) "" else state.phone,
                        editing = editPhone,
                        value = state.phone,
                        onValueChange = vm::setPhone,
                        onToggleEdit = { editPhone = !editPhone },
                        keyboardType = KeyboardType.Phone
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(stringResource(R.string.checkout_address), color = sub, style = MaterialTheme.typography.labelSmall)
                    Text(state.address, color = text, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = RoundedCornerShape(16.dp),
                color = block
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.checkout_map), color = sub)
                }
            }

            Spacer(Modifier.height(14.dp))

            Surface(shape = RoundedCornerShape(16.dp), color = block) {
                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.checkout_payment), color = text, style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.weight(1f))
                    Text("DbL Card •••• 6429", color = sub, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accent)
            }
        }
    }
}

@Composable
private fun ContactRow(
    iconRes: Int,
    label: String,
    editing: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    onToggleEdit: () -> Unit,
    keyboardType: KeyboardType
) {
    val sub = colorResource(R.color.brand_sub_text_dark)
    val text = colorResource(R.color.brand_text)
    val block = colorResource(R.color.brand_block)

    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(painterResource(iconRes), null, tint = sub)
        Spacer(Modifier.width(10.dp))

        if (!editing) {
            Text(label, color = text, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        } else {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f).height(48.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = block,
                    unfocusedContainerColor = block,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                ),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType)
            )
        }

        IconButton(onClick = onToggleEdit) {
            Icon(painterResource(R.drawable.ic_edit), null, tint = sub)
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, total: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = colorResource(R.color.brand_sub_text_dark))
        Text(
            value,
            style = if (total) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.labelSmall,
            color = if (total) colorResource(R.color.brand_accent) else colorResource(R.color.brand_sub_text_dark)
        )
    }
    Spacer(Modifier.height(6.dp))
}