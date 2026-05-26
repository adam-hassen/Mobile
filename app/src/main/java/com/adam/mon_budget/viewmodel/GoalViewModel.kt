package com.adam.mon_budget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adam.mon_budget.data.api.GrokService
import com.adam.mon_budget.data.database.AppDatabase
import com.adam.mon_budget.data.model.Goal
import com.adam.mon_budget.data.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AiState {
    object Idle    : AiState()
    object Loading : AiState()
    data class Success(val advice: String) : AiState()
    data class Error(val message: String)  : AiState()
}

class GoalViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GoalRepository
    val allGoals: StateFlow<List<Goal>>

    private val _aiState = MutableStateFlow<AiState>(AiState.Idle)
    val aiState: StateFlow<AiState> = _aiState

    init {
        val db = AppDatabase.getDatabase(application)
        repository = GoalRepository(db.goalDao())
        allGoals = repository.getAllGoals()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun addGoal(goal: Goal)    = viewModelScope.launch { repository.insert(goal) }
    fun updateGoal(goal: Goal) = viewModelScope.launch { repository.update(goal) }
    fun deleteGoal(goal: Goal) = viewModelScope.launch { repository.delete(goal) }

    fun getAiAdvice(goals: List<Goal>) {
        if (_aiState.value is AiState.Loading) return
        viewModelScope.launch {
            _aiState.value = AiState.Loading
            val result = GrokService.getGoalAdvice(goals)
            _aiState.value = result.fold(
                onSuccess = { AiState.Success(it) },
                onFailure = { e -> AiState.Error(e.message ?: "Erreur inconnue. Vérifie ta connexion.") }
            )
        }
    }

    fun resetAi() { _aiState.value = AiState.Idle }
}
