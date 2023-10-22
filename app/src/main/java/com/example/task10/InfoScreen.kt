package com.example.task10

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp


@Composable
fun InfoScreen() {
    val context = LocalContext.current

    ScreenPattern(
        { ScreenContent() }, context)
}



@Composable
fun InfoScreenContent() {
    Box(
        modifier = Modifier.padding(PaddingValues(25.dp, 85.dp))
    ) {
        Text(
            text = "Сделал Фёдор Шмаков\nИКБО-06-21"
        )
    }
}