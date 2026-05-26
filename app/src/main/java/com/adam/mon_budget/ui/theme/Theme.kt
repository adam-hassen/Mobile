package com.adam.mon_budget.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MonBudgetColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = White,
    background = GreenPrimary,
    surface = GreenPrimary,
    error = RedAlert,
)

@Composable
fun MonBudgetTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MonBudgetColorScheme,
        typography = Typography,
        content = content
    )
}
