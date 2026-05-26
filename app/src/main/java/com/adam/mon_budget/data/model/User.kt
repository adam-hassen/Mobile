package com.adam.mon_budget.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nom: String = "",
    val prenom: String = "",
    val email: String = "",
    val motDePasse: String = "",
    val budgetMensuel: Double = 0.0,
    val dateInscription: Long = System.currentTimeMillis()
)
