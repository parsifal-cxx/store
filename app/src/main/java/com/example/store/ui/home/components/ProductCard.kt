package com.example.store.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.store.R
import com.example.store.ui.home.HomeViewModel

/** Карточка товара. Дата: 06.03.2026, Автор: Бубнов Никита */
@Composable
fun ProductCard(
    product: HomeViewModel.UiProduct,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val block = colorResource(R.color.brand_block)
    val text = colorResource(R.color.brand_text)
    val subText = colorResource(R.color.brand_sub_text_dark)
    val accent = colorResource(R.color.brand_accent)
    val hint = colorResource(R.color.brand_hint)

    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = block
    ) {
        Box(Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier
                    .padding(10.dp)
                    .size(28.dp)
                    .align(Alignment.TopStart),
                shape = CircleShape,
                color = block,
                onClick = onFavoriteClick
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(
                            if (product.isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
                        ),
                        contentDescription = null,
                        tint = if (product.isFavorite) colorResource(R.color.brand_red) else hint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 26.dp)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 12.dp, end = 52.dp, bottom = 12.dp)
            ) {
                if (product.isBestSeller) {
                    Text("BEST SELLER", style = MaterialTheme.typography.labelSmall, color = accent)
                    Spacer(Modifier.height(6.dp))
                }
                Text(product.title, style = MaterialTheme.typography.bodyMedium, color = text)
                Spacer(Modifier.height(6.dp))
                Text(product.price, style = MaterialTheme.typography.labelMedium, color = subText)
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(48.dp)
                    .background(accent, RoundedCornerShape(topStart = 16.dp))
                    .clickable(onClick = onCartClick)
            ) {
                Icon(
                    painter = painterResource(
                        if (product.isInCart) R.drawable.ic_cart_small else R.drawable.ic_plus
                    ),
                    contentDescription = null,
                    tint = block,
                    modifier = Modifier.align(Alignment.Center).size(20.dp)
                )
            }
        }
    }
}