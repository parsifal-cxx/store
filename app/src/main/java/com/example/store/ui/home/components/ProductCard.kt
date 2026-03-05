package com.example.store.ui.home.components

import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.dp
import com.example.store.R
import com.example.store.ui.home.HomeProduct

/** Карточка товара Home. Дата: 04.03.2026, Автор: Бубнов Никита */
@Composable
fun ProductCard(
    product: HomeProduct,
    modifier: Modifier = Modifier
) {
    val block = colorResource(R.color.brand_block)
    val text = colorResource(R.color.brand_text)
    val subText = colorResource(R.color.brand_sub_text_dark)
    val accent = colorResource(R.color.brand_accent)
    val hint = colorResource(R.color.brand_hint)

    Surface(
        modifier = modifier.size(width = 160.dp, height = 210.dp),
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
                color = block
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_heart_outline),
                        contentDescription = null,
                        tint = hint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Image(
                painter = painterResource(product.imageRes),
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
                Text(
                    text = stringResource(R.string.best_seller),
                    style = MaterialTheme.typography.labelSmall,
                    color = accent
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(product.titleRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = text
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(product.priceRes),
                    style = MaterialTheme.typography.labelMedium,
                    color = subText
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(48.dp)
                    .background(accent, RoundedCornerShape(topStart = 16.dp))
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = null,
                    tint = block,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(20.dp)
                )
            }
        }
    }
}