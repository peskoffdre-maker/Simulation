package com.example.simulation.simulation.systems

import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.SimulationConfiguration.agingPerTick
import com.example.simulation.simulation.World
import kotlin.reflect.KClass

class FoodSystem(private val world: World) : System {

    override fun update() {
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

