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

/** Нижнее меню Home. Дата: 05.03.2026, Автор: Бубнов Никита */
@Composable
fun StoreBottomBar(
    selected: HomeTab,
    onSelect: (HomeTab) -> Unit,
    onBagClick: () -> Unit
) {
    val block = colorResource(R.color.brand_block)
    val active = colorResource(R.color.brand_accent)
    val inactive = colorResource(R.color.brand_sub_text_dark)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
    ) {
        BottomAppBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            containerColor = block,
            tonalElevation = 6.dp,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomBarItem(
                    selected = selected == HomeTab.Home,
                    active = active,
                    inactive = inactive,
                    iconRes = R.drawable.ic_nav_home,
                    onClick = { onSelect(HomeTab.Home) }
                )

                BottomBarItem(
                    selected = selected == HomeTab.Favorites,
                    active = active,
                    inactive = inactive,
                    iconRes = R.drawable.ic_nav_favorite,
                    onClick = { onSelect(HomeTab.Favorites) }
                )

                Spacer(Modifier.width(72.dp))

                BottomBarItem(
                    selected = selected == HomeTab.Orders,
                    active = active,
                    inactive = inactive,
                    iconRes = R.drawable.ic_nav_truck,
                    onClick = { onSelect(HomeTab.Orders) }
                )

                BottomBarItem(
                    selected = selected == HomeTab.Profile,
                    active = active,
                    inactive = inactive,
                    iconRes = R.drawable.ic_nav_profile,
                    onClick = { onSelect(HomeTab.Profile) }
                )
            }
        }

        FloatingActionButton(
            onClick = onBagClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(60.dp)
                .offset(y = 0.dp),
            shape = CircleShape,
            containerColor = active,
            contentColor = block
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_nav_bag),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun RowScope.BottomBarItem(
    selected: Boolean,
    active: androidx.compose.ui.graphics.Color,
    inactive: androidx.compose.ui.graphics.Color,
    iconRes: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = if (selected) active else inactive
            )
        }
    }
}