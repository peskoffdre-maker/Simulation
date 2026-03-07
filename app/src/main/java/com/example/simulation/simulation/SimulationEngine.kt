package com.example.simulation.simulation

import com.example.simulation.simulation.SimulationConfiguration.initialTimeModifier
import com.example.simulation.simulation.factories.CreatureFactory
import com.example.simulation.simulation.factories.PlantFactory
import com.example.simulation.simulation.statistics.StatisticsService
import com.example.simulation.simulation.systems.Systems


enum class CreatureStates {
    IDLE,
    ROAMING,
    HUNGER,
    REPRODUCTION
}
enum class Diets {
    HERBIVORE,
    CARNIVORE,
    OMNIVORE,
}

class SimulationEngine(
    private val width: Float,
    private val height: Float,
    private val initialPopulation: Int,
    private val initialPlants: Int
) {

    private val _world = World()
    val world = _world
    private val creatureFactory = CreatureFactory(_world, width, height)
    private val plantFactory = PlantFactory(_world, width, height)
    val statisticsService = StatisticsService(_world)

    init {
        repeat(initialPopulation) { creatureFactory.spawnRandomCreature() }
        repeat(initialPlants) { plantFactory.spawnPlant() }

    }

    private var delta = initialTimeModifier
    private val systems = Systems(world, width, height).getSystems()

    fun tick() {
        systems.forEach { it.update(world, delta) }
    }

    fun setDelta(value: Int) {
        delta = value
    }
}



