package com.adam.mon_budget.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adam.mon_budget.data.model.Goal
import com.adam.mon_budget.navigation.Routes
import com.adam.mon_budget.ui.components.BottomNavBar
import com.adam.mon_budget.ui.components.getGoalIcon
import com.adam.mon_budget.ui.theme.Black
import com.adam.mon_budget.ui.theme.GreenDark
import com.adam.mon_budget.ui.theme.GreenPrimary
import com.adam.mon_budget.ui.theme.GreyLight
import com.adam.mon_budget.ui.theme.InterFontFamily
import com.adam.mon_budget.ui.theme.NunitoFontFamily
import com.adam.mon_budget.ui.theme.RedAlert
import com.adam.mon_budget.ui.theme.White
import com.adam.mon_budget.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GoalsScreen(navController: NavController) {
    val vm: GoalViewModel = viewModel()
    val goals by vm.allGoals.collectAsState()
    var goalToDelete by remember { mutableStateOf<Goal?>(null) }

    if (goalToDelete != null) {
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            title = { Text("Supprimer l'objectif ?", fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
            text = { Text("Cette action est irréversible.", fontFamily = InterFontFamily, fontSize = 14.sp, color = Color.Gray) },
            confirmButton = {
                TextButton(onClick = { vm.deleteGoal(goalToDelete!!); goalToDelete = null }) {
                    Text("Supprimer", color = RedAlert, fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { goalToDelete = null }) {
                    Text("Annuler", color = GreenDark, fontFamily = InterFontFamily)
                }
            },
            containerColor = White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    val totalEpargne = goals.sumOf { it.montantActuel }
    val nbAtteints = goals.count { it.montantActuel >= it.montantCible }

    Scaffold(
        containerColor = GreenPrimary,
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.ADD_GOAL) },
                containerColor = GreenDark,
                contentColor = White,
                shape = CircleShape,
                modifier = Modifier.size(58.dp)
            ) {
                Icon(Icons.Filled.Add, "Nouvel objectif", modifier = Modifier.size(28.dp))
            }
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(GreenPrimary),
            contentPadding = pad,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            /* ── HEADER ── */
            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(GreenDark)
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Mes Objectifs", fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.Normal, fontSize = 28.sp, color = White)
                        Text("Épargne & projets", fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Normal, fontSize = 13.sp, color = White.copy(alpha = 0.65f))
                    }
                }
            }

            /* ── STATS ── */
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GoalStatCard("Total épargné", "${String.format("%.0f", totalEpargne)} €", Modifier.weight(1f))
                    GoalStatCard("Atteints", "$nbAtteints / ${goals.size}", Modifier.weight(1f), valueColor = GreenDark)
                }
            }

            /* ── LISTE ou VIDE ── */
            if (goals.isEmpty()) {
                item { GoalsEmpty() }
            } else {
                items(goals, key = { it.id }) { goal ->
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                        GoalCard(goal = goal, onDelete = { goalToDelete = goal })
                    }
                }
            }

            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

/* ── Sous-composants ── */

@Composable
private fun GoalStatCard(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = Black) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(2.dp, Black, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, fontFamily = InterFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, color = Color(0xFF888888))
        Text(value, fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, color = valueColor)
    }
}

@Composable
private fun GoalCard(goal: Goal, onDelete: () -> Unit) {
    val progress = if (goal.montantCible > 0)
        (goal.montantActuel / goal.montantCible).toFloat().coerceIn(0f, 1f) else 0f
    val atteint = goal.montantActuel >= goal.montantCible
    val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date(goal.dateLimite))
    val restant = (goal.montantCible - goal.montantActuel).coerceAtLeast(0.0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(White)
            .border(2.dp, Black, RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Titre + icône + supprimer
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(GreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(getGoalIcon(goal.iconeType), null, tint = GreenDark, modifier = Modifier.size(26.dp))
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(goal.nom, fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp, color = Black)
                Text("Échéance : $dateStr", fontFamily = InterFontFamily, fontWeight = FontWeight.Normal,
                    fontSize = 12.sp, color = Color(0xFF888888))
            }
            if (atteint) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        .background(GreenDark).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("✓ Atteint", fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp, color = White)
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Filled.Delete, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(18.dp))
            }
        }

        // Montants
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Épargné", fontFamily = InterFontFamily, fontSize = 11.sp, color = Color(0xFF888888))
                Text("${String.format("%.2f", goal.montantActuel)} €",
                    fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = GreenDark)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Objectif", fontFamily = InterFontFamily, fontSize = 11.sp, color = Color(0xFF888888))
                Text("${String.format("%.2f", goal.montantCible)} €",
                    fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Black)
            }
        }

        // Barre de progression
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                color = if (atteint) GreenDark else GreenDark,
                trackColor = GreyLight
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${(progress * 100).toInt()}% complété",
                    fontFamily = InterFontFamily, fontSize = 11.sp, color = Color(0xFF888888))
                if (!atteint) Text("Il manque ${String.format("%.2f", restant)} €",
                    fontFamily = InterFontFamily, fontSize = 11.sp, color = Color(0xFF888888))
            }
        }
    }
}

@Composable
private fun GoalsEmpty() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp)).background(White)
            .border(2.dp, Black, RoundedCornerShape(20.dp)).padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier.size(68.dp).clip(CircleShape).background(GreenPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.EmojiEvents, null, tint = GreenDark, modifier = Modifier.size(34.dp))
        }
        Text("Aucun objectif", fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp, color = Black)
        Text("Appuie sur + pour créer\nton premier objectif d'épargne.",
            fontFamily = InterFontFamily, fontWeight = FontWeight.Normal,
            fontSize = 13.sp, color = Color(0xFF888888))
    }
}
