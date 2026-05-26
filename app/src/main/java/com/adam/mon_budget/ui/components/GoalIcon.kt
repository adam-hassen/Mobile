package com.adam.mon_budget.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector

val goalIconOptions = listOf(
    "VOYAGE"       to Icons.Filled.FlightTakeoff,
    "VOITURE"      to Icons.Filled.DirectionsCar,
    "MAISON"       to Icons.Filled.Home,
    "FORMATION"    to Icons.Filled.School,
    "SANTE"        to Icons.Filled.Favorite,
    "ELECTRONIQUE" to Icons.Filled.Laptop,
    "AUTRE"        to Icons.Filled.EmojiEvents,
)

fun getGoalIcon(iconeType: String): ImageVector =
    goalIconOptions.find { it.first == iconeType }?.second ?: Icons.Filled.EmojiEvents
