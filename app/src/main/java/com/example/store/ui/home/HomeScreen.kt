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
import com.example.store.R
import com.example.store.ui.home.components.ProductCard
import com.example.store.ui.home.components.PromoCard
import com.example.store.ui.home.components.SearchField

/** Экран Home. Дата: 04.03.2026, Автор: Бубнов Никита */
@Composable
fun HomeScreen() {
    val bg = colorResource(R.color.brand_background)
    val block = colorResource(R.color.brand_block)
    val text = colorResource(R.color.brand_text)
    val subText = colorResource(R.color.brand_sub_text_dark)
    val accent = colorResource(R.color.brand_accent)

    val categories = listOf("Все", "Outdoor", "Tennis")
    var selectedCategory by remember { mutableStateOf(0) }

    val products = listOf(
        HomeProduct(
            imageRes = R.drawable.img_product_1,
            titleRes = R.string.product_name_nike_air_max,
            priceRes = R.string.price_rub_752
        ),
        HomeProduct(
            imageRes = R.drawable.img_product_1,
            titleRes = R.string.product_name_nike_air_max,
            priceRes = R.string.price_rub_752
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
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
                SearchField(
                    modifier = Modifier.weight(1f)
                )

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
                items(categories.size) { index ->
                    val selected = index == selectedCategory
                    val chipBg = if (selected) accent else block
                    val chipText = if (selected) block else subText

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = chipBg,
                        modifier = Modifier.height(40.dp),
                        onClick = { selectedCategory = index }
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = categories[index],
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
                items(products) { p ->
                    ProductCard(
                        product = p
                    )
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
                    text = stringResource(R.string.promo_title),
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
            PromoCard(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** Модель товара для Home. Дата: 04.03.2026, Автор: Бубнов Никита */
data class HomeProduct(
    val imageRes: Int,
    val titleRes: Int,
    val priceRes: Int
)