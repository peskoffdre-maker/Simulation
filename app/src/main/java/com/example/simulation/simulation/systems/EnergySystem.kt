package com.example.simulation.simulation.systems

import com.example.simulation.simulation.EnergyComponent
import com.example.simulation.simulation.HistoryComponent
import com.example.simulation.simulation.StateComponent
import com.example.simulation.simulation.World
import kotlin.reflect.KClass

class EnergySystem() : System {
    override fun update(world: World, delta: Int) {
        for ((id, energy) in world.energies) {
            val state = world.states[id] ?: continue
            val history = world.histories[id] ?: continue

            val cost = energy.energyCost[state.state] ?: 0f
            energy.energy -= cost
            history.energySpent += cost
        }
    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            StateComponent::class,
        )
    }

    override fun writes(): List<KClass<*>> {
        return listOf(
            EnergyComponent::class,
            HistoryComponent::class,
        )
    }
}