package com.adam.mon_budget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adam.mon_budget.data.model.Category
import com.adam.mon_budget.ui.theme.Black
import com.adam.mon_budget.ui.theme.GreenDark
import com.adam.mon_budget.ui.theme.InterFontFamily
import com.adam.mon_budget.ui.theme.White

fun getCategoryIcon(category: Category): ImageVector {
    return when (category) {
        Category.TRANSPORT -> Icons.Filled.DirectionsCar
        Category.LOISIRS -> Icons.Filled.SportsEsports
        Category.SHOPPING -> Icons.Filled.ShoppingCart
        Category.LOGEMENT -> Icons.Filled.Home
        Category.NOURRITURE -> Icons.Filled.Restaurant
        Category.AUTRE -> Icons.Filled.MoreHoriz
    }
}

fun getCategoryIconByName(categorie: String): ImageVector {
    val cat = Category.entries.find { it.name == categorie } ?: Category.AUTRE
    return getCategoryIcon(cat)
}

@Composable
fun CategoryIcon(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) GreenDark else White
    val contentColor = if (isSelected) White else Black

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(bgColor)
            .border(2.dp, Black, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = getCategoryIcon(category),
            contentDescription = category.libelle,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = category.libelle,
            color = contentColor,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp
        )
    }
}
