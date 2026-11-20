package com.droidinspector.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.droidinspector.core.components.PlaceholderScreen
import com.droidinspector.feature.battery.BatteryScreen
import com.droidinspector.feature.battery.BatteryViewModel
import com.droidinspector.feature.dashboard.DashboardScreen
import com.droidinspector.feature.dashboard.DashboardViewModel
import com.droidinspector.feature.hardware.HardwareScreen
import com.droidinspector.feature.hardware.HardwareViewModel
import com.droidinspector.feature.storage.StorageScreen
import com.droidinspector.feature.storage.StorageViewModel

/**
 * Graphe de navigation de l'application Droid Inspector X.
 * Actifs : Dashboard, Hardware, Batterie et Stockage. Les autres écrans sont encore des Placeholders.
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
            val hardwareViewModel: HardwareViewModel = hiltViewModel()
            HardwareScreen(viewModel = hardwareViewModel)
        }
        composable(Screen.Battery.route) {
            val batteryViewModel: BatteryViewModel = hiltViewModel()
            BatteryScreen(viewModel = batteryViewModel)
        }
        composable(Screen.Sensors.route) {
            PlaceholderScreen(title = Screen.Sensors.title)
        }
        composable(Screen.Applications.route) {
            PlaceholderScreen(title = Screen.Applications.title)
        }
        composable(Screen.Storage.route) {
            val storageViewModel: StorageViewModel = hiltViewModel()
            StorageScreen(viewModel = storageViewModel)
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
