package com.example.simulation.simulation.factories

import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.CooldownComponent
import com.example.simulation.simulation.PositionComponent
import com.example.simulation.simulation.SimulationConfiguration.maxPlantCooldown
import com.example.simulation.simulation.SimulationConfiguration.maxPlantInitialMaxAge
import com.example.simulation.simulation.SimulationConfiguration.minPlantInitialMaxAge
import com.example.simulation.simulation.World
import kotlin.random.Random

class PlantFactory(
    private val world: World,
    private val width: Float,
    private val height: Float
) {
    fun spawnPlant() {
        val entity = world.entityManager.create()

        world.positions[entity] = PositionComponent(
            Random.nextFloat() * width,
            Random.nextFloat() * height
        )

        world.ages[entity] = AgeComponent(
            age = 0,
            maxAge = Random.nextInt(
                minPlantInitialMaxAge,
                maxPlantInitialMaxAge)
        )

        world.cooldowns[entity] = CooldownComponent(maxPlantCooldown, 0)

        world.plantTags.add(entity)
    }
}