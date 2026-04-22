package com.example.simulation.simulation

import com.example.simulation.simulation.SimulationConfiguration.initialTimeModifier
import com.example.simulation.simulation.SimulationConfiguration.initialZonesCount
import com.example.simulation.simulation.factories.CreatureFactory
import com.example.simulation.simulation.factories.PlantFactory
import com.example.simulation.simulation.factories.ZoneFactory
import com.example.simulation.simulation.statistics.StatisticsService
import com.example.simulation.simulation.systems.Systems
import kotlin.random.Random


enum class CreatureStates {
    IDLE,
    ROAMING,
    HUNGER,
    REPRODUCTION,
    DECAYING,
}
enum class Diets {
    HERBIVORE,
    CARNIVORE,
    OMNIVORE,
}

enum class AgingStages {
    YOUNG,
    ADULT,
    OLD
}

class SimulationEngine(
    private val width: Float,
    private val height: Float,
    private val initialPopulation: Int,
    private val initialPlants: Int,
    val rng: Random = Random.Default,
) {

    private val _world = World(rng)
    val world : World
            get() = _world
    val statisticsService = StatisticsService(_world)

    init {
        val creatureFactory = CreatureFactory(_world, width, height)
        val plantFactory = PlantFactory(_world, width, height)
        val zoneFactory = ZoneFactory(_world, width, height)

        val herbivores = (initialPopulation * 0.9).toInt()
        val carnivores = (initialPopulation * 0.05).toInt()
        val omnivores = (initialPopulation * 0.05).toInt()
        repeat(herbivores) { creatureFactory.spawnInitialCreature(DietComponent(Diets.HERBIVORE)) }
        repeat(carnivores) { creatureFactory.spawnInitialCreature(DietComponent(Diets.CARNIVORE)) }
        repeat(omnivores) { creatureFactory.spawnInitialCreature(DietComponent(Diets.OMNIVORE)) }
        repeat(initialPlants) { plantFactory.spawnPlantAtRandomCoordinates() }
        repeat(initialZonesCount) {zoneFactory.createNewZone()}
    }

    private var delta = initialTimeModifier
    private val systems = Systems(world, width, height).getSystems()

    fun tick() {
        repeat(delta) {
            systems.forEach { system ->
                system.update()
            }

            world.eventBus.clear()
        }
    }

    fun setDelta(value: Int) {
        delta = value
    }
}



