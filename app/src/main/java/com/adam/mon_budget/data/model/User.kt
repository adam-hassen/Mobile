package com.adam.mon_budget.data.model

data class User(
    val id: Long = 0,
    val nom: String = "",
    val prenom: String = "",
    val email: String = "",
    val budgetMensuel: Double = 0.0,
    val dateInscription: Long = System.currentTimeMillis()
)
