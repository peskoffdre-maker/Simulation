package com.example.simulation.simulation.systems

import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.World
import com.example.simulation.simulation.factories.FoodFactory
import kotlin.reflect.KClass

class FoodSystem(
): System {

    override fun update(world: World, delta: Int) {
        for (food in world.foodTags) {
            updateFood(food, world, delta)
        }
    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            AgeComponent::class,
        )
    }

    override fun writes(): List<KClass<*>> {
        return listOf(
            AgeComponent::class,
        )
    }

    private fun updateFood(food: EntityId, world: World, delta: Int) {
        world.ages[food]?.age += delta
    }
}

