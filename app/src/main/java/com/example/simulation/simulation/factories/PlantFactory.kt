package com.example.simulation.simulation.factories

import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.PlantCooldownComponent
import com.example.simulation.simulation.PositionComponent
import com.example.simulation.simulation.SimulationConfiguration.maxPlantCooldown
import com.example.simulation.simulation.SimulationConfiguration.maxPlantInitialMaxAge
import com.example.simulation.simulation.SimulationConfiguration.minPlantInitialMaxAge
import com.example.simulation.simulation.World
import kotlin.random.Random


/**
 * Class creates new plants in at random point or inside a certain zone
 */
class PlantFactory(
    private val world: World,
    private val width: Float,
    private val height: Float
) {

    private val rng = world.rng
    fun spawnPlantAtRandomCoordinates() {
        val entity = world.entityManager.create()

        world.positions[entity] = getRandomPositionComponent()
        world.ages[entity] = getRandomAgeComponent()
        world.cooldowns[entity] = getRandomCooldownComponent()

        world.plantTags.add(entity)
    }

    fun spawnPlantAt(x: Float, y: Float) {
        val entity = world.entityManager.create()

        world.positions[entity] = PositionComponent(x, y)

        world.ages[entity] = getRandomAgeComponent()
        world.cooldowns[entity] = getRandomCooldownComponent()

        world.plantTags.add(entity)
    }

    private fun getRandomPositionComponent() : PositionComponent {
        return PositionComponent(
            rng.nextFloat() * width,
            rng.nextFloat() * height
        )
    }

    private fun getRandomAgeComponent() : AgeComponent {
        return AgeComponent(
            value = 0,
            maxAge = rng.nextInt(
                minPlantInitialMaxAge,
                maxPlantInitialMaxAge)
        )
    }

    private fun getRandomCooldownComponent() : PlantCooldownComponent {
        return PlantCooldownComponent(maxPlantCooldown, rng.nextInt(0,600))
    }
}