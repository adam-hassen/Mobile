package com.adam.mon_budget.ui.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
import java.util.Date
import java.util.Locale

@Composable
fun ExpenseDetailScreen(navController: NavController, expenseId: Long) {
    val vm: ExpenseViewModel = viewModel()
    val allExpenses by vm.allExpenses.collectAsState()
    val expense = allExpenses.find { it.id == expenseId }

    var showDeleteDialog by remember { mutableStateOf(false) }

    // Dialogue suppression
    if (showDeleteDialog && expense != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
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
                    vm.deleteExpense(expense)
                    navController.popBackStack()
                }) {
                    Text("Supprimer", color = RedAlert, fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annuler", color = GreenDark, fontFamily = InterFontFamily)
                }
            },
            containerColor = White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenPrimary)
            .verticalScroll(rememberScrollState())
    ) {
        /* ── HEADER ── */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(GreenDark)
                .padding(horizontal = 8.dp, vertical = 20.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(Icons.Filled.ArrowBackIosNew, null, tint = White, modifier = Modifier.size(20.dp))
            }
            Text(
                text = "Détail",
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                color = White,
                modifier = Modifier.align(Alignment.Center)
            )
            if (expense != null) {
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(Icons.Filled.Delete, null, tint = White.copy(alpha = 0.8f), modifier = Modifier.size(22.dp))
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        if (expense == null) {
            // Dépense introuvable
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(White)
                    .border(2.dp, Black, RoundedCornerShape(20.dp))
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Dépense introuvable",
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        } else {
            /* ── HÉRO MONTANT ── */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Grande icône catégorie
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(White)
                        .border(2.dp, Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIconByName(expense.categorie),
                        contentDescription = expense.categorie,
                        tint = GreenDark,
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Catégorie
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(GreenDark)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = expense.categorie,
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = White
                    )
                }

                // Montant principal
                Text(
                    text = "−${String.format("%.2f", expense.montant)} €",
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 48.sp,
                    color = Black
                )
            }

            Spacer(Modifier.height(24.dp))

            /* ── CARTE DÉTAILS ── */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(White)
                    .border(2.dp, Black, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                val dateFormatted = SimpleDateFormat("EEEE d MMMM yyyy · HH:mm", Locale.FRANCE)
                    .format(Date(expense.date))
                    .replaceFirstChar { it.uppercase() }

                DetailRow(
                    icon = Icons.Filled.CalendarToday,
                    label = "Date",
                    value = dateFormatted
                )

                if (expense.note.isNotBlank()) {
                    Divider()
                    DetailRow(
                        icon = Icons.Filled.Notes,
                        label = "Note",
                        value = expense.note
                    )
                }

                if (expense.lieu.isNotBlank()) {
                    Divider()
                    DetailRow(
                        icon = Icons.Filled.LocationOn,
                        label = "Lieu",
                        value = expense.lieu
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

/* ── Sous-composants ── */

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(GreenPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = GreenDark, modifier = Modifier.size(20.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
                fontSize = 14.sp,
                color = Black
            )
        }
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFEEEEEE))
    )
}
