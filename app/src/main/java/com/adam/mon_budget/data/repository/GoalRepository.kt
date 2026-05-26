package com.adam.mon_budget.data.repository

import com.adam.mon_budget.data.database.GoalDao
import com.adam.mon_budget.data.model.Goal

class GoalRepository(private val goalDao: GoalDao) {
    fun getAllGoals() = goalDao.getAllGoals()
    suspend fun insert(goal: Goal) = goalDao.insert(goal)
    suspend fun update(goal: Goal) = goalDao.update(goal)
    suspend fun delete(goal: Goal) = goalDao.delete(goal)
}
