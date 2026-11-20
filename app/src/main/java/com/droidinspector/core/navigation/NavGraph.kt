package com.droidinspector.core.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.droidinspector.feature.about.AboutScreen
import com.droidinspector.feature.about.AboutViewModel
import com.droidinspector.feature.applications.AppManagerScreen
import com.droidinspector.feature.applications.AppManagerViewModel
import com.droidinspector.feature.battery.BatteryScreen
import com.droidinspector.feature.battery.BatteryViewModel
import com.droidinspector.feature.dashboard.DashboardScreen
import com.droidinspector.feature.dashboard.DashboardViewModel
import com.droidinspector.feature.hardware.HardwareScreen
import com.droidinspector.feature.hardware.HardwareViewModel
import com.droidinspector.feature.reports.ReportsScreen
import com.droidinspector.feature.reports.ReportsViewModel
import com.droidinspector.feature.sensors.SensorLabScreen
import com.droidinspector.feature.sensors.SensorLabViewModel
import com.droidinspector.feature.storage.StorageScreen
import com.droidinspector.feature.storage.StorageViewModel
import com.droidinspector.feature.system.SystemScreen
import com.droidinspector.feature.system.SystemViewModel

/**
 * Graphe de navigation complet reliant tous les modules de Droid Inspector X.
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
        modifier = modifier,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable(Screen.Dashboard.route) {
            val viewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(viewModel = viewModel, navController = navController)
        }
        composable(Screen.Hardware.route) {
            val viewModel: HardwareViewModel = hiltViewModel()
            HardwareScreen(viewModel = viewModel, navController = navController)
        }
        composable(Screen.Battery.route) {
            val viewModel: BatteryViewModel = hiltViewModel()
            BatteryScreen(viewModel = viewModel, navController = navController)
        }
        composable(Screen.Sensors.route) {
            val viewModel: SensorLabViewModel = hiltViewModel()
            SensorLabScreen(viewModel = viewModel, navController = navController)
        }
        composable(Screen.Applications.route) {
            val viewModel: AppManagerViewModel = hiltViewModel()
            AppManagerScreen(viewModel = viewModel, navController = navController)
        }
        composable(Screen.Storage.route) {
            val viewModel: StorageViewModel = hiltViewModel()
            StorageScreen(viewModel = viewModel, navController = navController)
        }
        composable(Screen.System.route) {
            val viewModel: SystemViewModel = hiltViewModel()
            SystemScreen(viewModel = viewModel, navController = navController)
        }
        composable(Screen.Reports.route) {
            val viewModel: ReportsViewModel = hiltViewModel()
            ReportsScreen(viewModel = viewModel, navController = navController)
        }
        composable(Screen.About.route) {
            val viewModel: AboutViewModel = hiltViewModel()
            AboutScreen(viewModel = viewModel, navController = navController)
        }
    }
}
