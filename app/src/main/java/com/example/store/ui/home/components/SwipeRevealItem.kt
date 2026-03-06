package com.example.store.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Свайп с ограничением (Reveal). Дата: 06.03.2026, Автор: Бубнов Никита */
@Composable
fun SwipeRevealItem(
    modifier: Modifier = Modifier,
    onRevealRight: () -> Unit = {},
    onRevealLeft: () -> Unit = {},
    content: @Composable () -> Unit,
    backgroundRight: @Composable () -> Unit,
    backgroundLeft: @Composable () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var width by remember { mutableStateOf(0f) }

    val revealThreshold = 180f // ширина открываемых кнопок

    Box(modifier = modifier.onSizeChanged { width = it.width.toFloat() }) {
        // Фон (кнопки)
        if (offsetX.value > 0) {
            backgroundRight()
        } else if (offsetX.value < 0) {
            backgroundLeft()
        }

        // Передний контент
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        scope.launch {
                            val newValue = (offsetX.value + delta).coerceIn(-revealThreshold, revealThreshold)
                            offsetX.snapTo(newValue)
                        }
                    },
                    onDragStopped = {
                        val target = if (offsetX.value > revealThreshold / 2) revealThreshold
                        else if (offsetX.value < -revealThreshold / 2) -revealThreshold
                        else 0f
                        scope.launch { offsetX.animateTo(target) }
                    }
                )
        ) {
            content()
        }
    }
}