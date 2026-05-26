package com.adam.mon_budget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adam.mon_budget.ui.theme.Black
import com.adam.mon_budget.ui.theme.GreyLight
import com.adam.mon_budget.ui.theme.GreenDark
import com.adam.mon_budget.ui.theme.InterFontFamily
import com.adam.mon_budget.ui.theme.NunitoFontFamily
import com.adam.mon_budget.ui.theme.RedAlert
import com.adam.mon_budget.ui.theme.White

@Composable
fun BudgetCard(
    budgetMensuel: Double,
    monthlyTotal: Double,
    modifier: Modifier = Modifier
) {
    val restant = budgetMensuel - monthlyTotal
    val progress = if (budgetMensuel > 0) {
        (monthlyTotal / budgetMensuel).toFloat().coerceIn(0f, 1f)
    } else 0f
    val montantColor = if (restant < 0) RedAlert else Black
    val barColor = if (progress > 0.9f) RedAlert else GreenDark

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(2.dp, Black, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Budget mensuel",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Black
            )
            Text(
                text = "${String.format("%.2f", restant)} € restants",
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = montantColor
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = barColor,
                trackColor = GreyLight
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Dépensé: ${String.format("%.2f", monthlyTotal)} €",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = Black
                )
                Text(
                    text = "Budget: ${String.format("%.2f", budgetMensuel)} €",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = Black
                )
            }
        }
    }
}
