package com.example.simulation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simulation.screens.SimulationScreen
import com.example.simulation.screens.StatisticsScreen
import com.example.simulation.simViewModel.SimulationViewModel

/**
 * Class with navigation routes
 */
sealed class Screen (val route: String) {
    object SimulationScreen: Screen("simulationScreen")
    object StatisticsScreen: Screen("statisticsScreen")
}


/**
 * Composable responsible for navigation between different screens
 */
@Composable
fun Nav (modifier: Modifier) {
    val navController = rememberNavController()
    val viewModel: SimulationViewModel = viewModel() // creates viewModel, that is shared between screens

    SideDrawer(navController) {
        NavHost(
            navController = navController,
            startDestination = Screen.SimulationScreen.route
        ) {
            composable(Screen.SimulationScreen.route) {
                SimulationScreen(viewModel = viewModel, modifier = modifier)
            }

            composable(Screen.StatisticsScreen.route) {
                StatisticsScreen(viewModel = viewModel, modifier = modifier)
            }
        }
    }
}