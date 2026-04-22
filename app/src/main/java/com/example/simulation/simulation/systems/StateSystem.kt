package com.example.simulation.simulation.systems

import com.example.simulation.simulation.CreatureStates
import com.example.simulation.simulation.EnergyComponent
import com.example.simulation.simulation.SimulationConfiguration.hungerStateEnergyThreshhold
import com.example.simulation.simulation.SimulationConfiguration.roamingStateEnergyThreshhold
import com.example.simulation.simulation.StateComponent
import com.example.simulation.simulation.World
import kotlin.reflect.KClass

class StateSystem(private val world: World) : System {
    override fun update() {
        for ((id, energy) in world.energies) {
            val state = world.states[id] ?: continue
            val canReproduce = world.reproductions[id]?.canReproduce ?: continue

            if (state.state == CreatureStates.DECAYING) continue
            state.state = when {
                canReproduce -> CreatureStates.REPRODUCTION
                energy.currentEnergy < hungerStateEnergyThreshhold -> CreatureStates.HUNGER
                energy.currentEnergy < roamingStateEnergyThreshhold -> CreatureStates.ROAMING
                else -> CreatureStates.ROAMING
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