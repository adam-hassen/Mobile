package com.adam.mon_budget.ui.dashboard

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adam.mon_budget.data.model.Expense
import com.adam.mon_budget.data.session.UserSession
import com.adam.mon_budget.navigation.Routes
import com.adam.mon_budget.ui.components.BottomNavBar
import com.adam.mon_budget.ui.components.getCategoryIconByName
import com.adam.mon_budget.ui.theme.Black
import com.adam.mon_budget.ui.theme.GreenDark
import com.adam.mon_budget.ui.theme.GreenPrimary
import com.adam.mon_budget.ui.theme.GreyLight
import com.adam.mon_budget.ui.theme.InterFontFamily
import com.adam.mon_budget.ui.theme.NunitoFontFamily
import com.adam.mon_budget.ui.theme.RedAlert
import com.adam.mon_budget.ui.theme.White
import com.adam.mon_budget.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun DashboardScreen(navController: NavController) {
    val vm: DashboardViewModel = viewModel()
    val expenses by vm.recentExpenses.collectAsState()
    val monthlyTotal by vm.monthlyTotal.collectAsState()
    val budget = UserSession.currentUserBudget.takeIf { it > 0.0 } ?: vm.budgetMensuel
    val prenom = UserSession.currentUserPrenom.ifBlank { "vous" }

    val restant = budget - monthlyTotal
    val progress = if (budget > 0) (monthlyTotal / budget).toFloat().coerceIn(0f, 1f) else 0f
    val overBudget = restant < 0
    val nbDepenses = expenses.size
    val pctEconomise = if (budget > 0 && !overBudget) ((restant / budget) * 100).toInt() else 0

    Scaffold(
        containerColor = GreenPrimary,
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.ADD_EXPENSE) },
                containerColor = GreenDark,
                contentColor = White,
                shape = CircleShape,
                modifier = Modifier.size(58.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nouvelle dépense", modifier = Modifier.size(28.dp))
            }
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(GreenPrimary),
            contentPadding = pad,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            /* ── 1. HEADER ── */
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(GreenDark)
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // App name row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MonBudget",
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = GreenPrimary
                            )
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(GreenPrimary)
                                    .border(2.dp, White.copy(alpha = 0.3f), CircleShape)
                                    .clickable { navController.navigate(Routes.PROFILE) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prenom.take(1).uppercase(),
                                    fontFamily = NunitoFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = GreenDark
                                )
                            }
                        }
                        // Greeting
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Bonjour, $prenom 👋",
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 26.sp,
                                color = White
                            )
                            Text(
                                text = SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRANCE)
                                    .format(Date()).replaceFirstChar { it.uppercase() },
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                color = White.copy(alpha = 0.65f)
                            )
                        }
                    }
                }
            }

            /* ── 2. CARTE BUDGET ── */
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(White)
                            .border(2.dp, Black, RoundedCornerShape(24.dp))
                            .padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Titre + badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Budget mensuel",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                color = Color(0xFF888888)
                            )
                            StatusBadge(overBudget, progress)
                        }

                        // Montant principal
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "${if (overBudget) "−" else ""}${String.format("%.2f", abs(restant))} €",
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 42.sp,
                                color = if (overBudget) RedAlert else Black
                            )
                            Text(
                                text = if (overBudget)
                                    "Budget dépassé de ${String.format("%.2f", abs(restant))} €"
                                else
                                    "restants sur ${String.format("%.2f", budget)} €",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                color = Color(0xFF888888)
                            )
                        }

                        // Barre de progression
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                color = if (overBudget) RedAlert else GreenDark,
                                trackColor = GreyLight
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MiniLabel("Dépensé", "${String.format("%.2f", monthlyTotal)} €")
                                MiniLabel("Budget", "${String.format("%.2f", budget)} €", alignEnd = true)
                            }
                        }
                    }
                }
            }

            /* ── 3. STATS RAPIDES ── */
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        icon = Icons.Filled.ReceiptLong,
                        value = "$nbDepenses",
                        label = "Dépenses ce mois",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Filled.EmojiEvents,
                        value = if (!overBudget) "$pctEconomise%" else "—",
                        label = if (!overBudget) "Économisé" else "Budget dépassé",
                        valueColor = if (!overBudget) GreenDark else RedAlert,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(20.dp))
            }

            /* ── 4. TITRE DÉPENSES ── */
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dépenses récentes",
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.sp,
                        color = Black
                    )
                    Row(
                        modifier = Modifier.clickable { navController.navigate(Routes.HISTORY) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tout voir",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = GreenDark
                        )
                        Icon(
                            Icons.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = GreenDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            /* ── 5. LISTE ou VIDE ── */
            if (expenses.isEmpty()) {
                item {
                    EmptyState(
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            } else {
                items(expenses) { expense ->
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                        ExpenseRow(
                            expense = expense,
                            onClick = { navController.navigate(Routes.expenseDetail(expense.id)) }
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

/* ── Sous-composants ────────────────────────────────────────────────────── */

@Composable
private fun StatusBadge(overBudget: Boolean, progress: Float) {
    val bgColor = if (overBudget) RedAlert.copy(alpha = 0.1f) else GreenPrimary.copy(alpha = 0.5f)
    val textColor = if (overBudget) RedAlert else GreenDark
    val text = if (overBudget) "⚠ Dépassé" else "${(progress * 100).toInt()}% utilisé"

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = textColor
        )
    }
}

@Composable
private fun MiniLabel(label: String, value: String, alignEnd: Boolean = false) {
    Column(horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start) {
        Text(
            text = label,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            color = Color(0xFF888888)
        )
        Text(
            text = value,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = Black
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Black
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(White)
            .border(2.dp, Black, RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(GreenPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = GreenDark, modifier = Modifier.size(20.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                text = value,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = valueColor
            )
            Text(
                text = label,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = Color(0xFF888888),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(White)
            .border(2.dp, Black, RoundedCornerShape(20.dp))
            .padding(36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(GreenPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.ReceiptLong,
                contentDescription = null,
                tint = GreenDark,
                modifier = Modifier.size(34.dp)
            )
        }
        Text(
            text = "Aucune dépense",
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = Black
        )
        Text(
            text = "Appuie sur le bouton + pour\nenregistrer ta première dépense.",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = Color(0xFF888888)
        )
    }
}

@Composable
private fun ExpenseRow(expense: Expense, onClick: () -> Unit) {
    val fmt = SimpleDateFormat("dd MMM", Locale.FRANCE)
    val dateStr = fmt.format(Date(expense.date))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(2.dp, Black, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Icône
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(GreenPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getCategoryIconByName(expense.categorie),
                contentDescription = expense.categorie,
                tint = GreenDark,
                modifier = Modifier.size(24.dp)
            )
        }

        // Texte
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = expense.categorie,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Black
            )
            Text(
                text = if (expense.note.isNotBlank()) expense.note else dateStr,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = Color(0xFF888888),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Montant + date
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = "−${String.format("%.2f", expense.montant)} €",
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Black
            )
            if (expense.note.isNotBlank()) {
                Text(
                    text = dateStr,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = Color(0xFF888888)
                )
            }
        }
    }
}
