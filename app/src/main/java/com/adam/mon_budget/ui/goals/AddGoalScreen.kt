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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adam.mon_budget.data.model.Goal
import com.adam.mon_budget.ui.components.PrimaryButton
import com.adam.mon_budget.ui.components.getGoalIcon
import com.adam.mon_budget.ui.components.goalIconOptions
import com.adam.mon_budget.ui.theme.Black
import com.adam.mon_budget.ui.theme.GreenDark
import com.adam.mon_budget.ui.theme.GreenPrimary
import com.adam.mon_budget.ui.theme.InterFontFamily
import com.adam.mon_budget.ui.theme.NunitoFontFamily
import com.adam.mon_budget.ui.theme.RedAlert
import com.adam.mon_budget.ui.theme.White
import com.adam.mon_budget.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(navController: NavController) {
    val vm: GoalViewModel = viewModel()

    var nom by remember { mutableStateOf("") }
    var montantCible by remember { mutableStateOf("") }
    var montantActuel by remember { mutableStateOf("") }
    var selectedIcone by remember { mutableStateOf("VOYAGE") }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorNom by remember { mutableStateOf<String?>(null) }
    var errorMontant by remember { mutableStateOf<String?>(null) }

    // Date par défaut = dans 6 mois
    val defaultDate = Calendar.getInstance().apply { add(Calendar.MONTH, 6) }.timeInMillis
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = defaultDate)
    val selectedDate = datePickerState.selectedDateMillis ?: defaultDate
    val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date(selectedDate))

    // Dialog date picker
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK", color = GreenDark, fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Annuler", color = Color.Gray, fontFamily = InterFontFamily)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenPrimary)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {

        /* ── HEADER ── */
        Box(
            modifier = Modifier.fillMaxWidth()
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
                "Nouvel objectif", fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Normal, fontSize = 20.sp,
                color = White, modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            /* ── ICÔNE (sélecteur) ── */
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionLabel("Icône de l'objectif")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    goalIconOptions.chunked(4).forEach { row ->
                        // 2 rangées de 4 icônes max
                    }
                }
                // Grille d'icônes 4 par rangée
                goalIconOptions.chunked(4).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEach { (key, _) ->
                            val isSel = selectedIcone == key
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSel) GreenDark else White)
                                    .border(2.dp, if (isSel) GreenDark else Black, RoundedCornerShape(14.dp))
                                    .clickable { selectedIcone = key }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    getGoalIcon(key), key,
                                    tint = if (isSel) White else GreenDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }

            /* ── NOM ── */
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel("Nom de l'objectif")
                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it; errorNom = null },
                    placeholder = {
                        Text("Ex : Voyage à Tokyo…", fontFamily = InterFontFamily, fontSize = 14.sp, color = Color.Gray)
                    },
                    isError = errorNom != null,
                    supportingText = if (errorNom != null) {
                        { Text(errorNom!!, fontFamily = InterFontFamily, fontSize = 12.sp, color = RedAlert) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = White, unfocusedContainerColor = White,
                        focusedBorderColor = Black, unfocusedBorderColor = Black,
                        focusedTextColor = Black, unfocusedTextColor = Black,
                        errorContainerColor = White, errorBorderColor = RedAlert,
                    )
                )
            }

            /* ── MONTANTS côte à côte ── */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Montant cible
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionLabel("Montant cible")
                    OutlinedTextField(
                        value = montantCible,
                        onValueChange = { montantCible = it.replace(",", "."); errorMontant = null },
                        placeholder = { Text("0.00", fontFamily = InterFontFamily, fontSize = 14.sp, color = Color.Gray) },
                        prefix = { Text("€", fontFamily = InterFontFamily, fontSize = 14.sp, color = GreenDark) },
                        isError = errorMontant != null,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = White, unfocusedContainerColor = White,
                            focusedBorderColor = Black, unfocusedBorderColor = Black,
                            focusedTextColor = Black, unfocusedTextColor = Black,
                            errorContainerColor = White, errorBorderColor = RedAlert,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                // Déjà épargné
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionLabel("Déjà épargné")
                    OutlinedTextField(
                        value = montantActuel,
                        onValueChange = { montantActuel = it.replace(",", ".") },
                        placeholder = { Text("0.00 (optionnel)", fontFamily = InterFontFamily, fontSize = 12.sp, color = Color.Gray) },
                        prefix = { Text("€", fontFamily = InterFontFamily, fontSize = 14.sp, color = GreenDark) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = White, unfocusedContainerColor = White,
                            focusedBorderColor = Black, unfocusedBorderColor = Black,
                            focusedTextColor = Black, unfocusedTextColor = Black,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (errorMontant != null) {
                Text(errorMontant!!, fontFamily = InterFontFamily, fontSize = 12.sp, color = RedAlert)
            }

            /* ── DATE LIMITE ── */
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel("Date limite")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(White)
                        .border(2.dp, Black, RoundedCornerShape(16.dp))
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            dateStr, fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Black
                        )
                        Icon(Icons.Filled.CalendarToday, null, tint = GreenDark, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            /* ── BOUTON CRÉER ── */
            PrimaryButton(
                text = "Créer l'objectif",
                onClick = {
                    var valid = true
                    if (nom.isBlank()) { errorNom = "Veuillez nommer votre objectif"; valid = false }
                    val cible = montantCible.toDoubleOrNull()
                    if (cible == null || cible <= 0.0) { errorMontant = "Montant cible invalide"; valid = false }
                    if (!valid) return@PrimaryButton
                    val actuel = montantActuel.toDoubleOrNull() ?: 0.0
                    vm.addGoal(Goal(
                        nom = nom.trim(),
                        montantCible = cible!!,
                        montantActuel = actuel,
                        dateLimite = selectedDate,
                        iconeType = selectedIcone
                    ))
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Black)
}
