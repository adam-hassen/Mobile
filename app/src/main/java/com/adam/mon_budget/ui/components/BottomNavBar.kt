package com.adam.mon_budget.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.adam.mon_budget.navigation.Routes
import com.adam.mon_budget.ui.theme.Black
import com.adam.mon_budget.ui.theme.GreenDark
import com.adam.mon_budget.ui.theme.GreenPrimary
import com.adam.mon_budget.ui.theme.InterFontFamily
import com.adam.mon_budget.ui.theme.White

private data class NavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        NavItem("Accueil",    Icons.Filled.Home,         Routes.DASHBOARD),
        NavItem("Historique", Icons.Filled.History,      Routes.HISTORY),
        NavItem("Objectifs",  Icons.Filled.TrackChanges, Routes.GOALS),
        NavItem("Réglages",   Icons.Filled.Settings,     Routes.SETTINGS),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
    ) {
        // Ligne séparatrice en haut
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(Color(0xFFE0E0E0))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) GreenDark else Color(0xFFAAAAAA),
                    animationSpec = tween(200), label = "icon"
                )
                val labelColor by animateColorAsState(
                    targetValue = if (isSelected) GreenDark else Color(0xFFAAAAAA),
                    animationSpec = tween(200), label = "label"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(Routes.DASHBOARD) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Indicateur actif au-dessus de l'icône
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isSelected) GreenDark else Color.Transparent)
                    )

                    Spacer(Modifier.height(4.dp))

                    // Icône
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 26.dp else 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = iconColor,
                            modifier = Modifier.size(if (isSelected) 26.dp else 22.dp)
                        )
                    }

                    // Label
                    Text(
                        text = item.label,
                        fontFamily = InterFontFamily,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 10.sp,
                        color = labelColor
                    )
                }
            }
        }
    }
}
