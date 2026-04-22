package com.example.simulation.simulation.factories

import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.FoodEnergyComponent
import com.example.simulation.simulation.PositionComponent
import com.example.simulation.simulation.SimulationConfiguration.foodMaxAge
import com.example.simulation.simulation.SimulationConfiguration.minFoodInitialEnergy
import com.example.simulation.simulation.World
import kotlin.random.Random


/**
 * Food factory creates new food instances around a given plant.
 */
class FoodFactory(
    private val world: World,
    private val width: Float,
    private val height: Float
) {
    private val rng = world.rng
    // Spawns food around a plant, resets cooldown.
    fun spawnFood(plant: EntityId) {
        val cooldown = world.cooldowns[plant] ?: return
        val position = world.positions[plant] ?: return
        if (cooldown.cooldown <= 0) {
            cooldown.cooldown = cooldown.maxCooldown
            val food = world.entityManager.create()

            val offsetX = rng.nextFloat() * 100 - 50
            val offsetY = rng.nextFloat() * 100 - 50

            world.foodTags.add(food)
            world.foodValues[food] = FoodEnergyComponent(minFoodInitialEnergy)
            world.ages[food] = AgeComponent(0, maxAge = foodMaxAge)
            world.positions[food] = PositionComponent(
                (position.x + offsetX).coerceIn(0f, width),
                (position.y + offsetY).coerceIn(0f, height)
            )
        }
    }
}