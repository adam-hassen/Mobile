package com.adam.mon_budget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adam.mon_budget.data.database.AppDatabase
import com.adam.mon_budget.data.model.Expense
import com.adam.mon_budget.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ExpenseRepository
    val allExpenses: StateFlow<List<Expense>>
    val monthlyTotal: StateFlow<Double>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ExpenseRepository(db.expenseDao())
        allExpenses = repository.getAllExpenses()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        val startOfMonth = getStartOfMonth()
        monthlyTotal = repository.getMonthlyTotal(startOfMonth)
            .map { it ?: 0.0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    }

    fun addExpense(expense: Expense) = viewModelScope.launch {
        repository.insert(expense)
    }

    fun updateExpense(expense: Expense) = viewModelScope.launch {
        repository.update(expense)
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        repository.delete(expense)
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
