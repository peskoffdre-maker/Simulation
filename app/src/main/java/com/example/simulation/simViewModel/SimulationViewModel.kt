package com.example.simulation.simViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simulation.simulation.RenderSnapshot
import com.example.simulation.simulation.SimulationEngine
import com.example.simulation.simulation.SnapshotBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import kotlin.math.max

class SimulationViewModel: ViewModel() {
    private var engine: SimulationEngine? = null

    // State flow for creatures. UI reacts to change in this flow and recomposes
    private val _snapshot = MutableStateFlow(
        RenderSnapshot(emptyList(), emptyList(), emptyList(),0)
    )
    val snapshot: StateFlow<RenderSnapshot> = _snapshot

    // StateFlow for simulation state.
    private val _simulationState = MutableStateFlow(SimulationStates.STOPPED)
    val simulationState: StateFlow<SimulationStates> = _simulationState
    private val snapshotBuilder = SnapshotBuilder()
    val population: StateFlow<Int> =
        snapshot
            .map { it.population }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Job to pause and resume simulation
    private var simulationJob: Job? = null
    private val frameTime = 16L

    // Runs simulation in 60 FPS
    private suspend fun simulationRun() {
        val startTime = System.currentTimeMillis()
        engine?.tick() ?: return
        _snapshot.value = snapshotBuilder.build(engine!!.world)
        val elapsed = System.currentTimeMillis() - startTime
        delay(max(0, frameTime - elapsed))
    }

    // Function called after pushing start button.
    fun start(width: Float, height: Float, initialPopulation: Int, initialPlants: Int) {
        if (_simulationState.value == SimulationStates.RUNNING) return

        engine = SimulationEngine(width, height, initialPopulation, initialPlants)
        simulationJob?.cancel()
        _simulationState.value = SimulationStates.RUNNING

        simulationJob = viewModelScope.launch {
            _simulationState.value = SimulationStates.RUNNING
            while(isActive) {
                simulationRun()
            }
        }
    }

    fun pause() {
        if (_simulationState.value != SimulationStates.RUNNING) return
        simulationJob?.cancel()
        _simulationState.value = SimulationStates.PAUSED
    }

    fun resume() {
        if (_simulationState.value != SimulationStates.PAUSED) return
        simulationJob?.cancel()

        simulationJob = viewModelScope.launch {
            _simulationState.value = SimulationStates.RUNNING
            while (isActive) {
                simulationRun()
            }
        }
    }

    fun stop() {
        engine?.statisticsService?.printSimulationStateIntoConsole() ?: println("No stats found")
        simulationJob?.cancel()
        simulationJob = null
        engine = null
        _snapshot.value = RenderSnapshot(
            emptyList(),
            emptyList(),
            emptyList(),
            0
        )
        _simulationState.value = SimulationStates.STOPPED
    }

    fun changeSimulationSpeed(value: Int) {
        engine?.setDelta(value) ?: return
    }
}

enum class SimulationStates {
    STOPPED,
    RUNNING,
    PAUSED
}