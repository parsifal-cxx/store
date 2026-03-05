package com.example.store.ui.onboard

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.store.R
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/** Экран онбординга. Дата: 04.03.2026, Автор: Бубнов Никита */
@Composable
fun OnboardScreen(
    onFinished: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardPageData(
            imageRes = R.drawable.img_onboard_1_shoe,
            titleRes = R.string.onboard_1_title,
            subtitleRes = null
        ),
        OnboardPageData(
            imageRes = R.drawable.img_onboard_2_shoe,
            titleRes = R.string.onboard_2_title,
            subtitleRes = R.string.onboard_2_subtitle
        ),
        OnboardPageData(
            imageRes = R.drawable.img_onboard_3_shoe,
            titleRes = R.string.onboard_3_title,
            subtitleRes = R.string.onboard_3_subtitle
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    val bgTop = colorResource(R.color.brand_accent)
    val bgBottom = colorResource(R.color.brand_disable)
    val bgBrush = Brush.verticalGradient(listOf(bgTop, bgBottom))

    val btnBg = colorResource(R.color.brand_block)
    val btnText = colorResource(R.color.brand_hint)

    val horizontalPadding = 24.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val offset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            OnboardPage(
                pageIndex = page,
                data = pages[page],
                pageOffset = offset,
                horizontalPadding = horizontalPadding
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = horizontalPadding)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PagerIndicator(current = pagerState.currentPage, count = pages.size)

            Spacer(Modifier.height(22.dp))

            val buttonText = if (pagerState.currentPage == 0) {
                stringResource(R.string.onboard_btn_start)
            } else {
                stringResource(R.string.onboard_btn_next)
            }

            Button(
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage < pages.lastIndex) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            onFinished()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = btnBg,
                    contentColor = btnText,
                    disabledContainerColor = btnBg,
                    disabledContentColor = btnText
                )
            ) {
                Text(text = buttonText, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

private data class OnboardPageData(
    @DrawableRes val imageRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int?
)

@Composable
private fun OnboardPage(
    pageIndex: Int,
    data: OnboardPageData,
    pageOffset: Float,
    horizontalPadding: Dp
) {
    val abs = pageOffset.absoluteValue
    val alpha = (1f - abs).coerceIn(0f, 1f)

    val titleColor = colorResource(R.color.brand_block)
    val subtitleColor = colorResource(R.color.brand_block).copy(alpha = 0.65f)

    val topPadding = if (pageIndex == 0) 96.dp else 120.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (pageIndex == 0) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnboardTextBlock(
                    titleRes = data.titleRes,
                    subtitleRes = data.subtitleRes,
                    titleStyle = MaterialTheme.typography.displayMedium,
                    subtitleStyle = MaterialTheme.typography.labelSmall,
                    alpha = alpha,
                    absOffset = abs,
                    titleColor = titleColor,
                    subtitleColor = subtitleColor
                )
            }

            Spacer(Modifier.height(44.dp))

            OnboardImage(
                imageRes = data.imageRes,
                alpha = alpha,
                pageOffset = pageOffset,
                absOffset = abs,
                horizontalPadding = 0.dp,
                height = 340.dp,
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth,
                extraDownOffset = 64.dp
            )
        } else {
            OnboardImage(
                imageRes = data.imageRes,
                alpha = alpha,
                pageOffset = pageOffset,
                absOffset = abs,
                horizontalPadding = horizontalPadding,
                height = 300.dp,
                alignment = Alignment.Center,
                contentScale = ContentScale.Fit,
                extraDownOffset = 18.dp
            )

            Spacer(Modifier.height(28.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnboardTextBlock(
                    titleRes = data.titleRes,
                    subtitleRes = data.subtitleRes,
                    titleStyle = MaterialTheme.typography.displaySmall,
                    subtitleStyle = MaterialTheme.typography.labelSmall,
                    alpha = alpha,
                    absOffset = abs,
                    titleColor = titleColor,
                    subtitleColor = subtitleColor,
                    extraDownOffset = 16.dp
                )
            }
        }
    }
}

@Composable
private fun OnboardImage(
    @DrawableRes imageRes: Int,
    alpha: Float,
    pageOffset: Float,
    absOffset: Float,
    horizontalPadding: Dp,
    height: Dp,
    alignment: Alignment,
    contentScale: ContentScale,
    extraDownOffset: Dp
) {
    Image(
        painter = painterResource(imageRes),
        contentDescription = null,
        alignment = alignment,
        contentScale = contentScale,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .height(height)
            .offset(y = extraDownOffset)
            .graphicsLayer {
                this.alpha = alpha
                translationX = -pageOffset * 80f
                translationY = absOffset * 20f
            }
    )
}

@Composable
private fun OnboardTextBlock(
    @StringRes titleRes: Int,
    @StringRes subtitleRes: Int?,
    titleStyle: androidx.compose.ui.text.TextStyle,
    subtitleStyle: androidx.compose.ui.text.TextStyle,
    alpha: Float,
    absOffset: Float,
    titleColor: androidx.compose.ui.graphics.Color,
    subtitleColor: androidx.compose.ui.graphics.Color,
    extraDownOffset: Dp = 0.dp
) {
    Text(
        text = stringResource(titleRes),
        style = titleStyle,
        color = titleColor,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .offset(y = extraDownOffset)
            .graphicsLayer {
                this.alpha = alpha
                translationY = absOffset * 24f
            }
    )

    if (subtitleRes != null) {
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(subtitleRes),
            style = subtitleStyle,
            color = subtitleColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .offset(y = extraDownOffset)
                .graphicsLayer {
                    this.alpha = alpha
                    translationY = absOffset * 24f
                }
        )
    }
}

@Composable
private fun PagerIndicator(current: Int, count: Int) {
    val active = colorResource(R.color.brand_block)
    val inactive = colorResource(R.color.brand_block).copy(alpha = 0.35f)

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(count) { index ->
            val w = if (index == current) 28.dp else 10.dp
            val c = if (index == current) active else inactive
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .width(w)
                    .background(c, RoundedCornerShape(10.dp))
            )
        }
    }
}