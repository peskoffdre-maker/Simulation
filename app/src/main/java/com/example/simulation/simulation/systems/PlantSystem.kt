package com.example.simulation.simulation.systems

import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.PlantCooldownComponent
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.PositionComponent
import com.example.simulation.simulation.World
import com.example.simulation.simulation.factories.FoodFactory
import kotlin.reflect.KClass


data class Plant(
    val id: EntityId,
    val position: PositionComponent,
    val age: AgeComponent,
    val cooldown: PlantCooldownComponent
)

class PlantSystem(
    private val world: World,
    private val width: Float,
    private val height: Float
) : System {

    val foodFactory = FoodFactory(world, width, height)

    override fun update() {
        for (entity in world.plantTags) {
            foodFactory.spawnFood(entity)
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


}