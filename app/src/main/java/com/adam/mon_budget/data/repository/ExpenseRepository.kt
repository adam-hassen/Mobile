package com.adam.mon_budget.data.repository

import com.adam.mon_budget.data.database.ExpenseDao
import com.adam.mon_budget.data.model.Expense

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    fun getAllExpenses() = expenseDao.getAllExpenses()
    fun getExpensesByCategory(category: String) = expenseDao.getExpensesByCategory(category)
    fun getTotalExpenses() = expenseDao.getTotalExpenses()
    fun getMonthlyTotal(startOfMonth: Long) = expenseDao.getMonthlyTotal(startOfMonth)
    suspend fun insert(expense: Expense) = expenseDao.insert(expense)
    suspend fun update(expense: Expense) = expenseDao.update(expense)
    suspend fun delete(expense: Expense) = expenseDao.delete(expense)
}
