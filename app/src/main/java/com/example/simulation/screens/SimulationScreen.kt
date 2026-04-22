package com.example.simulation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simulation.simViewModel.SimulationStates
import com.example.simulation.simViewModel.SimulationViewModel
import com.example.simulation.simulation.CreatureRenderModel
import com.example.simulation.simulation.Diets


// Screen with running simulation
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulationScreen(
    modifier: Modifier = Modifier,
    viewModel: SimulationViewModel = viewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsState()
    val simulationState by viewModel.simulationState.collectAsState()
    val plantPoints = snapshot.plants.map { Offset(it.x, it.y) }
    val foodPoints = snapshot.foods.map { Offset(it.x, it.y) }

    var canvasWidth by remember { mutableFloatStateOf(0f) }
    var canvasHeight by remember { mutableFloatStateOf(0f) }

    var showControls by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showControls = !showControls }
            ) {
                Text("⚙")
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Full screen canvas
            SimulationCanvas(
                creatures = snapshot.creatures,
                plantPoints = plantPoints,
                foodPoints = foodPoints,
                modifier = Modifier
                    .fillMaxSize(),
                onSizeChanged = { w, h ->
                    canvasWidth = w
                    canvasHeight = h
                }
            )

            // Floating control panel
            var selectedSpeed by remember { mutableIntStateOf(1) }
            val onSpeedChange = {speed: Int -> selectedSpeed = speed}

            if (showControls) {
                SimulationControlsPanel(
                    simulationState = simulationState,
                    viewModel = viewModel,
                    width = canvasWidth,
                    height = canvasHeight,
                    selectedSpeed = selectedSpeed,
                    onSpeedChange = onSpeedChange,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp)
                )
            }

            Text(
                text = "${snapshot.population}",
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }

}

@Composable
fun SimulationCanvas(
    creatures: List<CreatureRenderModel>,
    plantPoints: List<Offset>,
    foodPoints: List<Offset>,
    onSizeChanged: (Float, Float) -> Unit,
    modifier: Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .onSizeChanged { size ->
                onSizeChanged(size.width.toFloat(), size.height.toFloat())
            }
    ) {
        creatures.forEach { creature ->
            if (creature.diet == Diets.HERBIVORE) {
                drawCircle(
                    color = creature.color,
                    radius = creature.size,
                    center = Offset(creature.x, creature.y)
                )
            } else if (creature.diet == Diets.CARNIVORE) {
                drawCircle(
                    color = Color.Red,
                    radius = creature.size,
                    center = Offset(creature.x, creature.y)
                )
            }
        }
        drawPoints(
                points = plantPoints,
        pointMode = PointMode.Points,
        color = Color(0xFF3D251E),
        strokeWidth = 16f,
        cap = StrokeCap.Round
        )

        drawPoints(
            points = foodPoints,
            pointMode = PointMode.Points,
            color = Color.Green,
            strokeWidth = 16f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun SimulationControlsPanel(
    simulationState: SimulationStates,
    viewModel: SimulationViewModel,
    width: Float,
    height: Float,
    selectedSpeed: Int,
    onSpeedChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column (
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            modifier = modifier
                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                onClick = {
                    when(simulationState) {
                        SimulationStates.STOPPED -> viewModel.start(width, height, 100, 100)
                        else -> {
                            viewModel.saveStatistics(context = context)
                            viewModel.stop()
                        }
                    }
                },
            ) { Text(text = when(simulationState) {
                    SimulationStates.STOPPED -> "Start"
                    else -> "Stop"
                }
            ) }
        }
        Row (
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                .border(3.dp, Color.Black, RoundedCornerShape(16.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    when (simulationState) {
                        SimulationStates.RUNNING -> viewModel.pause()
                        SimulationStates.PAUSED -> viewModel.resume()
                        else -> {}
                    }
                },
                enabled = simulationState != SimulationStates.STOPPED
            ) {
                Text(
                    text = when (simulationState) {
                        SimulationStates.RUNNING -> "Pause"
                        SimulationStates.PAUSED -> "Resume"
                        else -> "Pause"
                    }
                )
            }
            val speeds = listOf(1, 2, 3, 4)

            Row {
                speeds.forEach { speed ->
                    Button(
                        onClick = {
                            onSpeedChange(speed)
                            viewModel.changeSimulationSpeed(speed)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedSpeed == speed)
                                Color.Cyan
                            else
                                Color.Blue
                        )
                    ) {
                        Text("${speed}X")
                    }
                }
            }
        }

    }
}

