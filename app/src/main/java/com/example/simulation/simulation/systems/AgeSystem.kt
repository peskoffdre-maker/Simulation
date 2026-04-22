package com.example.simulation.simulation.systems

import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.AgingStages
import com.example.simulation.simulation.CreatureStates
import com.example.simulation.simulation.DecayComponent
import com.example.simulation.simulation.EntityDiedOfAgeEvent
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.SimulationConfiguration.agingPerTick
import com.example.simulation.simulation.World
import kotlin.reflect.KClass

/**
 * Class responsible for managing ages of all entities.
 * If entity has aging stages, determines the stage.
 */
class AgeSystem(private val world: World) : System {
    override fun update() {
        for ((id, age) in world.ages) {
            age.value += agingPerTick
            checkIfDied(age,id)
            if (id in world.creatureTags) {
                handleCreatureAging(id)
            }
        }
    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            AgeComponent::class,
        )
    }

    override fun writes(): List<KClass<*>> {
        return listOf(
            AgeComponent::class
        )
    }

    private fun handleCreatureAging(id: EntityId) {
        val state = world.states[id] ?: return
        if (state.state == CreatureStates.DECAYING) {
            val decay = world.decays[id] ?: return
            decay.currentDecay--
            checkIfDecayed(id, decay)
        }
        val age = world.ages[id] ?: return
        determineAgeStage(age)
    }

    private fun determineAgeStage(age: AgeComponent) {
        val currentAge = age.value
        when {
            currentAge > age.youngAdult -> age.agingStage = AgingStages.ADULT
            currentAge > age.adultOld -> age.agingStage = AgingStages.OLD
        }
    }

    private fun checkIfDied(age: AgeComponent, id: EntityId) {

        if (age.value >= age.maxAge) {
            if (id in world.creatureTags) {
                world.eventBus.emit(EntityDiedOfAgeEvent(id))
            } else {
                world.deathQ.addLast(id)
            }
        }
    }

    private fun checkIfDecayed(id: EntityId, decay: DecayComponent) {
        if (decay.currentDecay <= 0) {
            world.deathQ.addLast(id)
        }
    }
}