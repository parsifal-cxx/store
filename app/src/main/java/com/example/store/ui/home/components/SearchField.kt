package com.example.store.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.store.R

/** Поле поиска Home. Дата: 04.03.2026, Автор: Бубнов Никита */
@Composable
fun SearchField(
    modifier: Modifier = Modifier
) {
    val block = colorResource(R.color.brand_block)
    val hint = colorResource(R.color.brand_hint)
    val text = colorResource(R.color.brand_text)

    var value by remember { mutableStateOf("") }

    TextField(
        value = value,
        onValueChange = { value = it },
        modifier = modifier
            .height(46.dp)
            .shadow(6.dp, RoundedCornerShape(14.dp)),
        placeholder = {
            Text(
                text = stringResource(R.string.search_hint),
                style = MaterialTheme.typography.labelSmall,
                color = hint
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                tint = hint
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = block,
            unfocusedContainerColor = block,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = text
        )
    )
}