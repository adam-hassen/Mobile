package com.adam.mon_budget.ui.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adam.mon_budget.data.model.Category
import com.adam.mon_budget.data.model.Expense
import com.adam.mon_budget.navigation.Routes
import com.adam.mon_budget.ui.components.BottomNavBar
import com.adam.mon_budget.ui.components.getCategoryIcon
import com.adam.mon_budget.ui.components.getCategoryIconByName
import com.adam.mon_budget.ui.theme.Black
import com.adam.mon_budget.ui.theme.GreenDark
import com.adam.mon_budget.ui.theme.GreenPrimary
import com.adam.mon_budget.ui.theme.InterFontFamily
import com.adam.mon_budget.ui.theme.NunitoFontFamily
import com.adam.mon_budget.ui.theme.RedAlert
import com.adam.mon_budget.ui.theme.White
import com.adam.mon_budget.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(navController: NavController) {
    val vm: ExpenseViewModel = viewModel()
    val allExpenses by vm.allExpenses.collectAsState()
    val monthlyTotal by vm.monthlyTotal.collectAsState()

    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    // Filtrage par catégorie
    val filtered = if (selectedCategory == null) allExpenses
    else allExpenses.filter { it.categorie == selectedCategory!!.name }

    // Groupement par date (Aujourd'hui / Hier / date formatée)
    val grouped = filtered.groupBy { dateLabel(it.date) }

    // Dialogue de confirmation suppression
    if (expenseToDelete != null) {
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = {
                Text(
                    "Supprimer la dépense ?",
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    "Cette action est irréversible.",
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteExpense(expenseToDelete!!)
                    expenseToDelete = null
                }) {
                    Text("Supprimer", color = RedAlert, fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }) {
                    Text("Annuler", color = GreenDark, fontFamily = InterFontFamily)
                }
            },
            containerColor = White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        containerColor = GreenPrimary,
        bottomBar = { BottomNavBar(navController) }
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(GreenPrimary),
            contentPadding = pad
        ) {

            /* ── HEADER ── */
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(GreenDark)
                        .padding(horizontal = 8.dp, vertical = 20.dp)
                ) {
                    IconButton(
                        onClick = { navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.DASHBOARD) { inclusive = false }
                        }},
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(Icons.Filled.ArrowBackIosNew, null, tint = White, modifier = Modifier.size(20.dp))
                    }
                    Text(
                        text = "Historique",
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp,
                        color = White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            /* ── CARTE RÉSUMÉ ── */
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Total dépenses
                    SummaryCard(
                        label = "Total ce mois",
                        value = "${String.format("%.2f", monthlyTotal)} €",
                        valueColor = Black,
                        modifier = Modifier.weight(1f)
                    )
                    // Nombre de dépenses
                    SummaryCard(
                        label = "Transactions",
                        value = "${allExpenses.size}",
                        valueColor = GreenDark,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            /* ── FILTRES CATÉGORIE ── */
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Filtrer par catégorie",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color(0xFF444444)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Chip "Tout"
                        FilterChip(
                            label = "Tout",
                            isSelected = selectedCategory == null,
                            onClick = { selectedCategory = null }
                        )
                        // Chips catégories
                        Category.entries.forEach { cat ->
                            FilterChip(
                                label = cat.libelle,
                                icon = {
                                    Icon(
                                        getCategoryIcon(cat),
                                        null,
                                        tint = if (selectedCategory == cat) White else GreenDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                isSelected = selectedCategory == cat,
                                onClick = {
                                    selectedCategory = if (selectedCategory == cat) null else cat
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            /* ── LISTE GROUPÉE PAR DATE ── */
            if (filtered.isEmpty()) {
                item { EmptyHistory() }
            } else {
                grouped.forEach { (dateStr, expenses) ->
                    // En-tête de date
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = dateStr,
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Color(0xFF444444)
                            )
                            // Ligne séparatrice
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(Color(0xFFCCCCCC))
                            )
                            // Sous-total du groupe
                            val sousTotal = expenses.sumOf { it.montant }
                            Text(
                                text = "−${String.format("%.2f", sousTotal)} €",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Black
                            )
                        }
                    }
                    // Dépenses du groupe
                    items(expenses, key = { it.id }) { expense ->
                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                            HistoryExpenseRow(
                                expense = expense,
                                onClick = { navController.navigate(Routes.expenseDetail(expense.id)) },
                                onDelete = { expenseToDelete = expense }
                            )
                        }
                    }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

/* ── Helpers ── */

private fun dateLabel(timestamp: Long): String {
    val cal = Calendar.getInstance()
    val today = Calendar.getInstance()

    cal.timeInMillis = timestamp
    return when {
        isSameDay(cal, today) -> "Aujourd'hui"
        isYesterday(cal, today) -> "Hier"
        else -> SimpleDateFormat("EEEE d MMMM", Locale.FRANCE)
            .format(Date(timestamp))
            .replaceFirstChar { it.uppercase() }
    }
}

private fun isSameDay(a: Calendar, b: Calendar) =
    a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
    a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

private fun isYesterday(a: Calendar, today: Calendar): Boolean {
    val yesterday = today.clone() as Calendar
    yesterday.add(Calendar.DAY_OF_YEAR, -1)
    return isSameDay(a, yesterday)
}

/* ── Sous-composants ── */

@Composable
private fun SummaryCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(2.dp, Black, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = Color(0xFF888888)
        )
        Text(
            text = value,
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            color = valueColor
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) GreenDark else White)
            .border(2.dp, if (isSelected) GreenDark else Black, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (icon != null) icon()
        Text(
            text = label,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = if (isSelected) White else Black
        )
    }
}

@Composable
private fun HistoryExpenseRow(
    expense: Expense,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val timeStr = SimpleDateFormat("HH:mm", Locale.FRANCE).format(Date(expense.date))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(2.dp, Black, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(start = 14.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icône catégorie
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GreenPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getCategoryIconByName(expense.categorie),
                contentDescription = null,
                tint = GreenDark,
                modifier = Modifier.size(22.dp)
            )
        }

        // Texte
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = expense.categorie,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Black
            )
            Text(
                text = if (expense.note.isNotBlank()) expense.note
                       else if (expense.lieu.isNotBlank()) "📍 ${expense.lieu}"
                       else timeStr,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = Color(0xFF888888),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Montant
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "−${String.format("%.2f", expense.montant)} €",
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Black
            )
            Text(
                text = timeStr,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = Color(0xFF888888)
            )
        }

        // Bouton supprimer
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Supprimer",
                tint = Color(0xFFCCCCCC),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun EmptyHistory() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(White)
            .border(2.dp, Black, RoundedCornerShape(20.dp))
            .padding(40.dp),
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
            text = "Aucune dépense ne correspond\nà ce filtre.",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = Color(0xFF888888)
        )
    }
}
