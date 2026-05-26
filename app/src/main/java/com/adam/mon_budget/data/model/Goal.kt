package com.adam.mon_budget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nom: String,
    val montantCible: Double,
    val montantActuel: Double = 0.0,
    val dateLimite: Long,
    val iconeType: String = "VOYAGE"
)
