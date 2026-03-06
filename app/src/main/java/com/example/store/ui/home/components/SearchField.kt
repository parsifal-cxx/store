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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.store.R

/** Поле поиска Home. Дата: 06.03.2026, Автор: Бубнов Никита */
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
        textStyle = TextStyle(fontSize = 12.sp, color = text), // <--- Уменьшили шрифт ввода
        placeholder = {
            Text(
                text = stringResource(R.string.search_hint),
                style = TextStyle(fontSize = 12.sp, color = hint) // <--- Уменьшили плейсхолдер
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                tint = hint,
                modifier = Modifier.size(18.dp)
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