package com.example.simulation.simulation.systems

import com.example.simulation.simulation.AgingStages
import com.example.simulation.simulation.CreatureStates
import com.example.simulation.simulation.Diets
import com.example.simulation.simulation.EnergyComponent
import com.example.simulation.simulation.EnergyDepletedEvent
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.HistoryComponent
import com.example.simulation.simulation.SizeComponent
import com.example.simulation.simulation.StateComponent
import com.example.simulation.simulation.VelocityComponent
import com.example.simulation.simulation.World
import kotlin.reflect.KClass

class EnergySystem(private val world: World) : System {
    val stateEnergyCost: Map<CreatureStates, Float> = mapOf(
        CreatureStates.HUNGER to 0.8f,
        CreatureStates.ROAMING to 2f,
        CreatureStates.IDLE to 0.2f,
    )

    val dietEnergyCost : Map<Diets, Float> = mapOf(
        Diets.HERBIVORE to 0.5f,
        Diets.CARNIVORE to 0f,
        Diets.OMNIVORE to 1f,
    )

    override fun update() {
        for ((id, energy) in world.energies) {
            val state = world.states[id] ?: continue
            val history = world.histories[id] ?: continue
            val diet = world.diets[id]?.diet ?: continue

            val age = world.ages[id] ?: continue
            val ageMultiplier = when (age.agingStage)

            {
                AgingStages.YOUNG -> 1.2f
                AgingStages.ADULT ->  1f
                else -> 1.2f
            }

            var cost = stateEnergyCost[state.state] ?: 0f
            cost += energy.metabolism * ageMultiplier * dietEnergyCost[diet]!!
//            cost = 0f
            energy.currentEnergy -= cost
            history.energySpent += cost

            checkIfDied(id, energy)
        }
    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            StateComponent::class,
            SizeComponent::class,
            VelocityComponent::class,
        )
    }

    override fun writes(): List<KClass<*>> {
        return listOf(
            EnergyComponent::class,
            HistoryComponent::class,
        )
    }

    private fun checkIfDied(id: EntityId, energy: EnergyComponent) {
        if (energy.currentEnergy <= 0) {
            world.eventBus.emit(EnergyDepletedEvent(id))
        }
    }
}