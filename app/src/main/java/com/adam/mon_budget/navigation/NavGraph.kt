package com.adam.mon_budget.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.adam.mon_budget.ui.auth.LoginScreen
import com.adam.mon_budget.ui.auth.RegisterScreen
import com.adam.mon_budget.ui.dashboard.DashboardScreen
import com.adam.mon_budget.ui.expense.AddExpenseScreen
import com.adam.mon_budget.ui.expense.ExpenseDetailScreen
import com.adam.mon_budget.ui.expense.HistoryScreen
import com.adam.mon_budget.ui.goals.AddGoalScreen
import com.adam.mon_budget.ui.goals.GoalsScreen
import com.adam.mon_budget.ui.profile.ChangePasswordScreen
import com.adam.mon_budget.ui.profile.EditProfileScreen
import com.adam.mon_budget.ui.profile.ProfileScreen
import com.adam.mon_budget.ui.settings.SettingsScreen
import com.adam.mon_budget.viewmodel.AuthViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    // ViewModel partagé entre Login et Register
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(navController = navController)
        }
        composable(Routes.ADD_EXPENSE) {
            AddExpenseScreen(navController = navController)
        }
        composable(
            route = Routes.EXPENSE_DETAIL,
            arguments = listOf(navArgument("expenseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getLong("expenseId") ?: 0L
            ExpenseDetailScreen(navController = navController, expenseId = expenseId)
        }
        composable(Routes.HISTORY) {
            HistoryScreen(navController = navController)
        }
        composable(Routes.GOALS) {
            GoalsScreen(navController = navController)
        }
        composable(Routes.ADD_GOAL) {
            AddGoalScreen(navController = navController)
        }
        composable(Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(navController = navController)
        }
        composable(Routes.CHANGE_PASSWORD) {
            ChangePasswordScreen(navController = navController)
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(navController = navController)
        }
    }
}
