package com.example.store.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.store.R
import com.example.store.ui.home.HomeTab

/** Нижнее меню Home. Дата: 04.03.2026, Автор: Бубнов Никита */
@Composable
fun StoreBottomBar(
    selected: HomeTab,
    onSelect: (HomeTab) -> Unit,
    onBagClick: () -> Unit
) {
    val block = colorResource(R.color.brand_block)
    val active = colorResource(R.color.brand_accent)
    val inactive = colorResource(R.color.brand_sub_text_dark)

    BottomAppBar(
        containerColor = block,
        tonalElevation = 6.dp,
        contentPadding = PaddingValues(horizontal = 22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onSelect(HomeTab.Home) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_nav_home),
                    contentDescription = null,
                    tint = if (selected == HomeTab.Home) active else inactive
                )
            }

            IconButton(onClick = { onSelect(HomeTab.Favorites) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_nav_favorite),
                    contentDescription = null,
                    tint = if (selected == HomeTab.Favorites) active else inactive
                )
            }

            Spacer(Modifier.weight(1f))

            IconButton(onClick = { onSelect(HomeTab.Orders) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_nav_truck),
                    contentDescription = null,
                    tint = if (selected == HomeTab.Orders) active else inactive
                )
            }

            IconButton(onClick = { onSelect(HomeTab.Profile) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_nav_profile),
                    contentDescription = null,
                    tint = if (selected == HomeTab.Profile) active else inactive
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        FloatingActionButton(
            onClick = onBagClick,
            shape = CircleShape,
            containerColor = active,
            contentColor = block
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_nav_bag),
                contentDescription = null
            )
        }
    }
}