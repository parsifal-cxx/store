package com.example.store.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    vm: HomeViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    HomeContent(
        state = state,
        onCategoryClick = { vm.selectCategory(it) },
        onDismissError = { vm.dismissError() }
    )
}

/** UI Home. Дата: 05.03.2026, Автор: Бубнов Никита */
@Composable
fun HomeContent(
    state: HomeViewModel.UiState,
    onCategoryClick: (String?) -> Unit,
    onDismissError: () -> Unit
) {
    val bg = colorResource(R.color.brand_background)
    val block = colorResource(R.color.brand_block)
    val text = colorResource(R.color.brand_text)
    val subText = colorResource(R.color.brand_sub_text_dark)
    val accent = colorResource(R.color.brand_accent)

    Box(Modifier.fillMaxSize().background(bg)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 64.dp, bottom = 110.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.home_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = text,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(22.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
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
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.categories) { c ->
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

                Spacer(Modifier.height(18.dp))
            }

            item {
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
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(state.products) { p ->
                        ProductCard(product = p)
                    }
                }
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