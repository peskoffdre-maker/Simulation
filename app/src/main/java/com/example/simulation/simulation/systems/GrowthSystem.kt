package com.example.simulation.simulation.systems

import com.example.simulation.simulation.SizeComponent
import com.example.simulation.simulation.World
import kotlin.reflect.KClass


/**
 * Class manages growth of entities, using growthRate property of AgeComponent.
 */
class GrowthSystem(private val world: World) : System {
    override fun update() {

        // Increases the size of a creature
        for (id in world.creatureTags) {
            val size = world.sizes[id] ?: continue
            val growthRate = world.growthRates[id] ?: continue
            if (size.value < size.maxSize) {
                size.value += growthRate.value
            }
        }
    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            SizeComponent::class
        )
    }

    override fun writes(): List<KClass<*>> {
        return listOf(
            SizeComponent::class
        )
    }

}