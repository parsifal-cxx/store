package com.example.store.ui.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.store.R
import com.example.store.ui.home.components.ProductCard

/** Экран Favorite. Дата: 05.03.2026, Автор: Бубнов Никита */
@Composable
fun FavoriteScreen(
    onBackToHome: () -> Unit,
    onOpenDetails: (String) -> Unit,
    vm: FavoriteViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    val bg = colorResource(R.color.brand_background)
    val accent = colorResource(R.color.brand_accent)
    val text = colorResource(R.color.brand_text)

    Box(Modifier.fillMaxSize().background(bg)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 64.dp, bottom = 110.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = colorResource(R.color.brand_block),
                        onClick = onBackToHome
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.ic_back),
                                contentDescription = null,
                                tint = text
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = stringResource(R.string.favorite_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = text
                    )

                    Spacer(Modifier.weight(1f))

                    Icon(
                        painter = painterResource(R.drawable.ic_heart_filled),
                        contentDescription = null,
                        tint = colorResource(R.color.brand_red)
                    )
                }

                Spacer(Modifier.height(14.dp))
            }

            items(state.products, key = { it.id }) { p ->
                ProductCard(
                    product = p,
                    modifier = Modifier.fillMaxWidth().aspectRatio(160f / 210f),
                    onClick = { onOpenDetails(p.id) },
                    onFavoriteClick = { vm.removeFromFavorite(p.id) },
                    onCartClick = { /* функционал корзины в избранном пока не требуется */ }
                )
            }
        }

        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accent)
            }
        }

        state.errorRes?.let { res ->
            AlertDialog(
                onDismissRequest = { },
                title = { Text(stringResource(R.string.error_title)) },
                text = { Text(stringResource(res)) },
                confirmButton = {
                    TextButton(onClick = vm::dismissError) { Text(stringResource(R.string.ok)) }
                }
            )
        }
    }
}