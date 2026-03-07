package com.example.simulation.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SideDrawer(
    navController: NavController,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.65f)
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                val buttons = hashMapOf<String, Screen>(
                    "Statistics" to Screen.StatisticsScreen,
                    "Simulation" to Screen.SimulationScreen
                )

                for ((key, value) in buttons) {
                    NavigationButton(
                        navController,
                        scope,
                        drawerState,
                        key,
                        value
                    )
                }
            }
        }
    ) {
        content()
    }
}

@Composable
private fun NavigationButton(
    navController: NavController,
    scope: CoroutineScope,
    drawerState: DrawerState,
    text: String,
    route: Screen) {
    TextButton(
        onClick = {
            navController.navigate(route.route) {
                launchSingleTop = true
                restoreState = true
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
            }
            scope.launch { drawerState.close() }
        },
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Text(text, fontSize = 20.sp)
    }
}