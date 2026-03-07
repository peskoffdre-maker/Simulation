package com.example.simulation.simulation.factories

import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.FoodValueComponent
import com.example.simulation.simulation.PositionComponent
import com.example.simulation.simulation.SimulationConfiguration.minFoodInitialEnergy
import com.example.simulation.simulation.World
import kotlin.random.Random

class FoodFactory(
    private val world: World,
    private val width: Float,
    private val height: Float
) {
    fun spawnFood(plant: EntityId) {
        val cooldown = world.cooldowns[plant] ?: return
        val position = world.positions[plant] ?: return
        if (cooldown.cooldown > cooldown.maxCooldown) {
            cooldown.cooldown = 0
            val food = world.entityManager.create()

            val offsetX = Random.nextFloat() * 100 - 50
            val offsetY = Random.nextFloat() * 100 - 50

            world.foodTags.add(food)
            world.foodValues[food] = FoodValueComponent(minFoodInitialEnergy)
            world.ages[food] = AgeComponent(0, 360)
            world.positions[food] = PositionComponent(
                (position.x + offsetX).coerceIn(0f, width),
                (position.y + offsetY).coerceIn(0f, height)
            )
        }
    }
}