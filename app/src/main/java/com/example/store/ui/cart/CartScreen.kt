package com.example.store.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.store.R
import kotlinx.coroutines.launch

/** Экран корзины. Дата: 06.03.2026, Автор: Бубнов Никита */
@Composable
fun CartScreen(
    onBack: () -> Unit,
    onCheckout: () -> Unit,
    vm: CartViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    val bg = colorResource(R.color.brand_background)
    val accent = colorResource(R.color.brand_accent)
    val text = colorResource(R.color.brand_text)
    val block = colorResource(R.color.brand_block)

    state.errorRes?.let { res ->
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(stringResource(res)) },
            confirmButton = { TextButton(onClick = vm::dismissError) { Text(stringResource(R.string.ok)) } }
        )
    }

    Scaffold(
        containerColor = bg,
        bottomBar = {
            Surface(color = bg) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp)) {
                    SummaryRow(stringResource(R.string.cart_sum), state.sum)
                    SummaryRow(stringResource(R.string.cart_delivery), state.delivery)
                    Divider(color = colorResource(R.color.brand_sub_text_light), thickness = 1.dp)
                    SummaryRow(stringResource(R.string.cart_total), state.total, total = true)

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = onCheckout,
                        enabled = state.items.isNotEmpty() && !state.loading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accent,
                            contentColor = block,
                            disabledContainerColor = colorResource(R.color.brand_disable),
                            disabledContentColor = block
                        )
                    ) {
                        Text(stringResource(R.string.cart_checkout))
                    }
                }
            }
        }
    ) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .background(bg)
        ) {
            Spacer(Modifier.height(64.dp))

            Row(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = block,
                    onClick = onBack
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = null,
                            tint = text
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Text(
                    text = stringResource(R.string.cart_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = text
                )

                Spacer(Modifier.weight(1f))
                Spacer(Modifier.size(40.dp))
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.cart_items_count, state.items.size),
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.brand_sub_text_dark),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(state.items, key = { it.rowId }) { item ->
                    CartSwipeItem(
                        item = item,
                        onInc = { vm.inc(item.productId) },
                        onDec = { vm.dec(item.productId) },
                        onDelete = { vm.deleteRow(item.rowId) }
                    )
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
private fun CartSwipeItem(
    item: CartViewModel.UiItem,
    onInc: () -> Unit,
    onDec: () -> Unit,
    onDelete: () -> Unit
) {
    val blue = colorResource(R.color.brand_accent)
    val red = colorResource(R.color.brand_red)
    val block = colorResource(R.color.brand_block)
    val text = colorResource(R.color.brand_text)

    val scope = rememberCoroutineScope()
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { true }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            when (dismissState.currentValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    // не используется
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    // не используется
                }
                else -> Unit
            }

            val direction = dismissState.targetValue
            if (direction == SwipeToDismissBoxValue.StartToEnd) {
                // + / count / -
                Row(
                    Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.width(54.dp).fillMaxHeight(),
                        shape = RoundedCornerShape(14.dp),
                        color = blue
                    ) {
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconButton(onClick = {
                                onInc()
                                scope.launch { dismissState.reset() }
                            }) {
                                Icon(painterResource(R.drawable.ic_plus), null, tint = block)
                            }
                            Text(item.count.toString(), color = block, style = MaterialTheme.typography.bodyMedium)
                            IconButton(onClick = {
                                onDec()
                                scope.launch { dismissState.reset() }
                            }) {
                                Icon(painterResource(R.drawable.ic_minus), null, tint = block)
                            }
                        }
                    }
                }
            } else if (direction == SwipeToDismissBoxValue.EndToStart) {
                // delete
                Row(
                    Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.width(70.dp).fillMaxHeight(),
                        shape = RoundedCornerShape(14.dp),
                        color = red,
                        onClick = {
                            onDelete()
                            scope.launch { dismissState.reset() }
                        }
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(painterResource(R.drawable.ic_trash), null, tint = block)
                        }
                    }
                }
            }
        },
        content = {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = block,
                tonalElevation = 2.dp
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = colorResource(R.color.brand_sub_text_light)
                    ) {
                        AsyncImage(model = item.imageUrl, contentDescription = null)
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(Modifier.weight(1f)) {
                        Text(item.title, color = text, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(item.price, color = colorResource(R.color.brand_sub_text_dark), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    )
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