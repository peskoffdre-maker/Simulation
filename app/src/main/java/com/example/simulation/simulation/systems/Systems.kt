package com.example.simulation.simulation.systems

import com.example.simulation.simulation.SimulationConfiguration.cellSize
import com.example.simulation.simulation.World
import com.example.simulation.simulation.utils.SpatialQueryService
import kotlin.reflect.KClass

interface System {
    fun update (world: World, delta: Int)
    fun reads() : List<KClass<*>>
    fun writes() : List<KClass<*>>
}

class Systems(
    private val world: World,
    private val width: Float,
    private val height: Float)
{
    private val spatial = SpatialQueryService(
        world = world,
        cellSize = cellSize
    )
    private val _systems = listOf<System>(
        CreatureSystem(),
        EnergySystem(),
        StateSystem(),
        BehaviorSystem(),
        FoodSystem(),
        TargetSeekingSystem(spatial),
        EatingSystem(),
        PlantSystem(width, height),
        ReproductionSystem(width, height),
        PhysicsSystem(width, height),
        SpatialIndexSystem(spatial = spatial),
        DeathSystem(),
    )

    fun getSystems(): List<System> {
        return _systems
    }
}