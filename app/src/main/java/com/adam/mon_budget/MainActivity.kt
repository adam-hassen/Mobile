package com.adam.mon_budget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.adam.mon_budget.navigation.NavGraph
import com.adam.mon_budget.ui.theme.MonBudgetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonBudgetTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
