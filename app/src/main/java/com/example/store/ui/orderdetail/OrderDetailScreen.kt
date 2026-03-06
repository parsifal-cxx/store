package com.example.store.ui.orderdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.store.R

/** Экран Detail Order. Дата: 06.03.2026, Автор: Бубнов Никита */
@Composable
fun OrderDetailScreen(
    orderId: Long,
    onBack: () -> Unit,
    vm: OrderDetailViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(orderId) { vm.load(orderId) }

    val bg = colorResource(R.color.brand_background)
    val block = colorResource(R.color.brand_block)
    val text = colorResource(R.color.brand_text)
    val sub = colorResource(R.color.brand_sub_text_dark)
    val accent = colorResource(R.color.brand_accent)

    Box(Modifier.fillMaxSize().background(bg)) {
        LazyColumn(
            contentPadding = PaddingValues(top = 64.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
        ) {
            item {
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
                    Text(orderId.toString(), style = MaterialTheme.typography.headlineSmall, color = text)
                    Spacer(Modifier.weight(1f))
                    Spacer(Modifier.size(40.dp))
                }
            }

            items(state.items) { it ->
                Surface(shape = RoundedCornerShape(16.dp), color = block) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = colorResource(R.color.brand_sub_text_light)
                        ) { AsyncImage(model = it.imageUrl, contentDescription = null) }

                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(it.title, color = text, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(it.price, color = sub, style = MaterialTheme.typography.labelSmall)
                        }
                        Text(it.count, color = sub, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Surface(shape = RoundedCornerShape(16.dp), color = block) {
                    Column(Modifier.fillMaxWidth().padding(14.dp)) {
                        Text(stringResource(R.string.checkout_contact), color = text, style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.height(10.dp))
                        Text(state.order?.email.orEmpty(), color = sub)
                        Text(state.order?.phone.orEmpty(), color = sub)
                        Spacer(Modifier.height(10.dp))
                        Text(stringResource(R.string.checkout_address), color = sub, style = MaterialTheme.typography.labelSmall)
                        Text(state.order?.address.orEmpty(), color = text, style = MaterialTheme.typography.bodySmall)
                    }
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