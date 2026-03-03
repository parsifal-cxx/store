package com.example.store.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.store.R

/** Поле ввода в стиле макета. Дата: 03.03.2026, Автор: Бубнов Никита */
@Composable
fun StoreTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: (@Composable (() -> Unit))? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val labelColor = colorResource(R.color.brand_hint)
    val placeholderColor = colorResource(R.color.brand_hint)
    val cursorColor = colorResource(R.color.brand_text)
    val fieldBg = colorResource(R.color.brand_sub_text_light)
    val textColor = colorResource(R.color.brand_text)

    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = labelColor
    )
    Spacer(Modifier.height(6.dp))

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        textStyle = MaterialTheme.typography.bodySmall.copy(color = textColor),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.labelSmall,
                color = placeholderColor
            )
        },
        singleLine = true,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        shape = RoundedCornerShape(14.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = fieldBg,
            unfocusedContainerColor = fieldBg,
            disabledContainerColor = fieldBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = cursorColor
        )
    )
}