package com.example.simulation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.simulation.simViewModel.SimulationViewModel


// placeholder statistics screen
@Composable
fun StatisticsScreen(
    modifier: Modifier,
    viewModel: SimulationViewModel,
) {
    // collect creatures list from view model
    val population by viewModel.population.collectAsState()
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Population: $population")
    }
}
