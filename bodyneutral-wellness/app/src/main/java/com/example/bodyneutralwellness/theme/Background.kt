package com.example.bodyneutralwellness.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun WellnessBackgroundBrush(): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            DawnBlush,
            CreamWhite,
            SoftMint,
            MistBlue.copy(alpha = 0.72f)
        )
    )
}

val AppChromeColor: Color = CreamWhite
