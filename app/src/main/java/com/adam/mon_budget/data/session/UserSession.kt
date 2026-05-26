package com.adam.mon_budget.data.session

object UserSession {
    var currentUserId: Long = -1L
    var currentUserNom: String = ""
    var currentUserPrenom: String = ""
    var currentUserEmail: String = ""
    var currentUserBudget: Double = 0.0

    fun isLoggedIn() = currentUserId != -1L

    fun clear() {
        currentUserId = -1L
        currentUserNom = ""
        currentUserPrenom = ""
        currentUserEmail = ""
        currentUserBudget = 0.0
    }
}
