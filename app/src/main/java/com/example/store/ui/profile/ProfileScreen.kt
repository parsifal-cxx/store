package com.example.store.ui.profile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.store.R
import com.example.store.ui.components.StoreTextField

/** Экран профиля. Дата: 05.03.2026, Автор: Бубнов Никита */
@Composable
fun ProfileScreen(
    vm: ProfileViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    val ctx = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) vm.setAvatar(bmp)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) cameraLauncher.launch(null)
    }

    fun openCamera() {
        val granted = ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        if (granted) cameraLauncher.launch(null) else permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    state.errorRes?.let { res ->
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(stringResource(res)) },
            confirmButton = { TextButton(onClick = vm::dismissError) { Text(stringResource(R.string.ok)) } }
        )
    }

    ProfileContent(
        state = state,
        onEdit = vm::startEdit,
        onCancel = vm::cancelEdit,
        onChangePhoto = ::openCamera,
        onSave = vm::save
    )

    if (state.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = colorResource(R.color.brand_accent))
        }
    }
}

/** Контент профиля. Дата: 05.03.2026, Автор: Бубнов Никита */
@Composable
private fun ProfileContent(
    state: ProfileViewModel.UiState,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
    onChangePhoto: () -> Unit,
    onSave: (String?, String?, String?, String?) -> Unit
) {
    val bg = colorResource(R.color.brand_background)
    val block = colorResource(R.color.brand_block)
    val fieldBg = colorResource(R.color.brand_sub_text_light)

    val textPrimary = colorResource(R.color.brand_text)
    val textSecondary = colorResource(R.color.brand_sub_text_dark)
    val hint = colorResource(R.color.brand_hint)
    val accent = colorResource(R.color.brand_accent)

    val profile = state.profile

    val fullName = listOfNotNull(
        profile?.firstname?.takeIf { it.isNotBlank() },
        profile?.lastname?.takeIf { it.isNotBlank() }
    ).joinToString(" ")

    var firstname by remember(profile?.firstname, state.isEditing) { mutableStateOf(profile?.firstname.orEmpty()) }
    var lastname by remember(profile?.lastname, state.isEditing) { mutableStateOf(profile?.lastname.orEmpty()) }
    var address by remember(profile?.address, state.isEditing) { mutableStateOf(profile?.address.orEmpty()) }
    var phone by remember(profile?.phone, state.isEditing) { mutableStateOf(profile?.phone.orEmpty()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(64.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.profile_title),
                style = MaterialTheme.typography.headlineLarge,
                color = textPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            if (!state.isEditing) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(28.dp),
                    shape = CircleShape,
                    color = accent,
                    onClick = onEdit
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = stringResource(R.string.profile_edit),
                            tint = block,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val avatarSize = 112.dp
            Avatar(
                size = avatarSize,
                background = block,
                url = profile?.photo,
                bitmap = state.avatarBitmap
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = fullName,
            style = MaterialTheme.typography.bodySmall,
            color = textSecondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(6.dp, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            color = block
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.img_profile_barcode),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .height(40.dp)
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        if (state.isEditing) {
            TextButton(
                onClick = onChangePhoto,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = stringResource(R.string.profile_change_photo),
                    style = MaterialTheme.typography.labelSmall,
                    color = accent
                )
            }

            Spacer(Modifier.height(14.dp))

            StoreTextField(
                label = stringResource(R.string.first_name),
                placeholder = "",
                value = firstname,
                onValueChange = { firstname = it }
            )
            Spacer(Modifier.height(12.dp))
            StoreTextField(
                label = stringResource(R.string.last_name),
                placeholder = "",
                value = lastname,
                onValueChange = { lastname = it }
            )
            Spacer(Modifier.height(12.dp))
            StoreTextField(
                label = stringResource(R.string.address),
                placeholder = "",
                value = address,
                onValueChange = { address = it }
            )
            Spacer(Modifier.height(12.dp))
            StoreTextField(
                label = stringResource(R.string.phone),
                placeholder = "",
                value = phone,
                onValueChange = { phone = it }
            )

            Spacer(Modifier.height(18.dp))

            Row(Modifier.fillMaxWidth()) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.brand_disable),
                        contentColor = block
                    )
                ) {
                    Text(stringResource(R.string.profile_cancel))
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = { onSave(firstname, lastname, phone, address) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent,
                        contentColor = block
                    )
                ) {
                    Text(stringResource(R.string.profile_save))
                }
            }
        } else {
            ProfileReadOnlyField(
                label = stringResource(R.string.first_name),
                value = profile?.firstname.orEmpty(),
                bg = fieldBg,
                labelColor = hint,
                valueColor = textPrimary
            )
            Spacer(Modifier.height(12.dp))
            ProfileReadOnlyField(
                label = stringResource(R.string.last_name),
                value = profile?.lastname.orEmpty(),
                bg = fieldBg,
                labelColor = hint,
                valueColor = textPrimary
            )
            Spacer(Modifier.height(12.dp))
            ProfileReadOnlyField(
                label = stringResource(R.string.address),
                value = profile?.address.orEmpty(),
                bg = fieldBg,
                labelColor = hint,
                valueColor = textPrimary
            )
            Spacer(Modifier.height(12.dp))
            ProfileReadOnlyField(
                label = stringResource(R.string.phone),
                value = profile?.phone.orEmpty(),
                bg = fieldBg,
                labelColor = hint,
                valueColor = textPrimary
            )
        }
    }
}

/** Аватар пользователя. Дата: 05.03.2026, Автор: Бубнов Никита */
@Composable
private fun Avatar(
    size: androidx.compose.ui.unit.Dp,
    background: Color,
    url: String?,
    bitmap: Bitmap?
) {
    Box(
        modifier = Modifier
            .size(size)
            .shadow(8.dp, CircleShape, clip = false)
            .background(background, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        when {
            bitmap != null -> {
                AsyncImage(
                    model = bitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            !url.isNullOrBlank() -> {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

/** Поле профиля только для чтения. Дата: 05.03.2026, Автор: Бубнов Никита */
@Composable
private fun ProfileReadOnlyField(
    label: String,
    value: String,
    bg: Color,
    labelColor: Color,
    valueColor: Color
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = labelColor
    )
    Spacer(Modifier.height(6.dp))
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        color = bg
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = valueColor
            )
        }
    }
}