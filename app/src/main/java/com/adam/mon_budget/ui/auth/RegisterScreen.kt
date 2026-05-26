package com.adam.mon_budget.ui.auth

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.adam.mon_budget.navigation.Routes
import com.adam.mon_budget.ui.components.InputField
import com.adam.mon_budget.ui.components.MonBudgetLogo
import com.adam.mon_budget.ui.components.PrimaryButton
import com.adam.mon_budget.ui.theme.Black
import com.adam.mon_budget.ui.theme.GreenDark
import com.adam.mon_budget.ui.theme.GreenPrimary
import com.adam.mon_budget.ui.theme.InterFontFamily
import com.adam.mon_budget.ui.theme.NunitoFontFamily
import com.adam.mon_budget.ui.theme.RedAlert
import com.adam.mon_budget.viewmodel.AuthState
import com.adam.mon_budget.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var motDePasse by remember { mutableStateOf("") }
    var confirmerMotDePasse by remember { mutableStateOf("") }
    var budgetMensuel by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val authState by authViewModel.authState.collectAsState()

    // Réagir aux changements d'état
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                // Après inscription → retour à la connexion
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.REGISTER) { inclusive = true }
                }
                authViewModel.resetState()
            }
            is AuthState.Error -> {
                errorMessage = state.message
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Logo (petit)
            MonBudgetLogo(size = 56.dp)

            Spacer(modifier = Modifier.height(4.dp))

            // Titre
            Text(
                text = "S'inscrire",
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 36.sp,
                color = Black,
                textAlign = TextAlign.Center
            )

            // Message d'erreur global
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = RedAlert,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { errorMessage = null }
                )
            }

            // Nom et Prénom côte à côte
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InputField(
                    value = nom,
                    onValueChange = {
                        nom = it
                        errorMessage = null
                    },
                    placeholder = "Nom",
                    modifier = Modifier.weight(1f)
                )
                InputField(
                    value = prenom,
                    onValueChange = {
                        prenom = it
                        errorMessage = null
                    },
                    placeholder = "Prénom",
                    modifier = Modifier.weight(1f)
                )
            }

            // Email
            InputField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                placeholder = "Adresse e-mail",
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth()
            )

            // Mot de passe (avec œil)
            InputField(
                value = motDePasse,
                onValueChange = {
                    motDePasse = it
                    errorMessage = null
                },
                placeholder = "Mot de passe",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Confirmer mot de passe (avec œil)
            InputField(
                value = confirmerMotDePasse,
                onValueChange = {
                    confirmerMotDePasse = it
                    errorMessage = null
                },
                placeholder = "Confirmer le mot de passe",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Budget mensuel (€ prefix, optionnel)
            InputField(
                value = budgetMensuel,
                onValueChange = {
                    budgetMensuel = it
                    errorMessage = null
                },
                placeholder = "Budget mensuel",
                keyboardType = KeyboardType.Decimal,
                prefix = "€",
                isOptional = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Bouton Créer mon compte
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    color = GreenDark,
                    modifier = Modifier.size(48.dp)
                )
            } else {
                PrimaryButton(
                    text = "Créer mon compte",
                    onClick = {
                        errorMessage = null
                        authViewModel.register(
                            nom = nom,
                            prenom = prenom,
                            email = email,
                            motDePasse = motDePasse,
                            confirmerMotDePasse = confirmerMotDePasse,
                            budgetMensuel = budgetMensuel
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Lien Se connecter
            Text(
                text = "Déjà un compte ?",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = Black,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Se connecter",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = GreenDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    authViewModel.resetState()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
