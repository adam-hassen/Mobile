package com.adam.mon_budget.ui.expense

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adam.mon_budget.data.model.Category
import com.adam.mon_budget.data.model.Expense
import com.adam.mon_budget.ui.components.PrimaryButton
import com.adam.mon_budget.ui.components.getCategoryIcon
import com.adam.mon_budget.ui.theme.Black
import com.adam.mon_budget.ui.theme.GreenDark
import com.adam.mon_budget.ui.theme.GreenPrimary
import com.adam.mon_budget.ui.theme.InterFontFamily
import com.adam.mon_budget.ui.theme.NunitoFontFamily
import com.adam.mon_budget.ui.theme.RedAlert
import com.adam.mon_budget.ui.theme.White
import com.adam.mon_budget.viewmodel.ExpenseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun AddExpenseScreen(navController: NavController) {
    val vm: ExpenseViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var montant by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.AUTRE) }
    var note by remember { mutableStateOf("") }
    var lieu by remember { mutableStateOf("") }
    var errorMontant by remember { mutableStateOf<String?>(null) }
    var loadingLocation by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf<String?>(null) }

    // Launcher permission localisation
    val locationPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            loadingLocation = true
            locationError = null
            scope.launch {
                val address = getCurrentAddress(context)
                loadingLocation = false
                if (address != null) lieu = address
                else locationError = "Impossible d'obtenir la position. Activez le GPS."
            }
        } else {
            locationError = "Permission de localisation refusée."
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
                text = "Nouvelle dépense",
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(Modifier.height(28.dp))

        /* ── MONTANT ── */
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Quel montant ?",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = GreenDark
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(White)
                    .border(2.dp, if (errorMontant != null) RedAlert else Black, RoundedCornerShape(20.dp))
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("€", fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal, fontSize = 36.sp, color = GreenDark)
                    OutlinedTextField(
                        value = montant,
                        onValueChange = { montant = it.replace(",", "."); errorMontant = null },
                        placeholder = {
                            Text("0.00", fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal,
                                fontSize = 36.sp, color = Color(0xFFCCCCCC),
                                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        },
                        textStyle = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal,
                            fontSize = 36.sp, color = Black, textAlign = TextAlign.Center),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            if (errorMontant != null) {
                Text(errorMontant!!, fontFamily = InterFontFamily, fontSize = 12.sp, color = RedAlert)
            }
        }

        Spacer(Modifier.height(28.dp))

        /* ── CATÉGORIES ── */
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SectionLabel("Catégorie")
            val categories = Category.entries
            categories.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { cat ->
                        CategoryChip(
                            category = cat,
                            isSelected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(3 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        /* ── NOTE ── */
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionLabel("Note (optionnel)")
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = {
                    Text("Ex : déjeuner avec des amis…", fontFamily = InterFontFamily, fontSize = 14.sp, color = Color.Gray)
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2, maxLines = 3,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = White, unfocusedContainerColor = White,
                    focusedBorderColor = Black, unfocusedBorderColor = Black,
                    focusedTextColor = Black, unfocusedTextColor = Black,
                )
            )
        }

        Spacer(Modifier.height(16.dp))

        /* ── LIEU AVEC GPS + MAP ── */
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionLabel("Lieu (optionnel)")

            // Champ texte lieu
            OutlinedTextField(
                value = lieu,
                onValueChange = { lieu = it; locationError = null },
                placeholder = {
                    Text("Ex : Carrefour, Paris…", fontFamily = InterFontFamily, fontSize = 14.sp, color = Color.Gray)
                },
                leadingIcon = {
                    Icon(Icons.Filled.LocationOn, null, tint = GreenDark, modifier = Modifier.size(20.dp))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = White, unfocusedContainerColor = White,
                    focusedBorderColor = Black, unfocusedBorderColor = Black,
                    focusedTextColor = Black, unfocusedTextColor = Black,
                )
            )

            // Boutons GPS + Ouvrir Map
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Bouton Ma position
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(White)
                        .border(2.dp, Black, RoundedCornerShape(14.dp))
                        .clickable {
                            locationPermLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (loadingLocation) {
                        CircularProgressIndicator(color = GreenDark, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Filled.MyLocation, null, tint = GreenDark, modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = if (loadingLocation) "Localisation…" else "Ma position",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Black
                    )
                }

                // Bouton Ouvrir Maps
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(GreenDark)
                        .border(2.dp, GreenDark, RoundedCornerShape(14.dp))
                        .clickable {
                            val query = if (lieu.isNotBlank()) Uri.encode(lieu) else "map"
                            val uri = Uri.parse("geo:0,0?q=$query")
                            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            // Fallback si Maps pas installé
                            val fallback = Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://maps.google.com/?q=$query"))
                            try { context.startActivity(intent) }
                            catch (e: Exception) { context.startActivity(fallback) }
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Map, null, tint = White, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Ouvrir Maps",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = White
                    )
                }
            }

            // Message d'erreur localisation
            if (locationError != null) {
                Text(locationError!!, fontFamily = InterFontFamily, fontSize = 12.sp, color = RedAlert)
            }
        }

        Spacer(Modifier.height(32.dp))

        /* ── BOUTON ENREGISTRER ── */
        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
            PrimaryButton(
                text = "Enregistrer la dépense",
                onClick = {
                    val d = montant.toDoubleOrNull()
                    if (d == null || d <= 0.0) {
                        errorMontant = "Veuillez saisir un montant valide"
                        return@PrimaryButton
                    }
                    vm.addExpense(Expense(montant = d, categorie = selectedCategory.name,
                        note = note.trim(), lieu = lieu.trim(), date = System.currentTimeMillis()))
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}

/* ── GPS helper (Coroutine, thread IO) ── */
private suspend fun getCurrentAddress(context: Context): String? = withContext(Dispatchers.IO) {
    try {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        var lat = 0.0; var lon = 0.0; var found = false
        for (p in providers) {
            if (lm.isProviderEnabled(p)) {
                @Suppress("MissingPermission")
                val loc = lm.getLastKnownLocation(p)
                if (loc != null) { lat = loc.latitude; lon = loc.longitude; found = true; break }
            }
        }
        if (!found) return@withContext null
        val geocoder = Geocoder(context, Locale.getDefault())
        @Suppress("DEPRECATION")
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        if (!addresses.isNullOrEmpty()) {
            val a = addresses[0]
            buildString {
                if (!a.featureName.isNullOrBlank() && !a.featureName.first().isDigit())
                    append(a.featureName)
                else if (!a.thoroughfare.isNullOrBlank())
                    append(a.thoroughfare)
                if (!a.locality.isNullOrBlank()) {
                    if (isNotEmpty()) append(", ")
                    append(a.locality)
                }
            }.ifBlank { null }
        } else null
    } catch (e: Exception) { null }
}

/* ── Sous-composants ── */

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Black)
}

@Composable
private fun CategoryChip(category: Category, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bg  = if (isSelected) GreenDark else White
    val bd  = if (isSelected) GreenDark else Black
    val txt = if (isSelected) White else Black
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(2.dp, bd, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier.size(38.dp).clip(CircleShape)
                .background(if (isSelected) White.copy(alpha = 0.15f) else GreenPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(getCategoryIcon(category), category.libelle,
                tint = if (isSelected) White else GreenDark, modifier = Modifier.size(20.dp))
        }
        Text(category.libelle, fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp, color = txt, textAlign = TextAlign.Center)
    }
}
