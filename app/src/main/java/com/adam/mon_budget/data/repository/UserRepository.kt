package com.adam.mon_budget.data.repository

import com.adam.mon_budget.data.database.UserDao
import com.adam.mon_budget.data.model.User

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User): Long = userDao.insertUser(user)

    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    suspend fun getUserById(id: Long): User? = userDao.getUserById(id)

    suspend fun updateUser(user: User) = userDao.updateUser(user)
}
