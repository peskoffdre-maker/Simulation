package com.example.simulation.simulation.systems

import com.example.simulation.simulation.CreatureStates
import com.example.simulation.simulation.EnergyComponent
import com.example.simulation.simulation.StateComponent
import com.example.simulation.simulation.World
import kotlin.reflect.KClass

class StateSystem : System {
    override fun update(world: World, delta: Int) {
        for ((id, energy) in world.energies) {
            val state = world.states[id] ?: continue

            state.state = when {
                energy.energy < 1000f -> CreatureStates.HUNGER
                energy.energy < 1500f -> CreatureStates.ROAMING
                else -> CreatureStates.IDLE
            }
        }
    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            EnergyComponent::class,
        )
    }

    override fun writes(): List<KClass<*>> {
        return listOf(
            StateComponent:: class,
        )
    }
}