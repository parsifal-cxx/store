package com.example.store.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.store.R
import com.example.store.ui.home.components.ProductCard
import com.example.store.ui.home.components.SearchField

/** Экран Home. Дата: 05.03.2026, Автор: Бубнов Никита */
@Composable
fun HomeScreen(
    onOpenDetails: (String) -> Unit,
    vm: HomeViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    HomeContent(
        state = state,
        onCategoryClick = vm::selectCategory,
        onToggleFavorite = vm::toggleFavorite,
        onOpenDetails = onOpenDetails,
        onDismissError = vm::dismissError
    )
}

/** UI Home. Дата: 05.03.2026, Автор: Бубнов Никита */
@Composable
fun HomeContent(
    state: HomeViewModel.UiState,
    onCategoryClick: (String?) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenDetails: (String) -> Unit,
    onDismissError: () -> Unit
) {
    val bg = colorResource(R.color.brand_background)
    val block = colorResource(R.color.brand_block)
    val text = colorResource(R.color.brand_text)
    val subText = colorResource(R.color.brand_sub_text_dark)
    val accent = colorResource(R.color.brand_accent)

    Box(Modifier.fillMaxSize().background(bg)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 64.dp, bottom = 110.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.home_title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = text,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(22.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SearchField(modifier = Modifier.weight(1f))

                        Spacer(Modifier.width(14.dp))

                        Surface(
                            modifier = Modifier
                                .size(46.dp)
                                .shadow(6.dp, CircleShape),
                            shape = CircleShape,
                            color = accent
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_filter),
                                    contentDescription = null,
                                    tint = block,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = stringResource(R.string.categories_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = text
                    )

                    Spacer(Modifier.height(12.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        lazyItems(state.categories, key = { it.id ?: "all" }) { c ->
                            val selected = c.id == state.selectedCategoryId
                            val chipBg = if (selected) accent else block
                            val chipText = if (selected) block else subText

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = chipBg,
                                modifier = Modifier.height(40.dp),
                                onClick = { onCategoryClick(c.id) }
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 18.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = c.title,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = chipText
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                }
            }

            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = stringResource(R.string.popular_title),
                            style = MaterialTheme.typography.headlineSmall,
                            color = text,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = stringResource(R.string.all),
                            style = MaterialTheme.typography.labelSmall,
                            color = accent
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        lazyItems(state.bestSellers, key = { it.id }) { p ->
                            ProductCard(
                                product = p,
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(210.dp),
                                onClick = { onOpenDetails(p.id) },
                                onFavoriteClick = { onToggleFavorite(p.id) }
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                }
            }

            item(span = { GridItemSpan(2) }) {
                Text(
                    text = stringResource(R.string.all_products_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = text,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            items(state.allProducts, key = { it.id }) { p ->
                ProductCard(
                    product = p,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(160f / 210f),
                    onClick = { onOpenDetails(p.id) },
                    onFavoriteClick = { onToggleFavorite(p.id) }
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
                    TextButton(onClick = onDismissError) {
                        Text(stringResource(R.string.ok))
                    }
                }
            )
        }
    }
}