package com.adam.mon_budget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val montant: Double,
    val categorie: String,
    val note: String = "",
    val lieu: String = "",
    val date: Long = System.currentTimeMillis()
)
