package com.example.store.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.store.R
import kotlinx.coroutines.launch

/** Экран заказов. Дата: 06.03.2026, Автор: Бубнов Никита */
@Composable
fun OrdersScreen(
    onOpenDetail: (Long) -> Unit,
    vm: OrdersViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val bg = colorResource(R.color.brand_background)
    val accent = colorResource(R.color.brand_accent)

    state.errorRes?.let { res ->
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Ошибка") },
            text = { Text(res.toString()) },
            confirmButton = { TextButton(onClick = vm::dismissError) { Text("OK") } }
        )
    }

    Box(Modifier.fillMaxSize().background(bg)) {
        LazyColumn(
            contentPadding = PaddingValues(top = 64.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
        ) {
            itemsIndexed(state.rows) { idx, row ->
                when (row) {
                    is OrdersViewModel.UiRow.Header -> {
                        Text(row.title, style = MaterialTheme.typography.labelLarge, color = colorResource(R.color.brand_text))
                    }
                    is OrdersViewModel.UiRow.OrderRow -> {
                        OrderSwipeCard(
                            row = row,
                            onOpen = { onOpenDetail(row.orderId) },
                            onRepeat = { vm.repeatOrder(row.orderId) },
                            onDelete = { vm.deleteOrder(row.orderId) }
                        )
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

@Composable
private fun OrderSwipeCard(
    row: OrdersViewModel.UiRow.OrderRow,
    onOpen: () -> Unit,
    onRepeat: () -> Unit,
    onDelete: () -> Unit
) {
    val blue = colorResource(R.color.brand_accent)
    val red = colorResource(R.color.brand_red)
    val block = colorResource(R.color.brand_block)
    val text = colorResource(R.color.brand_text)
    val sub = colorResource(R.color.brand_sub_text_dark)

    val scope = rememberCoroutineScope()
    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = { true })

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val target = dismissState.targetValue
            if (target == SwipeToDismissBoxValue.StartToEnd) {
                // Вправо: Повторить
                Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = blue,
                        onClick = {
                            onRepeat()
                            scope.launch { dismissState.reset() }
                        }
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(painterResource(R.drawable.ic_repeat), null, tint = block)
                        }
                    }
                }
            } else if (target == SwipeToDismissBoxValue.EndToStart) {

                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(56.dp),
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
                onClick = onOpen
            ) {
                Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = colorResource(R.color.brand_sub_text_light)
                    ) {
                        AsyncImage(model = row.imageUrl, contentDescription = null)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("№ ${row.orderId}", color = blue, style = MaterialTheme.typography.labelSmall)
                        Text(row.title, color = text, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(4.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(row.price, color = sub, style = MaterialTheme.typography.labelSmall)
                            Text(row.delivery, color = sub, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(row.timeText, color = sub, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    )
}