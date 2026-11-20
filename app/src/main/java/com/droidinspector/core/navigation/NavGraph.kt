package com.droidinspector.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.droidinspector.core.components.PlaceholderScreen
import com.droidinspector.feature.dashboard.DashboardScreen
import com.droidinspector.feature.dashboard.DashboardViewModel

/**
 * Graphe de navigation de l'application Droid Inspector X.
 * Seul le Dashboard est actif lors de cette étape, les autres écrans sont des Placeholders.
 * Signature : @Bienvenu
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(
                viewModel = dashboardViewModel,
                modifier = Modifier
            )
        }
        composable(Screen.Hardware.route) {
            PlaceholderScreen(title = Screen.Hardware.title)
        }
        composable(Screen.Battery.route) {
            PlaceholderScreen(title = Screen.Battery.title)
        }
        composable(Screen.Sensors.route) {
            PlaceholderScreen(title = Screen.Sensors.title)
        }
        composable(Screen.Applications.route) {
            PlaceholderScreen(title = Screen.Applications.title)
        }
        composable(Screen.Storage.route) {
            PlaceholderScreen(title = Screen.Storage.title)
        }
        composable(Screen.System.route) {
            PlaceholderScreen(title = Screen.System.title)
        }
        composable(Screen.Reports.route) {
            PlaceholderScreen(title = Screen.Reports.title)
        }
        composable(Screen.About.route) {
            PlaceholderScreen(title = Screen.About.title)
        }
    }
}
