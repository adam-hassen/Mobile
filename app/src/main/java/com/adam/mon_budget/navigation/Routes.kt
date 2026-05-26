package com.adam.mon_budget.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val ADD_EXPENSE = "add_expense"
    const val EXPENSE_DETAIL = "expense_detail/{expenseId}"
    const val HISTORY = "history"
    const val GOALS = "goals"
    const val ADD_GOAL = "add_goal"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val CHANGE_PASSWORD = "change_password"
    const val SETTINGS = "settings"

    fun expenseDetail(expenseId: Long) = "expense_detail/$expenseId"
}
