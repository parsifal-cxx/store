package com.example.store.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.store.R

/** Промо-карточка. Дата: 04.03.2026, Автор: Бубнов Никита */
@Composable
fun PromoCard(
    modifier: Modifier = Modifier
) {
    val block = colorResource(R.color.brand_block)
    val text = colorResource(R.color.brand_text)
    val accent = colorResource(R.color.brand_accent)

    Surface(
        modifier = modifier.height(95.dp),
        shape = RoundedCornerShape(16.dp),
        color = block
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.summer_sale),
                    style = MaterialTheme.typography.labelSmall,
                    color = text
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.off_15),
                    style = MaterialTheme.typography.displaySmall,
                    color = accent
                )
            }

            Image(
                painter = painterResource(R.drawable.img_promo_shoe),
                contentDescription = null,
                modifier = Modifier
                    .height(70.dp)
                    .width(120.dp)
            )
        }
    }
}