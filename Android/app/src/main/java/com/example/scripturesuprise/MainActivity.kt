package com.example.scripturesuprise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.scripturesuprise.ui.filters.FiltersScreen
import com.example.scripturesuprise.ui.home.HomeScreen
import com.example.scripturesuprise.ui.settings.SettingsScreen
import com.example.scripturesuprise.ui.theme.ScriptureSupriseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScriptureSupriseTheme {
                ScriptureApp()
            }
        }
    }
}

@Composable
fun ScriptureApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToFilters = { navController.navigate("filters") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("filters") {
            FiltersScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("settings") {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
