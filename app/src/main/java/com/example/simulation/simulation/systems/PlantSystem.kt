package com.example.simulation.simulation.systems

import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.CooldownComponent
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.PositionComponent
import com.example.simulation.simulation.World
import com.example.simulation.simulation.factories.FoodFactory
import kotlin.reflect.KClass


data class Plant(
    val id: EntityId,
    val position: PositionComponent,
    val age: AgeComponent,
    val cooldown: CooldownComponent
)

class PlantSystem(
    private val width: Float,
    private val height: Float
) : System {

    override fun update(world: World, delta: Int) {
        val foodFactory = FoodFactory(
            world,
            width,
            height,
        )
        for (entity in world.plantTags) {
            updatePlant(entity, world, delta)
            foodFactory.spawnFood(entity)
        }
    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            AgeComponent::class,
            CooldownComponent::class,
        )
    }

    override fun writes(): List<KClass<*>> {
        return listOf(
            AgeComponent::class,
            CooldownComponent::class,
        )
    }

    private fun updatePlant(plant: EntityId, world: World, delta: Int) {
        world.ages[plant]?.age += 0
        world.cooldowns[plant]?.cooldown += delta
    }


}