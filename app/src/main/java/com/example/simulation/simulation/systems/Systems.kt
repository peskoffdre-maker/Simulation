package com.example.simulation.simulation.systems

import com.example.simulation.simulation.SimulationConfiguration.cellSize
import com.example.simulation.simulation.World
import com.example.simulation.simulation.utils.SpatialQueryService
import kotlin.reflect.KClass

interface System {
    fun update ()
    fun reads() : List<KClass<*>>
    fun writes() : List<KClass<*>>
}

class Systems(
    private val world: World,
    private val width: Float,
    private val height: Float
)
{
    // Must be one instance of spatial service
    private val spatial = SpatialQueryService(
        world = world,
        cellSize = cellSize
    )
    private val _systems = listOf<System>(
        GrowthSystem(world),
        EnergySystem(world),
        StateSystem(world),
        AgeSystem(world),
        BehaviorSystem(world),
        TargetSeekingSystem(world, spatial),
        CollisionSystem(world),
        CombatSystem(world),
        EatingSystem(world),
        PlantSystem(world, this.width, this.height),
        PlantCreationSystem(world, this.width, this.height),
        ReproductionSystem(world, this.width, this.height),
        PhysicsSystem(world, this.width, this.height),
        SpatialIndexSystem(spatial = spatial),
        CooldownSystem(world),
        DeathSystem(world),
        StatisticSystem(world),
    )

    fun getSystems(): List<System> {
        return _systems
    }
}