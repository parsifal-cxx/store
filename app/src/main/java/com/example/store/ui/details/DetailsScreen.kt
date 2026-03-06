package com.example.store.ui.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.store.R
import kotlinx.coroutines.launch

/** Экран Details. Дата: 06.03.2026, Автор: Бубнов Никита */
@Composable
fun DetailsScreen(
    productId: String,
    onBack: () -> Unit,
    vm: DetailsViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(productId) { vm.load(productId) }

    val accent = colorResource(R.color.brand_accent)

    state.errorRes?.let { res ->
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(stringResource(res)) },
            confirmButton = { TextButton(onClick = vm::dismissError) { Text(stringResource(R.string.ok)) } }
        )
    }

    Box(Modifier.fillMaxSize().background(colorResource(R.color.brand_background))) {
        val p = state.product
        if (p != null) {
            DetailsContent(
                title = p.title,
                category = stringResource(R.string.details_category_mens_shoes),
                price = state.price,
                description = p.description,
                images = state.images,
                isFavorite = state.isFavorite,
                isInCart = state.isInCart,
                onBack = onBack,
                onToggleFavorite = vm::toggleFavorite,
                onToggleCart = vm::toggleCart
            )
        }

        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accent)
            }
        }
    }
}

@Composable
private fun DetailsContent(
    title: String,
    category: String,
    price: String,
    description: String,
    images: List<String>,
    isFavorite: Boolean,
    isInCart: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleCart: () -> Unit
) {
    val bg = colorResource(R.color.brand_background)
    val text = colorResource(R.color.brand_text)
    val sub = colorResource(R.color.brand_sub_text_dark)
    val block = colorResource(R.color.brand_block)
    val accent = colorResource(R.color.brand_accent)

    var expanded by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { maxOf(images.size, 1) })
    val scope = rememberCoroutineScope()

    val topBarTextStyle = MaterialTheme.typography.labelLarge.copy(fontSize = 22.sp)
    val titleStyle = MaterialTheme.typography.headlineLarge.copy(fontSize = 34.sp)
    val categoryStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 18.sp)
    val priceStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 28.sp)
    val descStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 16.sp)
    val linkStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 16.sp)
    val buttonTextStyle = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(64.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = block,
                onClick = onBack
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
                text = stringResource(R.string.details_shop_title),
                style = topBarTextStyle,
                color = text
            )

            Spacer(Modifier.weight(1f))

            Box {
                Icon(
                    painter = painterResource(R.drawable.ic_nav_bag),
                    contentDescription = null,
                    tint = text
                )
                if (isInCart) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(colorResource(R.color.brand_red), CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(title, style = titleStyle, color = text)
        Spacer(Modifier.height(6.dp))
        Text(category, style = categoryStyle, color = sub)
        Spacer(Modifier.height(6.dp))
        Text(price, style = priceStyle, color = text)

        Spacer(Modifier.height(14.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) { index ->
            val url = images.getOrNull(index)
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            itemsIndexed(images) { idx, url ->
                val selected = idx == pagerState.currentPage
                Surface(
                    modifier = Modifier
                        .size(54.dp)
                        .clickable { scope.launch { pagerState.animateScrollToPage(idx) } },
                    shape = RoundedCornerShape(12.dp),
                    color = block,
                    border = if (selected) BorderStroke(2.dp, accent) else null
                ) {
                    AsyncImage(model = url, contentDescription = null, contentScale = ContentScale.Crop)
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = description,
                style = descStyle,
                color = sub,
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = if (expanded) stringResource(R.string.details_hide) else stringResource(R.string.details_more),
            style = linkStyle,
            color = accent,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End)
                .clickable { expanded = !expanded }
        )

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                color = block,
                border = BorderStroke(1.dp, colorResource(R.color.brand_sub_text_light)),
                onClick = onToggleFavorite
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(if (isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline),
                        contentDescription = null,
                        tint = if (isFavorite) colorResource(R.color.brand_red) else colorResource(R.color.brand_hint)
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            val btnText = if (isInCart) "Добавлено" else stringResource(R.string.details_add_to_cart)
            val btnColor = if (isInCart) colorResource(R.color.brand_disable) else accent

            Button(
                onClick = onToggleCart,
                modifier = Modifier
                    .height(52.dp)
                    .weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = btnColor, contentColor = block)
            ) {
                Icon(painterResource(R.drawable.ic_nav_bag), contentDescription = null, tint = block)
                Spacer(Modifier.width(10.dp))
                Text(btnText, style = buttonTextStyle)
            }
        }
    }
}