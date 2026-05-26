package com.adam.mon_budget.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adam.mon_budget.data.database.AppDatabase
import com.adam.mon_budget.data.model.User
import com.adam.mon_budget.data.repository.UserRepository
import com.adam.mon_budget.data.session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository(
        AppDatabase.getDatabase(application).userDao()
    )

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun register(
        nom: String,
        prenom: String,
        email: String,
        motDePasse: String,
        confirmerMotDePasse: String,
        budgetMensuel: String
    ) {
        // — Validations —
        if (nom.isBlank() || prenom.isBlank()) {
            _authState.value = AuthState.Error("Veuillez renseigner votre nom et prénom.")
            return
        }
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Adresse e-mail invalide.")
            return
        }
        if (motDePasse.length < 6) {
            _authState.value = AuthState.Error("Le mot de passe doit contenir au moins 6 caractères.")
            return
        }
        if (motDePasse != confirmerMotDePasse) {
            _authState.value = AuthState.Error("Les mots de passe ne correspondent pas.")
            return
        }

        val budget = budgetMensuel.replace(",", ".").toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val existing = repository.getUserByEmail(email.trim().lowercase())
                if (existing != null) {
                    _authState.value = AuthState.Error("Cette adresse e-mail est déjà utilisée.")
                    return@launch
                }
                val user = User(
                    nom = nom.trim(),
                    prenom = prenom.trim(),
                    email = email.trim().lowercase(),
                    motDePasse = motDePasse,
                    budgetMensuel = budget
                )
                val userId = repository.insertUser(user)
                // Sauvegarder la session
                UserSession.currentUserId = userId
                UserSession.currentUserNom = user.nom
                UserSession.currentUserPrenom = user.prenom
                UserSession.currentUserEmail = user.email
                UserSession.currentUserBudget = budget
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Erreur lors de l'inscription. Réessayez.")
            }
        }
    }

    fun login(email: String, motDePasse: String) {
        if (email.isBlank() || motDePasse.isBlank()) {
            _authState.value = AuthState.Error("Veuillez remplir tous les champs.")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = repository.getUserByEmail(email.trim().lowercase())
            if (user != null && user.motDePasse == motDePasse) {
                UserSession.currentUserId = user.id
                UserSession.currentUserNom = user.nom
                UserSession.currentUserPrenom = user.prenom
                UserSession.currentUserEmail = user.email
                UserSession.currentUserBudget = user.budgetMensuel
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Email ou mot de passe incorrect.")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
