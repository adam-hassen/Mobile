package com.adam.mon_budget.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adam.mon_budget.data.database.AppDatabase
import com.adam.mon_budget.data.model.Expense
import com.adam.mon_budget.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val expenseRepository: ExpenseRepository
    val recentExpenses: StateFlow<List<Expense>>
    val monthlyTotal: StateFlow<Double>
    var budgetMensuel by mutableStateOf(750.0)

    init {
        val db = AppDatabase.getDatabase(application)
        expenseRepository = ExpenseRepository(db.expenseDao())
        recentExpenses = expenseRepository.getAllExpenses()
            .map { it.take(5) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        val startOfMonth = getStartOfMonth()
        monthlyTotal = expenseRepository.getMonthlyTotal(startOfMonth)
            .map { it ?: 0.0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    }

    private fun getStartOfMonth(): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
