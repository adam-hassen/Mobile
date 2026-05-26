package com.adam.mon_budget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adam.mon_budget.data.database.AppDatabase
import com.adam.mon_budget.data.model.Goal
import com.adam.mon_budget.data.repository.GoalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GoalRepository
    val allGoals: StateFlow<List<Goal>>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = GoalRepository(db.goalDao())
        allGoals = repository.getAllGoals()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun addGoal(goal: Goal) = viewModelScope.launch { repository.insert(goal) }
    fun updateGoal(goal: Goal) = viewModelScope.launch { repository.update(goal) }
    fun deleteGoal(goal: Goal) = viewModelScope.launch { repository.delete(goal) }
}
